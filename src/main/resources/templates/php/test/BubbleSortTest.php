<?php
use PHPUnit\Framework\TestCase;

final class BubbleSortTest extends TestCase {

    public function test_bubble_ort() {
        $array = [5, 3, 0, 2, 1];
        $sorted = [0, 1, 2, 3, 5];

        $bs = new BubbleSort();
        $bs->perform_sort($input);

        $this->assertEquals($sorted, $array);
    }
}
