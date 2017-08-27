# MiniSQLEngine
# Developer Name: Suraj Kannayyagari
# Language used 'JAVA'
# 'SQL Engine' is source code for the application

# jsqlparser is used to parse the query
# following are the steps required to run the queries.

# 1. Open command prompt on this folder
# 2. Run the following command to run a query.

# java -jar MiniSQLEngine.jar "<query>"

java -jar MiniSQLEngine.jar "select * from table1"
java -jar MiniSQLEngine.jar "select * from table2"
java -jar MiniSQLEngine.jar "select A,B,C,D from table1,table2"
java -jar MiniSQLEngine.jar "select A,B,C,D from table1,table2 where A<0 AND B>311"

# etc




# all the queries are supported which are mentioned.
# files are in 'src/files' folder. You can replace data(csv files) there and test the application.
