/**
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

import com.actionbazaar.persistence.Bid;
import com.actionbazaar.persistence.Bidder;
import com.actionbazaar.persistence.Item;
 
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment; 
//import org.jboss.arquillian.api.Run;
//import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This verifies that users can be persisted and retrieved.
 */
@RunWith(Arquillian.class)
//@Run(RunModeType.IN_CONTAINER)
public class UserServiceTest {

    /**
     * Injects the user service bean
     */
    @EJB
    private UserService userService;

    /**
     * Creates a deployment item.
     * @return ShrinkWrap
     */    
    @Deployment
    public static WebArchive createDeployment() { 
        return ShrinkWrap.create(WebArchive.class, 
        		UserServiceTest.class.getName() + ".war")
        		.addClasses(OrderProcessor.class,
                        OrderProcessorBean.class,UserService.class,UserServiceBean.class,
                        ItemService.class,
                        ItemServiceBean.class, Bid.class, Bidder.class, Item.class)	
        		// It's used for non-embedded (managed ,remote) server
        		.addAsWebInfResource(new ClassLoaderAsset("test-persistence.xml", UserServiceTest.class.getClassLoader()),
						"classes/META-INF/persistence.xml")
        		.addPackage(OrderProcessor.class.getPackage())
        		.addPackage(Bidder.class.getPackage())
        		.addAsResource("META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml"); 
    }
    /**
     * Test persistence of a user
     */
    @Test
    public void testItemPersistence() {
        Bidder bidder = new Bidder("John","Wesley Powell",1869l);
        userService.createUser(bidder);
        Assert.assertNotNull(bidder.getBidderId());
        Assert.assertNotNull(userService.getUser(bidder.getBidderId()));
    }
}
