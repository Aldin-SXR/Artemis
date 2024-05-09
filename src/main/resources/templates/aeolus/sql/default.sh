#!/usr/bin/env bash
set -e

build_and_test_the_code () {
  echo '⚙️ executing build_and_test_the_code'
  
  cp -r /opt/dbunit-runner .

  cp assignment/*.sql dbunit-runner/src/test/resources/assignment/
  cp *.sql dbunit-runner/src/test/resources/
  cp *.xml dbunit-runner/src/test/resources/

  java_file="public class DbUnitTest extends BaseDbUnitTest {
  "
  
  for file in *.sql; do
      if [ "$file" != "schema.sql" ]; then
          file=$(basename $file .sql)
          java_file+="public void test_${file}() throws Exception {
          sqlFiles(\"${file}\", \"${file}\");
      }
      "
      fi
  done
  java_file+="}"
  
  echo $java_file > dbunit-runner/src/test/java/DbUnitTest.java

  cd dbunit-runner && mvn clean test
}

main () {
  build_and_test_the_code
}

main "${@}"
