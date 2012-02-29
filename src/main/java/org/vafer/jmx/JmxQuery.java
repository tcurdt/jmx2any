package org.vafer.jmx;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class JmxQuery implements Iterable<JmxQuery.JmxBean> {

    private final JMXConnector connector;
    private final MBeanServerConnection connection;
    private final Collection<JmxBean> mbeans;

    public interface JmxAttribute {

        public ObjectName getBeanName();
        public String getAttributeName();
        public Object getAttributeValue() throws InstanceNotFoundException, IOException, AttributeNotFoundException, ReflectionException, MBeanException;

    }

    public final class JmxBean implements Iterable<JmxQuery.JmxAttribute> {

        private final Collection<JmxAttribute> attributes;

        public JmxBean(ObjectInstance mbean) throws IntrospectionException, InstanceNotFoundException, IOException, ReflectionException {
            final ObjectName mbeanName = mbean.getObjectName();
            final MBeanInfo mbeanInfo = connection.getMBeanInfo(mbeanName);
            
            final Collection<JmxAttribute> attributes = new ArrayList<JmxAttribute>();            
            for (final MBeanAttributeInfo attribute : mbeanInfo.getAttributes()) {
                if (attribute.isReadable()) {
                    attributes.add(new JmxAttribute() {
                        private Object value;
                        public ObjectName getBeanName() {
                            return mbeanName;
                        }
                        public String getAttributeName() {
                            return attribute.getName();
                        }
                        public Object getAttributeValue() throws InstanceNotFoundException, IOException, AttributeNotFoundException, ReflectionException, MBeanException {
                            if (value == null) {
                                // System.out.println("> reading " + this.getAttributeName());
                                value = connection.getAttribute(mbeanName, attribute.getName());
                            }
                            return value;
                        }
                    });
                }
            }
            this.attributes = attributes;
        }

        public Iterator<JmxAttribute> iterator() {
            return attributes.iterator();
        }
    }
    
    public JmxQuery(final String url, final Set<String> expressions) throws IOException, MalformedObjectNameException, IntrospectionException, InstanceNotFoundException, ReflectionException {
        this.connector = JMXConnectorFactory.connect(new JMXServiceURL(url));
        this.connection = connector.getMBeanServerConnection();

        final Collection<JmxBean> mbeans = new ArrayList<JmxBean>();
        for(String expression : expressions) {
            for(ObjectInstance mbean : connection.queryMBeans(new ObjectName(expression), null)) {
                mbeans.add(new JmxBean(mbean));
            }
        }
        this.mbeans = mbeans;
    }

    public Iterator<JmxBean> iterator() {
        return mbeans.iterator();
    }

    public void close() throws IOException {
        if (connector != null) {
            connector.close();
        }
    }
}
