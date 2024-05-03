<?php

interface SortStrategy {
    /**
     * Sorts a list of Dates.
     *
     * @param array $input list of Dates
     */
    public function performSort(array &$input);
}