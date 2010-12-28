package net.jandroid2cloud;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class MyFirstOAuth {
    
    
    private static final String HOST = "android2cloud.appspot.com";

    public static void main(String[] args) throws Exception {
//	MySimpleWebserver server = new MySimpleWebserver();
//	
//	OAuthService service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(HOST)
//		.apiSecret("ySS8cOHGuNgNS9qLsxUhQBH/").callback(server.getAuthAddress()).build();

	OAuthService service = new ServiceBuilder().provider(Android2CloudApi.class).apiKey(HOST)
		.apiSecret("ySS8cOHGuNgNS9qLsxUhQBH/").build();
//	Token requestToken = service.getRequestToken();
//	String requestURL =  "https://"+HOST + "/_ah/OAuthAuthorizeToken?oauth_token="+requestToken.getToken();
//	Runtime.getRuntime().exec("firefox "+requestURL);
//	System.out.println("opening " + requestURL);
//	System.out.println("received verifier token:"+server.getAuthToken());
//	Verifier verifier = new Verifier(server.getAuthToken());
//	Token accessToken = service.getAccessToken(requestToken, verifier);
	Token accessToken = new Token("1/dzwTKiqIm8DvYypYdSWK1LU09appvZ9RrbQ-p0fbmPw","V84BVByk7xEqDnkVsAznIMCz" );
	System.out.println("received accessToken: "+accessToken);
	
	
	OAuthRequest request = new OAuthRequest(Verb.GET, "http://"+HOST+"/links/get" );
	service.signRequest(accessToken, request);
	Response response = request.send();
	System.out.println("SUCCESS");
	System.out.println(response.getCode());
	System.out.println(response.getBody());
	
    }
}
