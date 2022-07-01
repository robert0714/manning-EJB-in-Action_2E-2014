/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.actionbazaar.listing01;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.servlet.ServletException; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException; 
import javax.jms.MessageProducer;
import javax.jms.Session;
 
/**
 *
 * @author <a href="mailto:mjremijan@yahoo.com">Michael Remijan</a>
 */
@WebServlet(name = "ActionBazaarShippingRequestServlet", urlPatterns = {"/ActionBazaarShippingRequestServlet"}) 
public class ActionBazaarShippingRequestServlet extends HttpServlet {
  
	/**
	 * 
	 */
	private static final long serialVersionUID = -9159556280355152906L;
	
	private static final Logger logger = getLogger(ActionBazaarShippingRequestServlet.class.getName());

		
    private String jmsVersion;
    
    @Resource(lookup = "java:/jms/remoteCF") //for pooled-connection-factory
//  @Resource(lookup = "java:/amq/ConnectionFactory")//for resource-adapter-amq
//  @Resource(lookup = "java:/ConnectionFactory")//adjusted tutorilas
    private ConnectionFactory cf ; 
    

    @JMSConnectionFactory("java:/jms/remoteCF") //for pooled-connection-factory
//	@JMSConnectionFactory("java:/amq/ConnectionFactory")//for resource-adapter-amq
//	@JMSConnectionFactory("java:/ConnectionFactory")  //adjusted tutorilas  
	@Inject
	private JMSContext context;	

//	@Resource(lookup = "java:/queue/simpleMDBTestQueue")//for resource-adapter-amq
//	@Resource(mappedName=  "java:/jms/queue/DLQ")//adjusted tutorilas
    @Resource(lookup = "java:/queue/testQueueRemoteArtemis")//for pooled-connection-factory
	private Queue destination;
	
    @PostConstruct
    protected void postConstruct() {
		try (Connection connection = cf.createConnection()) {
			final ConnectionMetaData cmd = connection.getMetaData();
			logger.info("------------------------------");
			if (cmd != null) { 
				jmsVersion = cmd.getJMSVersion();
				logger.info("jms version: "+jmsVersion);
			}
		} catch (JMSException ex) {
			logger.log(SEVERE, "Exception sending msg", ex);
		} finally {
			logger.info("------------end--------------");
		}
    }
    
	/**
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Throwable oops = null;
		try {
			final ActionBazaarShippingRequest shippingRequest = new ActionBazaarShippingRequest();
			shippingRequest.setItem("item");
			shippingRequest.setShippingAddress("address");
			shippingRequest.setShippingMethod("method");
			shippingRequest.setInsuranceAmount(100.50); 
			
			sendMsg(shippingRequest);
			

		} catch (Throwable t) {
			oops = t;
		} 
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet SendMessageServlet</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet SendMessageServlet at " + request.getContextPath() + "</h1>");
			out.println("<p>Time:  " + String.valueOf(new Date()) + "</p>");
			if (oops != null) {
				out.println("<p>Opps:</p>");
				out.println("<xmp>");
				oops.printStackTrace(out);
				out.println("</xmp>");
			} else {
				out.println("<p>Should be OK</p>");
			}
			out.println("</body>");
			out.println("</html>");
		} finally {
			out.close();
		}
	}

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP
   * <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

  /**
   * Handles the HTTP
   * <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
    protected void sendMsg(final ActionBazaarShippingRequest shippingRequest ) {
    	
		switch(jmsVersion) {
    	case "1.1":
    		sendMsgByJMS11(shippingRequest);
    		break;
    	default:
    		sendMsgByJMS2(shippingRequest);
    		break;    	
    	}
    }
    protected void sendMsgByJMS11(final ActionBazaarShippingRequest shippingRequest) { 
    	
		try (Connection connection = cf.createConnection()) {			
            final Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            
            final ObjectMessage om = session.createObjectMessage();
    		om.setObject(shippingRequest);
            final MessageProducer producer = session.createProducer(destination);             
            producer.send(om);
        }catch (JMSException ex) {
            logger.log(SEVERE, "Exception sending msg", ex);
        }
	}
    protected void sendMsgByJMS2(final ActionBazaarShippingRequest shippingRequest) { 
    	
		try {			
			 JMSContext jmsContext = getJMSContext();
	   		 final JMSProducer producer = jmsContext.createProducer();
	   		 logger.info("jms version: "+jmsVersion);
	   		 final ObjectMessage om =  jmsContext.createObjectMessage();
	   		 om.setObject(shippingRequest);
	   		 producer.send(destination, om);
        }catch (JMSException ex) {
            logger.log(SEVERE, "Exception sending msg", ex);
        }
	}
   
    /**
     * Because using wildfly-jar-maven-plugin ,we discover that  injecting JMSContext would be null.<br/>
     * we use JMS 1.1(ConnectionFactory) to get JMSContext.<br/>
     * Sending messages to ActiveMQ can be done via the JMS API. At the moment, ActiveMQ 5.x only supports the JMS 1.1 API.
     * **/
	protected JMSContext getJMSContext() {
		if (this.context != null) {
			return this.context;
		} else if(cf!=null) {
			return cf.createContext();
		}else {
			logger.info("Both ConnectionFactory & JMSContext are null.");
			return null;
		}
	} 
}
