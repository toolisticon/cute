./mvnw -B -Darguments="-Dmaven.deploy.skip=true" -Dmaven.test.skip=true -Dresume=false -DreleaseVersion=$1 -DdevelopmentVersion=$2 release:prepare release:perform
