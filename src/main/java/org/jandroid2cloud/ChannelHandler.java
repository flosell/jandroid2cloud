package org.jandroid2cloud;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.GAEChannel4j.IChannelHandler;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	logger.info("Server confirmed: Channel is open");
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
	System.out.println("received url: "+url);
	logger.info("new link "+url+" received. Opening browser");
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
