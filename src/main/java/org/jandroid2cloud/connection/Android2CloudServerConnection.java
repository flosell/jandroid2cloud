/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 Florian Sellmayr
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

package org.jandroid2cloud.connection;

import org.GAEChannel4j.Connection;
import org.eclipse.swt.widgets.Display;
import org.jandroid2cloud.configuration.Configuration;
import org.jandroid2cloud.exceptions.NetworkException;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This is the Entry point for the Application to create a connection to the Android2Cloud server. 
 * @author Florian Sellmayr
 *
 */
public class Android2CloudServerConnection {
    private Configuration config;
    private Connection connection;
    private OAuthTool oauth;
    private Display display;
    private static final Logger logger = LoggerFactory
	    .getLogger(Android2CloudServerConnection.class);
    public Android2CloudServerConnection(Configuration config) {
	logger.debug("Creating new A2C connection");
	this.config = config;
	this.oauth = new OAuthTool(config);
    }
    
    public Android2CloudServerConnection(Configuration config,Display display) {
	logger.debug("Creating new A2C connection");
	this.config = config;
	this.oauth = new OAuthTool(config);
	this.display=display;
    }
    
    /**
     * Opens a connection to the server. 
     * This method will return immediately and perform event handling in background. 
     */
    public void open() throws NetworkException{
	logger.info("Opening connection to Android2Cloud server "+config.getHost());
	if (connection == null) {
	    String response = oauth.makeRequest("http://" + config.getHost() + "/getToken",
		    Verb.GET, null);
	    logger.debug("Received channelToken:"+response);
	    String channelToken = response;

	    connection = new Connection(channelToken,display);
	    connection.setHandler(new ChannelHandler(config, oauth));
	    connection.open();
	    logger.info("Connection is up and running. Waiting for messages from server.");
	} else {
	    logger.warn("This should not happen");
	}
    }
    /**
     * Closes the connection to the server and stops background-threads
     */
    public void close() {
	// TODO
	logger.info("Closing connection");
	logger.warn("Closing connection not yet implemented");
    }

}
