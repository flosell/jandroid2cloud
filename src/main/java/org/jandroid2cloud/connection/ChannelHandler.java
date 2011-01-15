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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.plaf.basic.BasicBorders.MarginBorder;

import org.GAEChannel4j.IChannelHandler;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.jandroid2cloud.configuration.Configuration;
import org.jandroid2cloud.ui.notifications.NotificationAppender;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ChannelHandler implements IChannelHandler {
    private Configuration config;
    private OAuthTool oauth;
    private static final Logger logger = LoggerFactory.getLogger(ChannelHandler.class);
    public ChannelHandler(Configuration config, OAuthTool oauth) {
	this.config = config;
	this.oauth = oauth;
    }

    @Override
    public void open() {
	logger.info(NotificationAppender.MARKER,"Server confirmed: Channel is open");
	String username = oauth.makeRequest("http://" + config.getHost() + "/connected/"
		+ config.getIdentifier(), Verb.POST, null);
	logger.debug("Using username"+username);
    }

    @Override
    public void message(String rawMsg) {
	logger.debug("Received message from server:"+rawMsg);
	
	try {
	    JSONObject jsonMessage = new JSONObject(new JSONTokener(rawMsg));
	    JSONObject links = (JSONObject) jsonMessage.opt("links");
	    if (links == null) {
		JSONObject link = jsonMessage.optJSONObject("link");
		handleLink(link);
	    } else {
		Iterator it = links.keys();
		while (it.hasNext()) {
		    String s = (String) it.next();
		    if (s != null && !s.isEmpty()) {
			JSONObject o = links.getJSONObject(s);
			handleLink(o);
		    }
		}
	    }

	    Map<String, String> params = new HashMap<String, String>();
	    params.put("links", rawMsg);
	    String response = oauth.makeRequest("http://" + config.getHost() + "/markread",
		    Verb.POST, params);
	    logger.debug("Marked message as read");
	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }

    private void handleLink(JSONObject link) throws JSONException {
	if (link.has("link")) {
	    link = link.getJSONObject("link");
	}
	String url = link.getString("url");
	logger.info(NotificationAppender.MARKER,"new link "+url+" received. Opening browser");
	config.openURLinBrowser(url);
    }

    @Override
    public void close() {
	logger.warn("Server closed connection");
    }

    @Override
    public void error(String description, int code) {
	logger.error("error " + code + ": " + description);
    }

}
