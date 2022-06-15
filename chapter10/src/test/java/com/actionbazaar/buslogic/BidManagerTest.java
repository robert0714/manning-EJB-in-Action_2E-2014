/**
 *  BidManagerTest.java
 *  EJB 3 in Action
 *  Book: http://manning.com/panda2/
 *  Code: http://code.google.com/p/action-bazaar/
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.actionbazaar.buslogic;

import com.actionbazaar.State;
import com.actionbazaar.account.Address;
import com.actionbazaar.account.BazaarAccount;
import com.actionbazaar.account.BillingInfo;
import com.actionbazaar.account.Seller;
import com.actionbazaar.account.User;
import com.actionbazaar.buslogic.exceptions.CreditCardSystemException;
import com.actionbazaar.controller.BidController;
import com.actionbazaar.model.Bid;
import com.actionbazaar.model.CreditCard;
import com.actionbazaar.model.Item;
import com.actionbazaar.setup.Bootstrap;
import com.actionbazaar.util.FacesContextProducer;

import static java.util.logging.Logger.getLogger;
import static javax.batch.runtime.BatchStatus.COMPLETED;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.jboss.shrinkwrap.resolver.api.maven.Maven.resolver;
import static org.jboss.as.controller.client.helpers.ClientConstants.NAME;
import static org.jboss.as.controller.client.helpers.ClientConstants.OP;
import static org.jboss.as.controller.client.helpers.ClientConstants.OP_ADDR;
import static org.jboss.as.controller.client.helpers.ClientConstants.OUTCOME;
import static org.jboss.as.controller.client.helpers.ClientConstants.READ_ATTRIBUTE_OPERATION;
import static org.jboss.as.controller.client.helpers.ClientConstants.RESULT;
import static org.jboss.as.controller.client.helpers.ClientConstants.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
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
        WebArchive wa = ShrinkWrap.create(WebArchive.class, "chapter10-test.war")
            .addPackage(State.class.getPackage())
            .addPackage(User.class.getPackage())    
            .addPackage(BidManager.class.getPackage()) 
            .addPackage(CreditCardSystemException.class.getPackage())   
            .addPackage(Item.class.getPackage())
            .addPackage(BidController.class.getPackage())
            .addPackage(Bootstrap.class.getPackage()) 
            .addPackage(FacesContextProducer.class.getPackage())               
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
        BazaarAccount bidder = new BazaarAccount("rcuprak","password","Ryan","Cuprak",address,new Date(),false);
        em.persist(bidder);
        
        /**
         * Create a bid on the item
         */
        Bid bid = new Bid(bidder,item,new BigDecimal("100"));
        em.persist(bid);
        
    }
}
