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

package net.jandroid2cloud;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.GAEChannel4j.Connection;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class Android2CloudServerConnection {
    private static final Pattern LINK_PATTERN = Pattern.compile("<link>([^<]*)(?=</link>)");
    private Configuration config;
    private String channelToken;
    private Connection connection;
    private OAuthTool oauth;

    public Android2CloudServerConnection(Configuration config) {
	this.config = config;
	this.oauth = new OAuthTool(config);
	openChannel();
    }

    private void openChannel() {
	if (connection == null) {
	    String response = oauth.makeRequest("http://" + config.getHost() + "/getToken",
		    Verb.GET, null);
	    System.out.println("getChannelToken response:" + response);
	    channelToken = response;

	    connection = new Connection(channelToken);
	    connection.setHandler(new ChannelHandler(config, oauth));
	    connection.open();
	} else {
	    System.out.println("FAIL: WHAT HAPPENED"); // TODO: needed?
	}
    }

}
