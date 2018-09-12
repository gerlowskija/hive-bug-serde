package com.helloworld.serde;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator;
import org.apache.hadoop.hive.ql.io.HiveOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator.RecordWriter;

import java.io.IOException;
import java.util.Properties;

public class HelloWorldOutputFormat implements HiveOutputFormat<Text, HelloWorldWritable> {

    @Override
    public FileSinkOperator.RecordWriter getHiveRecordWriter(JobConf jc, Path finalOutPath, Class<? extends Writable> valueClass, boolean isCompressed, Properties tableProperties, Progressable progress) throws IOException {
        return new RecordWriter() {
            @Override
            public void write(Writable w) throws IOException {

            }

            @Override
            public void close(boolean abort) throws IOException {

            }
        };
    }

    @Override
    public org.apache.hadoop.mapred.RecordWriter<Text, HelloWorldWritable> getRecordWriter(FileSystem fileSystem, JobConf jobConf, String s, Progressable progressable) throws IOException {
        return null;
    }

    @Override
    public void checkOutputSpecs(FileSystem fileSystem, JobConf jobConf) throws IOException {

    }
}
