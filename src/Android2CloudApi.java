import org.scribe.builder.api.DefaultApi10a;


public class Android2CloudApi extends DefaultApi10a {
    private String host = "android2cloud.appspot.com";
    @Override
    protected String getAccessTokenEndpoint() {
	return  "https://" + host + "/_ah/OAuthGetAccessToken";

    }

    @Override
    protected String getRequestTokenEndpoint() {
	// TODO Auto-generated method stub
	return  "https://" + host + "/_ah/OAuthGetRequestToken";
    }

}
