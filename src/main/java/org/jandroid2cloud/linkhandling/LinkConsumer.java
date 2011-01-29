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

package org.jandroid2cloud.linkhandling;

import org.jandroid2cloud.configuration.Configuration;
import org.jandroid2cloud.ui.notifications.NotificationAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkConsumer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(LinkConsumer.class);
    private boolean stopped = false;
    
    
    @Override
    public void run() {
	LinkQueue q = LinkQueue.INSTANCE;
	Configuration config = Configuration.getInstance();
	int sleepTime = config.getTimeBetweenLinks();
	logger.debug("LinkConsumer running...");
	while (!stopped ) {
	    String url = q.pop();
	    if (url!="") {
		logger.info(NotificationAppender.MARKER,"Received link "+url+".\n" +
				"Opening browser and waiting "+sleepTime/1000.0+" seconds before opening the next link");
		config.openURLinBrowser(url);
		try {
		    Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
		    logger.warn("Something interrupted the sleep between two received links.",e);
		}
	    }
	}
	logger.debug("LinkConsumer stopped...");
    }

    /**
     * Stops the consumer.
     */
    public void stopConsumer() {
	logger.debug("Stopping LinkConsumer....");
	stopped=true;
	LinkQueue.INSTANCE.push("");
    }
}
