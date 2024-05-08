#include "BubbleSort.h"

/**
* Sorts integers with BubbleSort.
*
* @param std::vector<int> &input the vector of integers to be sorted
*/
void BubbleSort::perform_sort(std::vector<int> &input) {
    int n = input.size();
    for (int i = n - 1; i >= 0; i--) {
        for (int j = 0; j < i; j++) {
            if (input[j] > input[j + 1]) {
                std::swap(input[j], input[j + 1]);
            }
        }
    }
}