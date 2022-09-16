mysql -u root -prootpswd < db_scripts/createDB.sql
mysql -u root -prootpswd < db_scripts/fillDBForTest.sql
CALL mvnw.cmd clean install -f pom.xml
java -jar target\SenlaFinalProject-1.0-SNAPSHOT.jar
pause