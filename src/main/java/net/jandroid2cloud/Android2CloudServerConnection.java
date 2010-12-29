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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class Android2CloudServerConnection {
    private static final Pattern LINK_PATTERN = Pattern.compile("<link>([^<]*)(?=</link>)");
    private OAuthService service;
    private Token token;
    private Configuration config;
    private int tries = 0;

    public Android2CloudServerConnection(Configuration config) {
	this.config = config;
	service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(config.getHost())
		.apiSecret(config.getApiSecret()).build();
	token = new Token(config.getToken(), config.getSecret());
    }

    public String makeRequest(String url) {
	if (tries > 5) {
	    throw new IllegalStateException("tried " + tries + " times without success. aborted"); // TODO:
												   // improve
	}
	OAuthRequest request = new OAuthRequest(Verb.GET, url);
	service.signRequest(token, request);
	Response response = request.send();

	if (response.getBody().contains("error") && response.getBody().contains("auth")) { // TODO:
											   // improve
	    reauth();
	    String result = makeRequest(url);
	    tries = 0;
	    return result;
	} else {
	    return response.getBody();
	}
    }

    private void reauth() {
	MySimpleWebserver server = new MySimpleWebserver();
	service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(config.getHost())
		.apiSecret(config.getApiSecret()).callback(server.getAuthAddress()).build();
	Token requestToken = service.getRequestToken();
	String requestURL = "https://" + config.getHost() + "/_ah/OAuthAuthorizeToken?oauth_token="
		+ requestToken.getToken();
	config.openURLinBrowser(requestURL);
	System.out.println("opening " + requestURL);
	System.out.println("received verifier token:" + server.getAuthToken());
	Verifier verifier = new Verifier(server.getAuthToken());
	token = service.getAccessToken(requestToken, verifier);
	config.setToken(token.getToken());
	config.setSecret(token.getSecret());
	server.stop();
    }

    public String getLink() {
	String body = makeRequest("http://" + config.getHost() + "/links/get");

	Matcher m = LINK_PATTERN.matcher(body);
	if (m.find()) {
	    return m.group(1);
	}else {
	    return null;
	}
    }
}
