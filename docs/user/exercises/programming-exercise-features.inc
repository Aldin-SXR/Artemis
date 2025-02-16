.. _programming_features:

Artemis and its version control and continuous integration infrastructure is independent of the programming language and thus supports
teaching and learning with any programming language that can be compiled and tested on the command line.
Instructors have a lot of freedom in defining the environment (e.g. using build agents and Docker images) in which student code is executed and tested.
To simplify the setup of programming exercises, Artemis supports several ``templates`` that show how the setup works.
Instructors can still use those templates to generate programming exercises and then adapt and customize the settings in the repositories and build plans.


- The support for a specific programming language ``templates`` depends on the used ``continuous integration`` system. The table below gives an overview:

  +----------------------+----------+---------+
  | Programming Language | Local CI | Jenkins |
  +======================+==========+=========+
  | Java                 | yes      | yes     |
  +----------------------+----------+---------+
  | Python               | yes      | yes     |
  +----------------------+----------+---------+
  | C                    | yes      | yes     |
  +----------------------+----------+---------+
  | Haskell              | yes      | yes     |
  +----------------------+----------+---------+
  | Kotlin               | yes      | yes     |
  +----------------------+----------+---------+
  | VHDL                 | yes      | no      |
  +----------------------+----------+---------+
  | Assembler            | yes      | no      |
  +----------------------+----------+---------+
  | Swift                | yes      | yes     |
  +----------------------+----------+---------+
  | OCaml                | yes      | no      |
  +----------------------+----------+---------+

- Not all ``templates`` support the same feature set and supported features can also change depending on the continuous integration system setup.
  Depending on the feature set, some options might not be available during the creation of the programming exercise.
  The table below provides an overview of the supported features.
  In case a feature has different support for different continuous integration systems, the table shows the differences between Local CI **(L)** and Jenkins **(J)**.
  (Note that Gitlab CI is experimental and therefore has a limited set of features which are not mentioned here to keep the overview simpler.)

  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | Programming Language | Sequential Test Runs | Static Code Analysis | Plagiarism Check    | Package Name | Project Type                             | Solution Repository Checkout | Testwise Coverage Analysis | Auxiliary repositories |
  +======================+======================+======================+=====================+==============+==========================================+==============================+============================+========================+
  | Java                 | yes                  | yes                  | yes                 | yes          | Gradle, Maven                            | no                           | J: yes; L: no              | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | Python               | L: yes; J: no        | no                   | yes                 | no           | n/a                                      | no                           | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | C                    | no                   | L: yes; J: no        | yes                 | no           | FACT, GCC                                | no                           | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | C (FACT framework)   | no                   | L: yes; J: no        | yes                 | no           | n/a                                      | no                           | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | Haskell              | L: yes; J: no        | no                   | no                  | no           | n/a                                      | L: yes; J: no                | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | Kotlin               | yes                  | no                   | yes                 | yes          | n/a                                      | no                           | J: yes; L: no              | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | VHDL                 | no                   | no                   | no                  | no           | n/a                                      | no                           | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | Assembler            | no                   | no                   | no                  | no           | n/a                                      | no                           | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | Swift                | no                   | yes                  | yes                 | yes          | Plain (Xcode: not supported)             | no                           | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+
  | OCaml                | no                   | no                   | no                  | no           | n/a                                      | yes                          | no                         | L: yes, J: no          |
  +----------------------+----------------------+----------------------+---------------------+--------------+------------------------------------------+------------------------------+----------------------------+------------------------+

  - *Sequential Test Runs*: ``Artemis`` can generate a build plan which first executes structural and then behavioral tests. This feature can help students to better concentrate on the immediate challenge at hand.
  - *Static Code Analysis*: ``Artemis`` can generate a build plan which additionally executes static code analysis tools.
    ``Artemis`` categorizes the found issues and provides them as feedback for the students. This feature makes students aware of code quality issues in their submissions.
  - *Plagiarism Checks*: ``Artemis`` is able to automatically calculate the similarity between student submissions. A side-by-side view of similar submissions is available to confirm the plagiarism suspicion.
  - *Package Name*: A package name has to be provided
  - *Solution Repository Checkout*: Instructors are able to compare a student submission against a sample solution in the solution repository
  - *Testwise Coverage Analysis*: ``Artemis`` can generate a build plan which additionally executes a testwise coverage analysis.
    ``Artemis`` aggregates the recorded data into different metrics. This feature allows instructors to check which code in the solution submission is how often executed by the test cases.

.. note::
  Only some ``templates`` for ``LocalCI`` support ``Sequential Test Runs`` at the moment.

.. note::
  Static Code Analysis for ``C`` exercises is only supported for ``LocalCI`` at the moment.

.. note::
  Testwise Coverage Analysis is currently not supported, but will be again supported in future versions.

.. note::
  Instructors are still able to extend the generated programming exercises with additional features that are not available in one specific template.

.. note::
  Documentation for FACT framework can be found here: https://fact.readthedocs.io/en/latest/

We encourage instructors to contribute improvements to the existing ``templates`` or to provide new templates. Please contact Stephan Krusche and/or create Pull Requests in the GitHub repository.

