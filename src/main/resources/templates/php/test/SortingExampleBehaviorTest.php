<?php
use PHPUnit\Framework\TestCase;

final class SortingExampleBehaviorTest extends TestCase {

    private $dates;
    private $datesWithCorrectOrder;

    public function setUp(): void {
        $dateFormat = "d.m.Y";
        $date1 = \DateTime::createFromFormat($dateFormat, "08.11.2018")->format('Y-m-d');
        $date2 = \DateTime::createFromFormat($dateFormat, "15.04.2017")->format('Y-m-d');
        $date3 = \DateTime::createFromFormat($dateFormat, "15.02.2016")->format('Y-m-d');
        $date4 = \DateTime::createFromFormat($dateFormat, "15.09.2017")->format('Y-m-d');

        $this->dates = [$date1, $date2, $date3, $date4];
        $this->datesWithCorrectOrder = [$date3, $date2, $date4, $date1];
    }

    public function testBubbleSort() {
        $bubbleSort = new BubbleSort();
        $bubbleSort->performSort($this->dates);
        $this->assertEquals($this->datesWithCorrectOrder, $this->dates);
    }

    public function testMergeSort() {
        $mergeSort = new MergeSort();
        $mergeSort->performSort($this->dates);
        $this->assertEquals($this->datesWithCorrectOrder, $this->dates);
    }

    public function testUseMergeSortForBigList() {
        $bigList = [];
        for ($i = 0; $i < 11; $i++) {
            $bigList[] = new \DateTime();
        }
        $chosenSortStrategy = $this->configurePolicyAndContext($bigList);
        $this->expectNotToPerformAssertions();
        if (!($chosenSortStrategy instanceof MergeSort)) {
            $this->fail("The sort algorithm of Context was not MergeSort for a list with more than 10 dates.");
        }
    }

    public function testUseBubbleSortForSmallList() {
        $smallList = [];
        for ($i = 0; $i < 3; $i++) {
            $smallList[] = new \DateTime();
        }
        $chosenSortStrategy = $this->configurePolicyAndContext($smallList);
        $this->expectNotToPerformAssertions();
        if (!($chosenSortStrategy instanceof BubbleSort)) {
            $this->fail("The sort algorithm of Context was not BubbleSort for a list with less or equal than 10 dates.");
        }
    }

    private function configurePolicyAndContext($dates) {
        $context = new Context();
        $context->setDates($dates);

        $policy = new Policy($context);
        $policy->configure();

        $chosenSortStrategy = $context->getSortAlgorithm();
        return $chosenSortStrategy;
    }
}