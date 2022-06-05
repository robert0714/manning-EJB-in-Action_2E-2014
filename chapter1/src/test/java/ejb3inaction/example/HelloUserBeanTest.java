/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb3inaction.example;

import javax.ejb.EJB;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment; 
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import jakarta.inject.Inject;
//import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertEquals;
/**
 * Unit Test for the HelloUserBean
 */
@RunWith(Arquillian.class)
//@ExtendWith(ArquillianExtension.class)
public class HelloUserBeanTest {

//	@Inject
	@EJB
    private HelloUser helloUser;

    @Deployment
    public static JavaArchive  createDeployment() { 
        return ShrinkWrap.create(JavaArchive.class, 
        		HelloUserBeanTest.class.getName() + ".jar")
				.addClasses(HelloUserBean.class  )
				.addPackage(HelloUserBean.class.getPackage()) 
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml"); 
    }

    /**
     * Tests the hello message
     */
    @Test
    public void testSayHello() {
    	System.out.println(helloUser); 
        String helloMessage = helloUser.sayHello("Curious George");
        assertEquals("Message did not match.","Hello Curious George welcome to EJB 3.1!",helloMessage);
    }
}
