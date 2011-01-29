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

package org.GAEChannel4j;

import org.GAEChannel4j.impl.BrowserThread;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private IChannelHandler handler = new ChannelHandlerAdapter();
    private String token;
    private BrowserThread thread;
    private Display display;

    /**
     * Creates a new Connection with the given token. This is just
     * initialization. Communication will happen only after the connection is
     * opened() // TODO: link
     * 
     * @param token
     *            the channels token
     */
    public Connection(String token) {
	this.token = token;
    }

    public Connection(String token, Display display) {
	this(token);
	this.display = display;
    }

    /**
     * Opens a new channel. This method will spawn a new background-thread that
     * handles all communication and returns immediately. The handler must be
     * set before a channel is opened. If you want to know if the connection was
     * successful, use respective handler.
     */
    public void open() {
	if (display != null) {
	    thread = new BrowserThread(handler, token, display);
	    thread.run();
	} else {
	    thread = new BrowserThread(handler, token);
	    thread.start();

	}
	logger.debug("Started background handler to handle channelevents");
    }

    /**
     * Stops the background thread. After that, no more events will happen.
     */
    public void close() {
	thread.stopThread();
	logger.debug("stopped events");
	// TODO: translate to real JS call?
    }

    public void setHandler(IChannelHandler handler) {
	this.handler = handler;
    }

}
