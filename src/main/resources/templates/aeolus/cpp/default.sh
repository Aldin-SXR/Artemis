#!/usr/bin/env bash
set -e

build_and_test_the_code () {
  echo '⚙️ executing build_and_test_the_code'
    
  sudo chown artemis_user:artemis_user .
  cp -r /opt/gtest gtest

  main_test_file='#include "gtest/gtest.h"
  '

  target_files='cpp run_all_tests.cpp '

  # Include all assignment files into CMakeLists
  for file in assignment/*.h assignment/*.cpp; do
    if [ -e "$file" ]; then
      target_files+="$file "
    fi
  done

  # Include all test files into CMakeLists, and include all test files into the main() test.
  for file in *.h *.cpp; do
    if [ -e "$file" ]; then
      target_files+="$file "
      if [ "$file" != "run_all_tests.cpp" ]; then
        main_test_file+="#include \"$file\"
      "
      fi
    fi
  done

  cmake_list="cmake_minimum_required(VERSION 3.27.8)
  project(cpp)

  set(CMAKE_CXX_STANDARD 17)

  enable_testing()
  add_subdirectory(./gtest)
  include_directories(./gtest/googletest/include)
  include_directories(./gtest/googlemock/include)

  add_executable($target_files)

  target_link_libraries(cpp PRIVATE gtest)

  include(GoogleTest)"

  main_test_file+='
  int main(int argc, char* argv[]) {
      ::testing::InitGoogleTest(&argc, argv);
      return RUN_ALL_TESTS();
  }'

  echo "$cmake_list" > CMakeLists.txt
  echo "$main_test_file" > run_all_tests.cpp

  # Build the project
  mkdir build
  cd build
  cmake ..
  make
  ./cpp --gtest_output=xml:../test-reports/results.xml
}

main () {
  build_and_test_the_code
}

main "${@}"