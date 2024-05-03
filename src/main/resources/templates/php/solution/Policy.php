<?php

class Policy {

    private const DATES_SIZE_THRESHOLD = 10;

    private $context;

    public function __construct($context) {
        $this->context = $context;
    }

    /**
     * Chooses a strategy depending on the number of date objects.
     */
    public function configure() {
        if (count($this->context->getDates()) > self::DATES_SIZE_THRESHOLD) {
            echo "More than " . self::DATES_SIZE_THRESHOLD . " dates, choosing merge sort!" . PHP_EOL;
            $this->context->setSortAlgorithm(new MergeSort());
        } else {
            echo "Less or equal than " . self::DATES_SIZE_THRESHOLD . " dates, choosing bubble sort!" . PHP_EOL;
            $this->context->setSortAlgorithm(new BubbleSort());
        }
    }
}