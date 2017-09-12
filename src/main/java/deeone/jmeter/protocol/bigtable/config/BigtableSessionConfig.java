package deeone.jmeter.protocol.bigtable.config;

import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.config.RetryOptions;
import com.google.cloud.bigtable.grpc.BigtableDataClient;
import com.google.cloud.bigtable.grpc.BigtableSession;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.TestBeanHelper;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//http://codyaray.com/2014/07/custom-jmeter-samplers-and-config-elements
public class BigtableSessionConfig extends AbstractTestElement
        implements ConfigElement, TestStateListener, TestBean {

    private static final Logger log = LoggerFactory.getLogger(BigtableSessionConfig.class);
    private String projectId;
    private String instanceId;
    private Integer dataChannelCount;
    private transient BigtableSession session;

    public void addConfigElement(ConfigElement configElement) {

    }

    public boolean expectsModification() {
        return false;
    }

    public void testStarted() {
        this.setRunningVersion(true);
        TestBeanHelper.prepare(this);
        JMeterVariables variables = getThreadContext().getVariables();

        final BigtableOptions.Builder builder = new BigtableOptions.Builder();

        builder.setProjectId(projectId)
               .setInstanceId(instanceId)
               .setDataChannelCount(dataChannelCount)
               .setUserAgent(this.getClass().getSimpleName())
               .setRetryOptions(new RetryOptions.Builder().build());

        if (dataChannelCount != null & dataChannelCount > 0) {
            builder.setDataChannelCount(dataChannelCount);
        }

        BigtableOptions bigtableOptions = builder.build();

        try {
            session = new BigtableSession(bigtableOptions);
            synchronized(this){
                variables.putObject("bigTableSession", session);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testStarted(String host) {
        testStarted();
    }

    public void testEnded() {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testEnded(String host) {
        testEnded();
    }

    public static BigtableDataClient getDataClient() throws IOException {
        final String poolName = "bigTableSession";
        Object sessionObject =
                JMeterContextService.getContext().getVariables().getObject(poolName);
        if (sessionObject == null) {
            throw new IOException("No pool found named: '" + poolName + "', ensure Variable Name matches Variable Name of JDBC Connection Configuration");
        } else {
            if(sessionObject instanceof BigtableSession) {
                BigtableSession pool = (BigtableSession) sessionObject;
                return pool.getDataClient();
            } else {
                String errorMsg = "Found object stored under variable:'"+poolName
                        +"' with class:"+sessionObject.getClass().getName()+" and value: '"+sessionObject+" but it's not a DataSourceComponent, check you're not already using this name as another variable";
                log.error(errorMsg);
                throw new IOException(errorMsg);
            }
        }
    }

    public synchronized String getProjectId() {
        return projectId;
    }

    public synchronized  void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public synchronized  String getInstanceId() {
        return instanceId;
    }

    public synchronized  void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public synchronized Integer getDataChannelCount() {
        return dataChannelCount;
    }

    public synchronized void setDataChannelCount(Integer dataChannelCount) {
        this.dataChannelCount = dataChannelCount;
    }

}