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

import java.util.Map;
import java.util.Map.Entry;

import org.jandroid2cloud.configuration.Configuration;
import org.jandroid2cloud.exceptions.NetworkException;
import org.jandroid2cloud.exceptions.NetworkException.Type;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Class provides all necessary functionality to perform the authorisation
 * with the server using OAuth.
 * 
 * @author Florian Sellmayr
 * 
 */
public class OAuthTool {
    private OAuthService service;
    private Token token;
    private Configuration config;
    private int tries = 0;
    private static final Logger logger = LoggerFactory.getLogger(OAuthTool.class);

    public OAuthTool(Configuration config) {
	this.config = config;
	service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(config.getHost())
		.apiSecret(config.getApiSecret()).build();
	token = new Token(config.getToken(), config.getSecret());
	logger.debug("Created OAuthTool");
    }

    /**
     * Performs a request to the server.
     * 
     * @param url
     *            the URL of the request
     * @param method
     *            GET or POST
     * @param params
     *            additional POST data or null
     * @return the response body from the server
     * @throws IllegalStateException
     *             if the request was not successful after 5 tries.
     */
    public String makeRequest(String url, Verb method, Map<String, String> params) throws NetworkException{
	logger.debug("OAuth request to " + method.name() + " " + url + " params:" + params);
	if (tries > 5) { // TODO: make this more flexible
	    throw new NetworkException("Could not make OAuth request to "+url+". To many authorization retries:"+tries); 
	}
	OAuthRequest request = new OAuthRequest(method, url);

	if (method.equals(Verb.POST) && params != null) {
	    for (Entry<String, String> entry : params.entrySet()) {
		request.addBodyParameter(entry.getKey(), entry.getValue());
	    }
	}

	service.signRequest(token, request);
	Response response = request.send();
	logger.debug("OAuth response: " + response.getCode() + ": \"" + response.getBody()+"\"");

	if (response.getBody().toLowerCase().contains("error")
		&& response.getBody().toLowerCase().contains("auth")) { // TODO:
	    logger.warn("OAuth-Request failed. trying again");
	    reauth();
	    String result = makeRequest(url, method, params);
	    tries = 0;
	    return result;
	} else {
	    if (response.getCode()==404) {
		throw new NetworkException(Type.NOT_FOUND, "Could not execute OAuth: "+url+" not found");
	    }
	    return response.getBody();
	}
    }

    private void reauth() throws NetworkException {
	logger.info("Authorizing application...");
	MySimpleWebserver server = new MySimpleWebserver();
	service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(config.getApiKey())
		.apiSecret(config.getApiSecret()).callback(server.getAuthAddress()).build();
	Token requestToken = service.getRequestToken();
	String requestURL = "https://" + config.getHost() + "/_ah/OAuthAuthorizeToken?oauth_token="
		+ requestToken.getToken();
	config.openURLinBrowser(requestURL);
	logger.debug("opening " + requestURL);
	logger.debug("received verifier token:" + server.getAuthToken());
	Verifier verifier = new Verifier(server.getAuthToken());
	token = service.getAccessToken(requestToken, verifier);
	config.setToken(token.getToken());
	config.setSecret(token.getSecret());
	server.stop();
	logger.debug("autorization complete: new token " + token);
	makeRequest("http://" + config.getHost() + "/getToken", Verb.GET, null);
    }

}
