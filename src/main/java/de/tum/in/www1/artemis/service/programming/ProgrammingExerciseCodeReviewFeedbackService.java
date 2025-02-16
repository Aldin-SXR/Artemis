package de.tum.in.www1.artemis.service.programming;

import static de.tum.in.www1.artemis.config.Constants.PROFILE_CORE;
import static java.time.ZonedDateTime.now;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import de.tum.in.www1.artemis.domain.Feedback;
import de.tum.in.www1.artemis.domain.ProgrammingExercise;
import de.tum.in.www1.artemis.domain.ProgrammingSubmission;
import de.tum.in.www1.artemis.domain.Result;
import de.tum.in.www1.artemis.domain.enumeration.AssessmentType;
import de.tum.in.www1.artemis.domain.enumeration.FeedbackType;
import de.tum.in.www1.artemis.domain.participation.ProgrammingExerciseStudentParticipation;
import de.tum.in.www1.artemis.repository.ProgrammingExerciseStudentParticipationRepository;
import de.tum.in.www1.artemis.repository.ResultRepository;
import de.tum.in.www1.artemis.service.ResultService;
import de.tum.in.www1.artemis.service.SubmissionService;
import de.tum.in.www1.artemis.service.connectors.athena.AthenaFeedbackSuggestionsService;
import de.tum.in.www1.artemis.service.notifications.GroupNotificationService;
import de.tum.in.www1.artemis.web.rest.errors.BadRequestAlertException;

/**
 * Service class for managing code review feedback on programming exercises.
 * This service handles the processing of feedback requests for programming exercises,
 * including automatic generation of feedback through integration with external services
 * such as Athena.
 */
