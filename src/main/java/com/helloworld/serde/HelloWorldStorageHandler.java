package com.helloworld.serde;

import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.hive.ql.metadata.DefaultStorageHandler;
import org.apache.hadoop.hive.serde2.AbstractSerDe;

public class HelloWorldStorageHandler extends DefaultStorageHandler {
    public HelloWorldStorageHandler() {

    }

    @Override
    public Class<? extends AbstractSerDe> getSerDeClass() {
        return HelloWorldSerDe.class;
    }

    @Override
    public Class<? extends OutputFormat> getOutputFormatClass() {
        return HelloWorldOutputFormat.class;
    }
}
