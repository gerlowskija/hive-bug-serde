package com.helloworld.serde;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class HelloWorldInputFormat implements InputFormat<IntWritable, HelloWorldWritable> {

    @Override
    public InputSplit[] getSplits(JobConf jobConf, int i) throws IOException {
        return new InputSplit[] { new HelloWorldInputSplit() };
    }

    @Override
    public RecordReader<IntWritable, HelloWorldWritable> getRecordReader(InputSplit inputSplit, JobConf jobConf, Reporter reporter) throws IOException {
        return new HelloWordRecordReader();
    }

    /**
     * This Reader returns three hardcoded key-value pairs to any query.
     */
    class HelloWordRecordReader implements RecordReader<IntWritable, HelloWorldWritable> {
        private static final int NUM_RECORDS = 3;
        private final String[] RECORDS = new String[] { "HELLO", "THERE", "WORLD"};
        int numRetrieved = 0;


        @Override
        public boolean next(IntWritable intWritable, HelloWorldWritable helloWorldWritable) throws IOException {
            if (numRetrieved >= NUM_RECORDS) {
                return false;
            }

            intWritable.set(numRetrieved);
            helloWorldWritable.set(RECORDS[numRetrieved++]);

            return true;
        }

        @Override
        public IntWritable createKey() { return new IntWritable(); }

        @Override
        public long getPos() throws IOException {
            return numRetrieved;
        }

        @Override
        public float getProgress() throws IOException {
            return (float)numRetrieved / (float) NUM_RECORDS;
        }

        @Override
        public void close() throws IOException { /* do nothing */ }

        @Override
        public HelloWorldWritable createValue() {
            return new HelloWorldWritable(RECORDS[numRetrieved]);
        }
    }

    class HelloWorldInputSplit implements InputSplit {

        @Override
        public long getLength() throws IOException {
            return 1L;
        }

        @Override
        public String[] getLocations() throws IOException {
            return new String[] { "hello", "world"};
        }

        @Override
        public void write(DataOutput dataOutput) throws IOException {
            // Do nothing
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
            // Do nothing
        }
    }
}
