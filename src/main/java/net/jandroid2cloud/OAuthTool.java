package net.jandroid2cloud;

import java.util.Map;
import java.util.Map.Entry;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
/**
 * This Class provides all necessary functionality to perform the authorisation with the server using OAuth.
 * @author Florian Sellmayr
 *
 */
public class OAuthTool {
    private OAuthService service;
    private Token token;
    private Configuration config;
    private int tries = 0;

    public OAuthTool(Configuration config) {
	this.config = config;
	service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(config.getHost())
		.apiSecret(config.getApiSecret()).build();
	token = new Token(config.getToken(), config.getSecret());
    }
    /**
     * Performs a request to the server.
     * @param url the URL of the request
     * @param method GET or POST
     * @param params additional POST data or null
     * @return the response body from the server
     * @throws IllegalStateException if the request was not successful after 5 tries. 
     */
    public String makeRequest(String url, Verb method, Map<String, String> params) {
	System.out.println("request to " + url);
	if (tries > 5) { // TODO: make this more flexible
	    throw new IllegalStateException("tried " + tries + " times without success. aborted"); // TODO:
												   // improve
	}
	OAuthRequest request = new OAuthRequest(method, url);

	if (method.equals(Verb.POST) && params != null) {
	    for (Entry<String, String> entry : params.entrySet()) {
		request.addBodyParameter(entry.getKey(), entry.getValue());
	    }
	}

	service.signRequest(token, request);
	Response response = request.send();

	if (response.getBody().toLowerCase().contains("error")
		&& response.getBody().toLowerCase().contains("auth")) { // TODO:
	    // improve
	    reauth();
	    String result = makeRequest(url, method, params);
	    tries = 0;
	    return result;
	} else {
	    System.out.println("response code: " + response.getCode());
	    return response.getBody();
	}
    }
    
    private void reauth() {
	System.out.println("reauthorizing...");
	MySimpleWebserver server = new MySimpleWebserver();
	service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(config.getApiKey())
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
	System.out.println("autorization complete: new token " + token);
	makeRequest("http://" + config.getHost() + "/getToken", Verb.GET, null);
    }

}
