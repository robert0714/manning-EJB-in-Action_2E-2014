# Missing ActiveMQ Artemis and Jakarta Messaging in Wildfly 12+
* [reference wildfly12](https://docs.wildfly.org/12/Getting_Started_Guide.html)
* [reference wildfly26](https://docs.wildfly.org/26/Getting_Started_Guide.html)  
> WildFly’s default configuration provides Jakarta EE Web Profile support and ``thus doesn’t include Jakarta Messaging (provided by ActiveMQ Artemis)``. As noted in the WildFly Configurations section, other configuration profiles do provide all features required by the Jakarta EE Full Platform. If you want to use messaging, make sure you start the server using an alternate configuration that provides the Jakarta EE Full Platform.

This document provides a quick overview on how to download and get started using WildFly 26 for your application development. For in-depth content on administrative features, refer to the WildFly 26 [Admin Guide](https://docs.wildfly.org/26/Admin_Guide.html#Messaging).

* https://docs.wildfly.org/26/Admin_Guide.html#Messaging
* https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.4/html-single/configuring_messaging/index

## Sample Configuration
* See $Wildfy_HOME/standalone/configuration/standalone-full.xml
* See $Wildfy_HOME/docs/examples/configs/standalone-activemq-colocated.xml

> Before modifying $Wildfy_HOME/standalone/configuration/standalone.xml , you need to backup it .

## After setting JMS
### wildfly-cli
After wildfy statup, exec $Wildfy_HOME/bin/jboss-cli

```shell
You are disconnected at the moment. Type 'connect' to connect to the server or 'help' for the list of supported commands.
[disconnected /] connect
[standalone@localhost:9990 /] 
```
See the Errorconsole

```shell
21:36:05,865 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: WildFly Full 26.1.1.Final (WildFly Core 18.1.1.Final) started in 14406ms - Started 547 of 747 services (417 services are lazy, passive or on-demand) - Server configuration file in use: standalone.xml
21:36:05,867 WARN  [org.apache.activemq.artemis.ra.ActiveMQRALogger] (default-threads - 1) AMQ153005: Unable to retrieve "jms/ShippingRequestQueue" from JNDI. Creating a new "javax.jms.Queue" named "jms.queue.ShippingRequestQueue" to be used by the MDB.
21:36:05,874 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0060: Http management interface listening on http://127.0.0.1:9990/management
21:36:05,875 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
21:36:05,981 INFO  [org.apache.activemq.artemis.ra.ActiveMQRALogger] (default-threads - 1) AMQ151000: awaiting topic/queue creation jms/ShippingRequestQueue
21:36:07,999 INFO  [org.apache.activemq.artemis.ra.ActiveMQRALogger] (default-threads - 1) AMQ151001: Attempting to reconnect org.apache.activemq.artemis.ra.inflow.ActiveMQActivationSpec(ra=org.wildfly.extension.messaging.activemq.ActiveMQResourceAdapter@16483d77 destination=jms/ShippingRequestQueue destinationType=javax.jms.Queue ack=Auto-acknowledge durable=false clientID=null user=null maxSession=15)
21:36:07,999 WARN  [org.apache.activemq.artemis.ra.ActiveMQRALogger] (default-threads - 1) AMQ153005: Unable to retrieve "jms/ShippingRequestQueue" from JNDI. Creating a new "javax.jms.Queue" named "jms.queue.ShippingRequestQueue" to be used by the MDB.
21:36:08,046 INFO  [org.apache.activemq.artemis.ra.ActiveMQRALogger] (default-threads - 1) AMQ151000: awaiting topic/queue creation jms/ShippingRequestQueue
```

Add the queue

```xml

     <subsystem xmlns="urn:jboss:domain:messaging-activemq:13.1">
            <server name="default" persistence-enabled="true">
                (ommitted....)
                <jms-queue name="jms.queue.ShippingRequestQueue" entries="java:/jms/ShippingRequestQueue"/> 
                (ommitted....)
            </server>            
        </subsystem>
        
```
### Configuring JMS destinations on WildFly 
Reference:　
* https://access.redhat.com/documentation/zh-tw/red_hat_jboss_enterprise_application_platform/7.3/html/developing_ejb_applications/message_driven_beans-1#configure_delivery_active
* http://www.mastertheboss.com/jbossas/jboss-jms/jboss-jms-configuration/

JMS destinations can be configured either by adding them in the core configuration file (standalone.xml/domain.xml) or via management interfaces such as the CLI :

#### Via CLI:

After wildfy statup, exec `$Wildfy_HOME/bin/jboss-cli` to ddd JMS queue:

```shell
You are disconnected at the moment. Type 'connect' to connect to the server or 'help' for the list of supported commands.
[disconnected /] connect
[standalone@localhost:9990 /]  jms-queue add --queue-address= jms.queue.ExampleQueue --entries=java:/jms/queue/exampleQueue,java:/jboss/exported/jms/queue/exampleQueue
```

After wildfy statup, exec `$Wildfy_HOME/bin/jboss-cli` to ddd JMS Topic:

```shell
You are disconnected at the moment. Type 'connect' to connect to the server or 'help' for the list of supported commands.
[disconnected /] connect
[standalone@localhost:9990 /]  jms-topic add --topic-address=jms.topic.ExampleTopic --entries=java:/jms/topic/exampleTopic,java:/jboss/exported/jms/topic/exampleTopic
```

Please note that the JNDI binding ``java:/jboss/exported/jms/[destination]`` is only required if you are connecting to your JMS Destination from a remote JMS Client

####  Via the configuration file:

```xml
<subsystem xmlns="urn:jboss:domain:messaging-activemq:1.0">
    <server name="default">
         . . . .  
        <jms-queue name="jms.queue.ExampleQueue" entries="java:/jms/queue/exampleQueue java:/jboss/exported/jms/queue/exampleQueue"/>
        <jms-topic name="jms.topic.ExampleTopic" entries="java:/jms/topic/exampleTopic java:/jboss/exported/jms/topic/exampleTopic"/>
    </server>
</subsystem>
```

### Test
Open the below url 

```
http://127.0.0.1:8080/chapter4-1.0-SNAPSHOT/ActionBazaarShippingRequestServlet
```
* factory: jms/QueueConnectionFactory
* queue: jms/ShippingRequestQueue

# Wildfly Boottable Jar
1. Documents About Wildfly Boottable Jar :
   * https://developers.redhat.com/articles/2022/01/26/build-bootable-jar-cloud-ready-microservices
   * https://docs.wildfly.org/26/Bootable_Guide.html
   * https://docs.microsoft.com/en-us/azure/openshift/howto-deploy-java-jboss-enterprise-application-platform-app
1. JNDI configuration references :
   * https://github.com/wildfly-extras/wildfly-datasources-galleon-pack/blob/main/testsuite/bootablejar/pom.xml
   * https://github.com/wildfly-extras/wildfly-datasources-galleon-pack/blob/main/doc/mysql/README.md

## executin command line
```shell
> mvn clean package

> java -jar target/chapter4-1.0-SNAPSHOT-bootable.jar -Djboss.bind.address=0.0.0.0  \
-Dorg.wildfly.datasources.mysql.datasource=TestDB   -Dorg.wildfly.datasources.mysql.database=cga101g1 -Dorg.wildfly.datasources.mysql.password=tibame  -Dorg.wildfly.datasources.mysql.user-name=tibame  -Dorg.wildfly.datasources.mysql.host=192.168.18.30   
```   