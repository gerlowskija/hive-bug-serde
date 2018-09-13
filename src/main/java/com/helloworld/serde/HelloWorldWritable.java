package com.helloworld.serde;

import org.apache.hadoop.io.Text;

public class HelloWorldWritable extends Text {
    public HelloWorldWritable(String value) {
        super(value);
    }
}
