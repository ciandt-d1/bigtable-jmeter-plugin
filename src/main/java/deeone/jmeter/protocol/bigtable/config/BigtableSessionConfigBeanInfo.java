package deeone.jmeter.protocol.bigtable.config;

import com.google.cloud.bigtable.config.BigtableOptions;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;

public class BigtableSessionConfigBeanInfo extends BeanInfoSupport {

    private static final Logger log = LoggerFactory.getLogger(BigtableSessionConfigBeanInfo.class);

    private static final String PROJECT_ID = "projectId";
    private static final String INSTANCE_ID = "instanceId";
    private static final String DATA_CHANNEL_COUNT = "dataChannelCount";


    public BigtableSessionConfigBeanInfo() {
        super(BigtableSessionConfig.class);

        createPropertyGroup("bigtable_session_config",             //$NON-NLS-1$
                new String[] { PROJECT_ID, INSTANCE_ID });

        PropertyDescriptor p = property(PROJECT_ID);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);

        p = property(INSTANCE_ID);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);

//        p = property(DATA_CHANNEL_COUNT);
//        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
//        p.setValue(DEFAULT, null);
//        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
    }
}
