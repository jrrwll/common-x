rem on windows host

rem .\\gradlew.bat :common-conv:example-jacob:bootJar

java -Xss1M -Xmx500M -server ^
    -Duser.timezone=GMT+08  ^
    -Dfile.encoding=utf-8 ^
    -Dserver.port=8080 ^
    -jar jacob-0.1.jar
