<?php

class MergeSort implements SortStrategy {
    /**
     * Wrapper method for the real MergeSort algorithm.
     *
     * @param array $input the array of Dates to be sorted
     */
    public function performSort(array &$input) {
        $this->mergesort($input, 0, count($input) - 1);
    }

    /**
     * Recursive merge sort method
     */
    private function mergesort(array &$input, int $low, int $high) {
        if ($high - $low < 1) {
            return;
        }
        $mid = (int)(($low + $high) / 2);
        $this->mergesort($input, $low, $mid);
        $this->mergesort($input, $mid + 1, $high);
        $this->merge($input, $low, $mid, $high);
    }

    /**
     * Merge method
     */
    private function merge(array &$input, int $low, int $middle, int $high) {

        $temp = [];
        $leftIndex = $low;
        $rightIndex = $middle + 1;
        $wholeIndex = 0;
        while ($leftIndex <= $middle && $rightIndex <= $high) {
            if ($input[$leftIndex] <= $input[$rightIndex]) {
                $temp[$wholeIndex] = $input[$leftIndex++];
            } else {
                $temp[$wholeIndex] = $input[$rightIndex++];
            }
            $wholeIndex++;
        }
        if ($leftIndex <= $middle && $rightIndex > $high) {
            while ($leftIndex <= $middle) {
                $temp[$wholeIndex++] = $input[$leftIndex++];
            }
        } else {
            while ($rightIndex <= $high) {
                $temp[$wholeIndex++] = $input[$rightIndex++];
            }
        }
        for ($wholeIndex = 0; $wholeIndex < count($temp); $wholeIndex++) {
            $input[$wholeIndex + $low] = $temp[$wholeIndex];
        }
    }
}