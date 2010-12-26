import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class Configuration {
    private static final String KEY_TOKEN = "token";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_HOST = "host";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_API_SECRET = "apiSecret";
    private static final String DEFAULT_FIREFOX_CMD = "firefox %url";
    private static final String DEFAULT_HOST = "android2cloud.appspot.com";
    private static final String DEFAULT_API_SECRET = "ySS8cOHGuNgNS9qLsxUhQBH/";
    private static final String KEY_OLDLINK = "lastLink";

    private String host;
    private String cmd;
    private String apiSecret;
    private String token;
    private String secret;
    private String oldlink;

    /**
     * @return the oldlink
     */
    public String getOldlink() {
	if (oldlink == null) {
	    return "";
	} else {
	    return oldlink;
	}
    }

    /**
     * @param oldlink
     *            the oldlink to set
     */
    public void setOldlink(String oldlink) {
	if (oldlink != null) {
	    this.oldlink = oldlink;
	}
    }

    public static Configuration getConfiguration(File file) {
	Configuration config = new Configuration();

	Properties properties = new Properties();

	try {
	    BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
	    properties.load(stream);
	    stream.close();
	    config.setApiSecret(properties.getProperty(KEY_API_SECRET));
	    config.setCmd(properties.getProperty(KEY_CMD));
	    config.setHost(properties.getProperty(KEY_HOST));
	    config.setSecret(properties.getProperty(KEY_SECRET));
	    config.setToken(properties.getProperty(KEY_TOKEN));
	    config.setOldlink(properties.getProperty(KEY_OLDLINK));
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    // e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return config;
    }

    public void saveConfiguration(File file) {
	Writer writer = null;
	try {
	    writer = new FileWriter(file);
	    Properties properties = new Properties();
	    properties.setProperty(KEY_HOST, getHost());
	    if (getBrowserCMD() != null) {
		properties.setProperty(KEY_CMD, getBrowserCMD());
	    }
	    properties.setProperty(KEY_API_SECRET, getApiSecret());
	    properties.setProperty(KEY_TOKEN, getToken());
	    properties.setProperty(KEY_SECRET, getSecret());
	    properties.setProperty(KEY_OLDLINK, getOldlink());

	    properties
		    .store(writer,
			    "This file stores the information for JAndroid2Cloud. "
				    + "\nlastLink,Token and Secret are just cached values for convenience.\n\n"
				    + "To configure a browser other than the OS Default Browser set cmd. \n"
				    + "It is supposed to be a command in which %url will be replaced with the URL received from the Android2Cloud server.\n"
				    + "Example \"cmd=firefox %url\" uses firefox on a Linux System. On Windows, the full path might be required.");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		writer.close();
	    } catch (Exception e) {
	    }
	}
    }

    private Configuration() {
	host = DEFAULT_HOST;
	cmd = null;
	apiSecret = DEFAULT_API_SECRET;
	token = "";
	secret = "";
    }

    public void openURLinBrowser(String url) {
	if (getBrowserCMD() != null) { // TODO: add support for default when
				       // desktop not available
	    try {
		Runtime.getRuntime().exec(getBrowserCMD().replace("%url", url));
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else if (Desktop.isDesktopSupported()) {
	    try {
		Desktop.getDesktop().browse(new URI(url));
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {
	    System.err.println("Desktop not supported. Please set cmd manually");
	}
    }

    private String getBrowserCMD() {
	return cmd;
    }

    public String getHost() {
	return host;
    }

    public String getToken() {
	return token;
    }

    public String getSecret() {
	return secret;
    }

    public String getApiSecret() {
	return apiSecret;
    }

    public static Configuration getDefaultConfig() {
	return new Configuration();
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
	if (host != null) {
	    this.host = host;
	}
    }

    /**
     * @param cmd
     *            the cmd to set
     */
    public void setCmd(String cmd) {
	if (cmd != null) {
	    this.cmd = cmd;
	}
    }

    /**
     * @param apiSecret
     *            the apiSecret to set
     */
    public void setApiSecret(String apiSecret) {
	if (apiSecret != null) {
	    this.apiSecret = apiSecret;
	}
    }

    /**
     * @param token
     *            the token to set
     */
    public void setToken(String token) {
	if (token != null) {
	    this.token = token;
	}
    }

    /**
     * @param secret
     *            the secret to set
     */
    public void setSecret(String secret) {
	if (secret != null) {
	    this.secret = secret;
	}
    }

}
