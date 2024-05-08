#ifndef CPP_BUBBLESORTTEST_H
#define CPP_BUBBLESORTTEST_H

#include "gtest/gtest.h"
#include "assignment/BubbleSort.h"

TEST(BubbleSort, SortIntegers) {
    std::vector<int> vect{ 5, 1, -3, 8, 4, 9 };
    std::vector<int> sorted{ -3, 1, 4, 5, 8, 9 };

    BubbleSort bs = BubbleSort();
    bs.perform_sort(vect);

    for (int i = 0; i < vect.size(); i++) {
        EXPECT_EQ(vect[i], sorted[i]) << "Expected <" << sorted[i] << ">, got <" << vect[i] << "> at index " << i;
    }
}

#endif //CPP_BUBBLESORTTEST_H
