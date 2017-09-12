package deeone.jmeter.protocol.bigtable.config;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;

public class BigtableSessionConfigBeanInfo extends BeanInfoSupport {

    private static final Logger log = LoggerFactory.getLogger(BigtableSessionConfigBeanInfo.class);
    private static final String PROJECT_ID = "projectId";
    private static final String INSTANCE_ID = "instanceId";

    public BigtableSessionConfigBeanInfo() {
        super(BigtableSessionConfig.class);

        PropertyDescriptor p = property(PROJECT_ID);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);

        p = property(INSTANCE_ID);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);


    }
}
