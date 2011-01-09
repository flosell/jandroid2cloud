/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2010 Florian Sellmayr
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package org.jandroid2cloud;
 import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simple webserver designed to catch responses from oauth
 * TODO: is this really needed?
 * TODO: use only required jetty libs
 * @author Florian Sellmayr
 *
 */
public class MySimpleWebserver {
    private static final String AUTH_TOKEN_KEY = "oauth_verifier";
    private static final Logger logger = LoggerFactory.getLogger(MySimpleWebserver.class);
    
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
	    logger.debug("Temporary server now listening at "+getAuthAddress());
	} catch (Exception e) {
	    logger.error("Error while starting temporary webserver. Authorizing may not succeed",e);
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
	    logger.debug("Stopped temporary server");
	} catch (Exception e) {
	    logger.warn("Error while stopping temporary webserver. This might not have serious consequences",e);
	}
    }
    
}
