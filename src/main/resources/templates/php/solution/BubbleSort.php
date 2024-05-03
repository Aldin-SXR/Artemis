<?php

class BubbleSort implements SortStrategy {

    /**
     * Sorts dates with BubbleSort.
     *
     * @param array $input the array of Dates to be sorted
     */
    public function performSort(array &$input) {
        $n = count($input);
        for ($i = $n - 1; $i >= 0; $i--) {
            for ($j = 0; $j < $i; $j++) {
                if ($input[$j] > $input[$j + 1]) {
                    $temp = $input[$j];
                    $input[$j] = $input[$j + 1];
                    $input[$j + 1] = $temp;
                }
            }
        }
    }
}