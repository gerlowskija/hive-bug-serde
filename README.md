## "Hello World" Storage Handler

The storage handler provided by this project is unfit for use for any purpose.  It was created solely to investigate a Hive bug that we encountered in a more complex [serde](https://github.com/lucidworks/hive-solr).  We wanted to see if the bug could be reproduced in a simpler SerDe as a means of showing that the bug was unrelated to the custom SerDe code, and was an issue in Hive more generally.

### The Initial Symptoms

This problem came to our attention when we noticed that a custom [serde](https://github.com/lucidworks/hive-solr) would occasionally display 'NULL' as the value for each position of a result set from queries made on external/non-native tables.  Example result-sets looked like:

```
+-----------------------+-----------------------+
| helloworld.hello_col  | helloworld.world_col  |
+-----------------------+-----------------------+
| NULL                  | NULL                  |
| NULL                  | NULL                  |
| NULL                  | NULL                  |
+-----------------------+-----------------------+
```

Only queries that ran on hiveserver2 seemed to be affected.  Queries which ran as part of Tez tasks were not.  The behavior was also affected by previous query history.  The behavior was only seen on queries made after INSERTing or altering data in the table.  The NULL behavior would then continue until a restart of hiveserver2, at which point queries went back to returning the expected values.  Lastly, we also noticed that the behavior only occurred when our custom JAR was loaded via the `ADD JAR /path/to/jar` command.  If our custom serde jar was loaded through Hive's `hive.aux.jars.path` option, the bug went away entirely.

### Investigation

Tracing the issue in our SerDe, we found that the NULL values were coming from a type check (`if (!(data instanceof ExpectedWritableType))`) in our SerDe's `deserialize()` method.  We added some logging there, and found that the operands to this type check were comparing the same class from two different instances of "UDFClassLoader". The behavior is exactly that warned against in this comment on [HIVE-11878](https://issues.apache.org/jira/browse/HIVE-11878?focusedCommentId=14876858&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-14876858). While we don't know quite enough about Hive's classloader setup to understand where the problem is, it seems that any custom StorageHandler that (1) performs either casting or explicit type checking, and (2) is loaded dynamically via `ADD JAR` is subject to the classpath-collision errors we're seeing.  Possibly since Hive 2.0.0, though we've only tested this in Hive 3.0.0.

To assist in identifying/fixing the bug, and to rule out any lingering suspicions that our SerDe was contributing to the issue, we decided to try our hand at a minimal reproduction of the problem, which you're reading now.

### Reproducing the Bug

Conceptually, all you need to do to reproduce this issue is (1) build this minimal reproduction SerDe, and (2) use it to index and then query an external table that uses it.

To make this a little easier, we've included some example commands below which can be used to trigger the issue.  If you rely on these commands, please note:
  - the commands below rely on a local data file called `books.csv`.  Before running the script, copy the `books.csv` in this repository to `/tmp` on any/all relevant nodes in your cluster.
  - the commands below also rely on the StorageHandler jar being present on your cluster.  Before running the script, build the repo with `./gradlew --info clean shadowJar` and copy the resulting artifact (`build/libs/helloworld-serde-1.0-SNAPSHOT.jar`) to the relevant nodes of your cluster.  (The commands below assume this jar lives on your cluster as `/tmp/helloworld-serde.jar`)

```
external_table_name="external_table"

jar_location="/tmp/helloworld-serde.jar"
hive_user="yourHiveUser"
hive_pass="yourHivePassword"

hive -n $hive_user -p $hive_pass -e "CREATE TABLE books (id STRING, cat STRING, title STRING, price FLOAT, in_stock BOOLEAN, author STRING, series STRING, seq INT, genre STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;"
hive -n $hive_user -p $hive_pass -e "LOAD DATA LOCAL INPATH '/tmp/books.csv' OVERWRITE INTO TABLE books;"
hive -n $hive_user -p $hive_pass -e "ALTER TABLE books SET TBLPROPERTIES ("skip.header.line.count"="1");"
hive -n $hive_user -p $hive_pass -e "ADD JAR $jar_location; CREATE EXTERNAL TABLE $external_table_name (hello_col STRING, world_col STRING) STORED BY 'com.helloworld.serde.HelloWorldStorageHandler' LOCATION '/tmp/$external_table_name';"

# This command will succeed and print out a table with 3 results with suitably helloworld values
hive -n $hive_user -p $hive_pass -e "ADD JAR $jar_location; SELECT * FROM $external_table_name;"
# This command will 'succeed' though really it's not doing any real work, as the table entries are hardcoded in the implementation
hive -n $hive_user -p $hive_pass -e "ADD JAR $jar_location; INSERT OVERWRITE TABLE $external_table_name SELECT id, cat FROM books b;"
# This command will fail and produce a table where each entry is just the word 'NULL'
hive -n $hive_user -p $hive_pass -e "ADD JAR $jar_location; SELECT * FROM $external_table_name;"
```


