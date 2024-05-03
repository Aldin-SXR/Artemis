<?php

class Context {
    private $sortAlgorithm;
    private $dates;

    public function getDates() {
        return $this->dates;
    }

    public function setDates($dates) {
        $this->dates = $dates;
    }

    public function setSortAlgorithm($sa) {
        $this->sortAlgorithm = $sa;
    }

    public function getSortAlgorithm() {
        return $this->sortAlgorithm;
    }

    /**
     * Runs the configured sort algorithm.
     */
    public function sort() {
        if ($this->sortAlgorithm !== null) {
            $this->sortAlgorithm->performSort($this->dates);
        }
    }
}