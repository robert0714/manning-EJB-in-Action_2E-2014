/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.actionbazaar.buslogic;

import com.actionbazaar.State;
import com.actionbazaar.account.Address;
import com.actionbazaar.account.Bidder;
import com.actionbazaar.account.User;
import com.actionbazaar.buslogic.exceptions.CreditCardSystemException;
import com.actionbazaar.model.Bid;
import com.actionbazaar.model.Item;
import com.actionbazaar.rs.BidDTO;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.junit.Assert.assertEquals;
import static org.jboss.shrinkwrap.resolver.api.maven.Maven.resolver;
import static java.lang.Thread.sleep;
 
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.math.BigDecimal; 
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the BidManager
 */
@RunWith(Arquillian.class)
public class BidManagerTest {
	
	private static final Logger logger = getLogger(BidManagerTest.class.getName());
	
	@ArquillianResource
	private URL url;
	
    /**
     * Entity Manager
     */
    @PersistenceContext
    private EntityManager em;
    
    /**
     * User Transaction
     */
    @Inject
    private UserTransaction utx;
    
    @Deployment
    public static Archive<?> createDeployment() {
    	WebArchive wa = ShrinkWrap.create(WebArchive.class, "chapter8-test.war")
                .addPackages(true ,State.class.getPackage())                 
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/test-persistence.xml")), "classes/META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
             
            // wa .addPackage(org.hibernate.mapping.RootClass.class.getPackage()) ;
            File[] files = resolver().loadPomFromFile("pom.xml").importRuntimeDependencies()
    				.resolve("org.hibernate:hibernate-core:5.3.24.Final").withTransitivity().asFile();
            wa.addAsLibraries(files); 
            System.out.println(wa.toString(true));
            return wa;
    }
    
    /**
     * Starts the transaction
     * @throws Exception - exception
     */
    @Before
    public void startTransaction() throws Exception {
        utx.begin();
        em.joinTransaction();
    }
    
    /**
     * Commits the transaction
     * @throws Exception - exception
     */
    @After
    public void commitTransaction() throws Exception {
        utx.commit();
    }
    
    /**
     * Tests creating and persisting an Item
     */
    @Test
    public void testCreates() {
    	logger.info("start persist test");
    	
        /**
         * Create an Item and persist it
         */
        Calendar endDate = new GregorianCalendar(2013,1,1,12,0);
        Item item = new Item("Hobie Holder 14", new Date(),endDate.getTime(), new Date(), new BigDecimal("14")); 
        Address address = new Address("street","city",State.California,"90210","555-555-5555");
        em.persist(item);
        
        /**
         * Create a bidder and persist it
         */
        Bidder bidder = new Bidder("rcuprak","password","Ryan","Cuprak",address,new Date(),false);
        em.persist(bidder);
        
        /**
         * Create a bid on the item
         */
        Bid bid = new Bid(bidder,item,new BigDecimal("100"));
        em.persist(bid);
        
    }	
    @Test
	public void testGetBid() throws Exception {
		logger.info("start rest receive messages test");
		BidDTO myResponse = invokeFuture(url + "/bidService/getBid/83749");
		assertEquals("magic number is: ", Long.valueOf("83749"), myResponse.getBidId());
		 
		logger.info("end rest receive messages test");
	}
    private BidDTO invokeFuture(String url) {
		Client client = newClient();
		WebTarget target = client.target(url);
		final AsyncInvoker asyncInvoker = target.request().async();
		Future<Response> future = asyncInvoker.get();
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			logger.log(SEVERE, "error", e);
		}
		 BidDTO myResponse = null;
		try {
			Response response = future.get();
			myResponse = response.readEntity(BidDTO.class);
		} catch (InterruptedException | ExecutionException e) {
			logger.log(SEVERE, "error", e);
		}
		return myResponse;
	} 
}
