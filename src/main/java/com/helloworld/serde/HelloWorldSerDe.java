package com.helloworld.serde;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.AbstractSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.*;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Writable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldSerDe extends AbstractSerDe {

    public HelloWorldSerDe() {

    }

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldSerDe.class);

    protected List<String> colNames;
    protected List<TypeInfo> colTypes;
    protected StructTypeInfo typeInfo;
    protected ObjectInspector inspector;

    @Override
    public Class<? extends Writable> getSerializedClass() {
        return HelloWorldWritable.class;
    }

    @Override
    public void initialize(Configuration conf, Properties tblProperties) throws SerDeException {
        colNames = Arrays.asList(new String[] {"hello_col", "world_col"});
        colTypes = new ArrayList<TypeInfo>();
        colTypes.add(TypeInfoFactory.getCharTypeInfo(100));
        colTypes.add(TypeInfoFactory.getCharTypeInfo(100));

        typeInfo = (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(colNames, colTypes);
        inspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);
    }

    public void heyLookAtMe() {}

    @Override
    public Object deserialize(Writable data) throws SerDeException {
        LOG.warn("JEGERLOW We're in deserialize!");
        if (!(data instanceof HelloWorldWritable)) {
            LOG.warn("JEGERLOW: Received unexpected Writable class.  Expected {} from classloader {}, but actually was {} from classloader {}",
                    HelloWorldWritable.class.getCanonicalName(), HelloWorldWritable.class.getClassLoader().toString(),
                    data.getClass().getCanonicalName(), data.getClass().getClassLoader().toString());
            return null;
        }

        final List<Object> row = new ArrayList<Object>();
        row.add("Hello_value");
        row.add("World_value");
        return row;
    }

    @Override
    public Writable serialize(Object data, ObjectInspector objInspector) throws SerDeException {
        // Unimplemented. Return any old writable.
        return new HelloWorldWritable();
    }

    @Override
    public SerDeStats getSerDeStats() {
        // Nothing for now
        return null;
    }

    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return inspector;
    }
}