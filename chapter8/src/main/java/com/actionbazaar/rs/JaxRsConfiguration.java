package com.actionbazaar.rs;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


/**
 * Configures JAX-RS for the application.<br/>
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6
 * "no XML" approach to activating JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath}
 * annotation.
 * </p>
 */
@ApplicationPath("/")
public class JaxRsConfiguration extends Application {

}
