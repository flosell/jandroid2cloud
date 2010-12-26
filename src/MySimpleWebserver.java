 import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * A very simple webserver designed to catch responses from oauth
 * TODO: is this really needed?
 * TODO: use only required jetty libs
 * @author Florian Sellmayr
 *
 */
public class MySimpleWebserver {
    private static final String AUTH_TOKEN_KEY = "oauth_verifier";
    private String authToken=null;
    private Server server;
    public MySimpleWebserver()  {
	server = new Server(0); 
	server.setHandler(new AbstractHandler() {

	    @Override
	    public void handle(String target, Request baseRequest, HttpServletRequest request,
		    HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		if (request.getParameter(AUTH_TOKEN_KEY)!=null) {
		    setAuthToken(request.getParameter(AUTH_TOKEN_KEY));
		    response.getWriter().println("Received token: "+authToken+" thank you.");
		}else {
		    response.getWriter().println("Something's wrong. Parameters:"+request.getParameterMap());
		}
		
	    }
	});
	
	try {
	    server.start();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private int getPort() {
	return server.getConnectors()[0].getLocalPort();
    }
    
    public synchronized void setAuthToken(String authToken) {
	this.authToken = authToken;
	notifyAll();
    }
    
    public synchronized String getAuthToken() {
	if (authToken==null) {
	    try {
		wait();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return authToken;
    }
    
    public String getAuthAddress() {
	return "http://localhost:"+getPort()+"/";
    }
    
    public void stop()  {
	try {
	    server.stop();
	    server.join();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
}
