package net.jandroid2cloud;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.GAEChannel4j.IChannelHandler;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.scribe.model.Verb;

public class ChannelHandler implements IChannelHandler {
    private Configuration config;
    private OAuthTool oauth;

    public ChannelHandler(Configuration config, OAuthTool oauth) {
	this.config = config;
	this.oauth = oauth;
    }

    @Override
    public void open() {
	System.out.println("successfully opened channel");
	String username = oauth.makeRequest("http://" + config.getHost() + "/connected/"
		+ config.getIdentifier(), Verb.POST, null);
	System.out.println("username: " + username);
    }

    @Override
    public void message(String rawMsg) {
	System.out.println("received a message:" + rawMsg);

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
	    System.out.println("response for markread: " + response);
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
	config.openURLinBrowser(url);
    }

    @Override
    public void close() {
	System.out.println("connection has been closed");
    }

    @Override
    public void error(String description, int code) {
	System.out.println("error " + code + ": " + description);
    }

}
