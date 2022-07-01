package com.actionbazaar.listing02;
import static java.util.logging.Logger.getLogger;
import static javax.batch.runtime.BatchStatus.COMPLETED;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.jboss.as.test.shared.integration.ejb.security.PermissionUtils.createPermissionsXmlAsset;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.jboss.shrinkwrap.resolver.api.maven.Maven.resolver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.jboss.as.controller.client.helpers.ClientConstants.NAME;
import static org.jboss.as.controller.client.helpers.ClientConstants.OP;
import static org.jboss.as.controller.client.helpers.ClientConstants.OP_ADDR;
import static org.jboss.as.controller.client.helpers.ClientConstants.OUTCOME;
import static org.jboss.as.controller.client.helpers.ClientConstants.READ_ATTRIBUTE_OPERATION;
import static org.jboss.as.controller.client.helpers.ClientConstants.RESULT;
import static org.jboss.as.controller.client.helpers.ClientConstants.SUCCESS;
import static org.jboss.as.test.shared.integration.ejb.security.PermissionUtils.createPermissionsXmlAsset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.util.PropertyPermission;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Queue;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.apache.commons.lang3.ArrayUtils;
import java.time.Duration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations; 
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.shared.TimeoutUtil;
import org.jboss.remoting3.security.RemotingPermission;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
 

import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.dmr.ModelNode;
 


import java.io.FilePermission;
import java.io.IOException;
import java.util.PropertyPermission;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.shared.TimeoutUtil;
import org.jboss.dmr.ModelNode;
import org.jboss.remoting3.security.RemotingPermission;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Ignore;

//refer: https://github.com/wildfly/wildfly/blob/main/testsuite/integration/basic/src/test/java/org/jboss/as/test/integration/ejb/mdb/deliveryactive/MDBTestCase.java
@RunWith(Arquillian.class)
public class MDBTestCase {

	private static final Logger logger = getLogger(MDBTestCase.class.getName());

	@Deployment
	public static Archive<?> createEJBDeployment() {
		final EnterpriseArchive ear = create(EnterpriseArchive.class, "mdb-ejb-test.ear");
		final JavaArchive ejbJar = create(JavaArchive.class, "mdb-ejb-test.jar")  
				.addPackage(TurtleShippingRequestMessageBean.class.getPackage()) 
				.addPackage(JMSOperations.class.getPackage()) 
				.addClass(TimeoutUtil.class)
				.addClass(MDBTestCase.class)
				.addClass(ManagementClient.class)				 			  
				.addAsManifestResource(
						new StringAsset(
								"Dependencies: org.jboss.as.controller-client, org.jboss.dmr, org.jboss.remoting\n"),
						"MANIFEST.MF");
		
				ejbJar.addPackage(org.codehaus.plexus.util.xml.Xpp3DomBuilder.class.getPackage());
				ejbJar.addAsManifestResource(new FileAsset(new File("src/test/resources/META-INF/persistence-test.xml")),
						"persistence.xml");
				
		// grant necessary permissions
        ejbJar.addAsManifestResource(createPermissionsXmlAsset(
                new PropertyPermission("ts.timeout.factor", "read"),
                new RemotingPermission("createEndpoint"),
                new RemotingPermission("connect"),
                new FilePermission(System.getProperty("jboss.inst") + "/standalone/tmp/auth/*", "read")
        ), "permissions.xml");
        
        ear.addAsModule(ejbJar); 
        
        File[] files = resolver().loadPomFromFile("pom.xml").importRuntimeDependencies()
				.resolve("org.awaitility:awaitility:3.1.6").withTransitivity().asFile();
        
        ear.addAsLibraries(files); 
		return ear;
	} 
    private static final int TIMEOUT = TimeoutUtil.adjust(5000);
	private final static String QUEUE_WORKING_LOOKUP = "java:/jms/queue/DLQ";
	
	
	@Resource(mappedName = QUEUE_WORKING_LOOKUP)
	private Queue queueWorking;
	
	@EJB
	private TurtleShippingRequestMessageBean mdbBean;
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory cf;
	
	@Test
	public void testWorkingBean() throws Exception {
		logger.info("starting message driven bean test");
		
		JMSContext context = cf.createContext(AUTO_ACKNOWLEDGE);
		context.createProducer().send(queueWorking, "need a pause");
		final	JMSConsumer consumer = context.createConsumer(queueWorking);
		  
		Message message = consumer.receive(TIMEOUT);
		
//		await()
//			.atLeast(Duration.ofMillis(400L))
//			.atMost(Duration.ofSeconds(10L))
//		.with()
//		    .pollInterval(Duration.ofMillis(400L))
//		     .until(consumer::receive, equalTo(message)); 
		
		assertNotNull("the message is received by the mdb: ", message);
		context.close();
	}
}
