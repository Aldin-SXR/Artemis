#!/usr/bin/env bash
set -e

build_and_test_the_code () {
  echo '⚙️ executing build_and_test_the_code'

  bootstrap="<?php
  spl_autoload_register(function (\$class_name) {
    include './assignment/'. \$class_name . '.php';
  });"

  echo "$bootstrap" > bootstrap.php

  phpunit --bootstrap bootstrap.php --verbose --log-junit test-reports/results.xml .
}

main () {
  build_and_test_the_code
}

main "${@}"