@Profile(PROFILE_CORE)
@Service
public class ProgrammingExerciseCodeReviewFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(ProgrammingExerciseCodeReviewFeedbackService.class);

    public static final String NON_GRADED_FEEDBACK_SUGGESTION = "NonGradedFeedbackSuggestion:";

    private final GroupNotificationService groupNotificationService;

    private final Optional<AthenaFeedbackSuggestionsService> athenaFeedbackSuggestionsService;

    private final ProgrammingExerciseStudentParticipationRepository programmingExerciseStudentParticipationRepository;

    private final SubmissionService submissionService;

    private final ResultService resultService;

    private final ResultRepository resultRepository;

    private final ProgrammingExerciseParticipationService programmingExerciseParticipationService;

    private final ProgrammingMessagingService programmingMessagingService;

    public ProgrammingExerciseCodeReviewFeedbackService(GroupNotificationService groupNotificationService,
            Optional<AthenaFeedbackSuggestionsService> athenaFeedbackSuggestionsService, SubmissionService submissionService, ResultService resultService,
            ProgrammingExerciseStudentParticipationRepository programmingExerciseStudentParticipationRepository, ResultRepository resultRepository,
            ProgrammingExerciseParticipationService programmingExerciseParticipationService1, ProgrammingMessagingService programmingMessagingService) {
        this.groupNotificationService = groupNotificationService;
        this.athenaFeedbackSuggestionsService = athenaFeedbackSuggestionsService;
        this.submissionService = submissionService;
        this.resultService = resultService;
        this.programmingExerciseStudentParticipationRepository = programmingExerciseStudentParticipationRepository;
        this.resultRepository = resultRepository;
        this.programmingExerciseParticipationService = programmingExerciseParticipationService1;
        this.programmingMessagingService = programmingMessagingService;
    }

    /**
     * Handles the request for generating feedback for a programming exercise.
     * This method decides whether to generate feedback automatically using Athena,
     * or notify a tutor to manually process the feedback.
     *
     * @param exerciseId          the id of the programming exercise.
     * @param participation       the student participation associated with the exercise.
     * @param programmingExercise the programming exercise object.
     * @return ProgrammingExerciseStudentParticipation updated programming exercise for a tutor assessment
     */
    public ProgrammingExerciseStudentParticipation handleNonGradedFeedbackRequest(Long exerciseId, ProgrammingExerciseStudentParticipation participation,
            ProgrammingExercise programmingExercise) {
        if (this.athenaFeedbackSuggestionsService.isPresent()) {
            this.checkRateLimitOrThrow(participation);
            CompletableFuture.runAsync(() -> this.generateAutomaticNonGradedFeedback(participation, programmingExercise));
            return participation;
        }
        else {
            log.debug("tutor is responsible to process feedback request: {}", exerciseId);
            groupNotificationService.notifyTutorGroupAboutNewFeedbackRequest(programmingExercise);
            return setIndividualDueDateAndLockRepository(participation, programmingExercise, true);
        }
    }

    /**
     * Generates automatic non-graded feedback for a programming exercise submission.
     * This method leverages the Athena service to generate feedback based on the latest submission.
     *
     * @param participation       the student participation associated with the exercise.
     * @param programmingExercise the programming exercise object.
     */
    public void generateAutomaticNonGradedFeedback(ProgrammingExerciseStudentParticipation participation, ProgrammingExercise programmingExercise) {
        log.debug("Using athena to generate feedback request: {}", programmingExercise.getId());

        // athena takes over the control here
        var submissionOptional = programmingExerciseParticipationService.findProgrammingExerciseParticipationWithLatestSubmissionAndResult(participation.getId())
                .findLatestSubmission();
        if (submissionOptional.isEmpty()) {
            throw new BadRequestAlertException("No legal submissions found", "submission", "noSubmission");
        }
        var submission = submissionOptional.get();

        // save result and transmit it over websockets to notify the client about the status
        var automaticResult = this.submissionService.saveNewEmptyResult(submission);
        automaticResult.setAssessmentType(AssessmentType.AUTOMATIC_ATHENA);
        automaticResult.setRated(false);
        automaticResult.setScore(100.0);
        automaticResult.setSuccessful(null);
        automaticResult.setCompletionDate(ZonedDateTime.now().plusMinutes(5)); // we do not want to show dates without a completion date, but we want the students to know their
                                                                               // feedback request is in work
        automaticResult = this.resultRepository.save(automaticResult);

        try {

            setIndividualDueDateAndLockRepository(participation, programmingExercise, false);
            this.programmingMessagingService.notifyUserAboutNewResult(automaticResult, participation);
            // now the client should be able to see new result

            log.debug("Submission id: {}", submission.getId());

            var athenaResponse = this.athenaFeedbackSuggestionsService.orElseThrow().getProgrammingFeedbackSuggestions(programmingExercise, (ProgrammingSubmission) submission,
                    false);

            List<Feedback> feedbacks = athenaResponse.stream().filter(individualFeedbackItem -> individualFeedbackItem.filePath() != null)
                    .filter(individualFeedbackItem -> individualFeedbackItem.description() != null).map(individualFeedbackItem -> {
                        var feedback = new Feedback();
                        String feedbackText;
                        if (Objects.nonNull(individualFeedbackItem.lineStart())) {
                            if (Objects.nonNull(individualFeedbackItem.lineEnd()) && !individualFeedbackItem.lineStart().equals(individualFeedbackItem.lineEnd())) {
                                feedbackText = String.format(NON_GRADED_FEEDBACK_SUGGESTION + "File %s at lines %d-%d", individualFeedbackItem.filePath(),
                                        individualFeedbackItem.lineStart(), individualFeedbackItem.lineEnd());
                            }
                            else {
                                feedbackText = String.format(NON_GRADED_FEEDBACK_SUGGESTION + "File %s at line %d", individualFeedbackItem.filePath(),
                                        individualFeedbackItem.lineStart());
                            }
                            feedback.setReference(String.format("file:%s_line:%d", individualFeedbackItem.filePath(), individualFeedbackItem.lineStart()));
                        }
                        else {
                            feedbackText = String.format(NON_GRADED_FEEDBACK_SUGGESTION + "File %s", individualFeedbackItem.filePath());
                        }
                        feedback.setText(feedbackText);
                        feedback.setDetailText(individualFeedbackItem.description());
                        feedback.setHasLongFeedbackText(false);
                        feedback.setType(FeedbackType.AUTOMATIC);
                        feedback.setCredits(0.0);
                        return feedback;
                    }).toList();

            automaticResult.setSuccessful(true);
            automaticResult.setCompletionDate(ZonedDateTime.now());

            this.resultService.storeFeedbackInResult(automaticResult, feedbacks, true);

            this.programmingMessagingService.notifyUserAboutNewResult(automaticResult, participation);
        }
        catch (Exception e) {
            log.error("Could not generate feedback", e);
            automaticResult.setSuccessful(false);
            automaticResult.setCompletionDate(ZonedDateTime.now());
            this.resultRepository.save(automaticResult);
            this.programmingMessagingService.notifyUserAboutNewResult(automaticResult, participation);
        }
        finally {
            unlockRepository(participation, programmingExercise);
        }
    }

    /**
     * Sets an individual due date for a participation, locks the repository,
     * and invalidates previous results to prepare for new feedback.
     *
     * @param participation       the programming exercise student participation.
     * @param programmingExercise the associated programming exercise.
     */
    private ProgrammingExerciseStudentParticipation setIndividualDueDateAndLockRepository(ProgrammingExerciseStudentParticipation participation,
            ProgrammingExercise programmingExercise, boolean invalidatePreviousResults) {
        // The participations due date is a flag showing that a feedback request is sent
        participation.setIndividualDueDate(now());

        participation = programmingExerciseStudentParticipationRepository.save(participation);
        // Circumvent lazy loading after save
        participation.setParticipant(participation.getParticipant());
        programmingExerciseParticipationService.lockStudentRepositoryAndParticipation(programmingExercise, participation);

        if (invalidatePreviousResults) {
            var participationResults = participation.getResults();
            participationResults.forEach(participationResult -> participationResult.setRated(false));
            this.resultRepository.saveAll(participationResults);
        }

        return participation;
    }

    /**
     * Removes the individual due date for a participation. If the due date for an exercise is empty or is in the future, unlocks the repository,
     *
     * @param participation       the programming exercise student participation.
     * @param programmingExercise the associated programming exercise.
     */
    private void unlockRepository(ProgrammingExerciseStudentParticipation participation, ProgrammingExercise programmingExercise) {
        if (programmingExercise.getDueDate() == null || now().isBefore(programmingExercise.getDueDate())) {
            programmingExerciseParticipationService.unlockStudentRepositoryAndParticipation(participation);
            participation.setIndividualDueDate(null);
            this.programmingExerciseStudentParticipationRepository.save(participation);
        }
    }

    private void checkRateLimitOrThrow(ProgrammingExerciseStudentParticipation participation) {

        List<Result> athenaResults = participation.getResults().stream().filter(result -> result.getAssessmentType() == AssessmentType.AUTOMATIC_ATHENA).toList();

        long countOfAthenaResultsInProcessOrSuccessful = athenaResults.stream().filter(result -> result.isSuccessful() == null || result.isSuccessful() == Boolean.TRUE).count();

        long countOfSuccessfulRequests = athenaResults.stream().filter(result -> result.isSuccessful() == Boolean.TRUE).count();

        if (countOfAthenaResultsInProcessOrSuccessful >= 3) {
            throw new BadRequestAlertException("Cannot send additional AI feedback requests now. Try again later!", "participation", "preconditions not met");
        }
        if (countOfSuccessfulRequests >= 3) {
            throw new BadRequestAlertException("Maximum number of AI feedback requests reached.", "participation", "preconditions not met");
        }
    }
}
