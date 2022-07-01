/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.actionbazaar.listing02;

import com.actionbazaar.listing01.ActionBazaarShippingRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 *
 * @author <a href="mailto:mjremijan@yahoo.com">Michael Remijan</a>
 */
@MessageDriven(name  = "TurtleShippingMDM", activationConfig = {  
  @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
  @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
  @ActivationConfigProperty(propertyName = "destinationType",    propertyValue = "javax.jms.Queue"),
//  @ActivationConfigProperty(propertyName = "destination", propertyValue = "simpleMDBTestQueue") //for resource-adapter-amq
@ActivationConfigProperty(propertyName = "destination", propertyValue = "testQueueRemoteArtemis") //for pooled-connection-factory
//  @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/DLQ") //adjusted tutorilas
//  , 
//  @ActivationConfigProperty(propertyName = "destinationLookup",  propertyValue = "jms/ShippingRequestQueue") // original tutorilas
})
//If you use pooled-connection-factory,you need to set the ResourceAdapter.
@ResourceAdapter("remote-artemis")
public class TurtleShippingRequestMessageBean
  implements MessageListener {
	private static final Logger logger = Logger.getLogger(TurtleShippingRequestMessageBean.class.getName());

  @PersistenceContext()
  private EntityManager entityManager;
  
  @Override
  public void onMessage(Message message) {
    try {
      ObjectMessage om = (ObjectMessage) message;
      Object o = om.getObject();
      ActionBazaarShippingRequest sr = (ActionBazaarShippingRequest) o;
      
      logger .log(Level.INFO, String.format("Got message: %s", sr));
    
      TurtleShippingRequest tr = new TurtleShippingRequest();      
      tr.setInsuranceAmount(sr.getInsuranceAmount());
      tr.setItem(sr.getItem());
      tr.setShippingAddress(sr.getShippingAddress());
      tr.setShippingMethod(sr.getShippingMethod());
      entityManager.persist(tr);      

    } catch (JMSException ex) {
    	logger .log(Level.SEVERE, null, ex);
    }
  }
}
