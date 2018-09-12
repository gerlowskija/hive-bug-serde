package com.helloworld.serde;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class HelloWorldWritable implements Writable {
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        // Unimplemented
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        // Unimplemented
    }
}
