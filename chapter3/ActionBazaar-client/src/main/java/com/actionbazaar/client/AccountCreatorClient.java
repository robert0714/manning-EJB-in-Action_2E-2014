/**
 *  EJB 3 in Action
 *  Book: http://www.manning.com/panda/
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
package com.actionbazaar.client;

import com.actionbazaar.buslogic.BidderAccountCreator;
import com.actionbazaar.persistence.BillingInfo;
import com.actionbazaar.persistence.BiographicalInfo;
import com.actionbazaar.persistence.LoginInfo; 

import java.util.Hashtable;
import java.util.Properties;
 
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException; 

/**
 * AccountCreatorClient
 */
public class AccountCreatorClient  {


    /**
     * Creates a new AccountCreatorClient
     */
    public AccountCreatorClient() {
//        super("Accont Creator Client");
    }

    public static void main(String[] args)  throws NamingException {
    	/**
         * Account creator
         */
    	BidderAccountCreator accountCreator =lookupAccountCreatorEJB();

        LoginInfo login = new LoginInfo();
        login.setUsername("dpanda");
        login.setPassword("welcome");

        accountCreator.addLoginInfo(login);

        BiographicalInfo bio = new BiographicalInfo();
        bio.setFirstName("Debu");
        bio.setLastName("Panda");

        accountCreator.addBiographicalInfo(bio);

        BillingInfo billing = new BillingInfo();
        billing.setCreditCardType("VISA");
        billing.setAccountNumber("0123456789");

        accountCreator.addBillingInfo(billing);

        // Create account
        accountCreator.createAccount();
    }

	private static BidderAccountCreator lookupAccountCreatorEJB() throws NamingException {		                            
		final String lookupNameV1 = "ejb:ActionBazaar-ear/ActionBazaar-ejb/BidderAccountCreator!com.actionbazaar.buslogic.BidderAccountCreator?stateful" ; 
		//see the console to make sure the ejb's lookupName
		                             
		Context ctx = createInitialContextV1();
		return (BidderAccountCreator) ctx.lookup(lookupNameV1);
	}
    private static Context createInitialContextV2() throws NamingException {
    	final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        //use HTTP upgrade, an initial upgrade requests is sent to upgrade to the remoting protocol
        jndiProperties.put(Context.PROVIDER_URL,"remote+http://localhost:8080");
        
        
        return new InitialContext(jndiProperties);
    }
    private static Context createInitialContextV1() throws NamingException {
        Properties jndiProperties = new Properties();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        jndiProperties.put("jboss.naming.client.ejb.context", true);
        
      
        return new InitialContext(jndiProperties);
    }
}