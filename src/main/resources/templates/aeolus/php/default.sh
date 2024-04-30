#!/usr/bin/env bash
set -e

build_and_test_the_code () {
  echo '⚙️ executing build_and_test_the_code'
  phpunit --verbose --log-junit test-reports/results.xml .
}

main () {
  build_and_test_the_code
}

main "${@}"
