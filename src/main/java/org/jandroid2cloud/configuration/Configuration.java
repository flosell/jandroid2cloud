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

package org.jandroid2cloud.configuration;

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

import org.jandroid2cloud.ui.notifications.NotificationAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
    private static final String DEFAULT_API_KEY = "dev.android2cloud.org";
    private static final String DEFAULT_API_SECRET = "RnPKDZCnYy/ccr8STpe8ASL7";
    private static final String DEFAULT_FIREFOX_CMD = "firefox %url";
    private static final String DEFAULT_HOST = "android2cloud.appspot.com";
    private static final String DEFAULT_IDENTIFIER = "Chrome"; // TODO: change?
    private static final String KEY_API_KEY = "apiKey";
    private static final String KEY_API_SECRET = "apiSecret";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_HOST = "host";
    private static final String KEY_IDENTIFIER = "identifier";
    private static final String KEY_OLDLINK = "lastLink";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_TOKEN = "token";
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static Configuration INSTANCE;

    public static void initializeInstance(File file) {
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
	    config.setApiKey(properties.getProperty(KEY_API_KEY));
	    config.setIdentifier(properties.getProperty(KEY_IDENTIFIER));
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    // e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	setInstance(config);
    }

    private static void setInstance(Configuration config) {
	INSTANCE = config;
	logger.info("using the following configuration:\n" + config.toString());

    }

    public synchronized static Configuration getInstance() {
	if (INSTANCE == null) {
	    return new Configuration();
	} else {
	    return INSTANCE;
	}
    }

    private String apiKey;
    private String apiSecret;

    private String cmd;

    private String host;

    /**
     * The name of the client, e.g. Chrome, myotherclient or whatever else
     */
    private String identifier;

    private String oldlink;

    private String secret;
    private String token;

    private Configuration() {
	host = DEFAULT_HOST;
	cmd = "";
	apiSecret = DEFAULT_API_SECRET;
	token = "";
	secret = "";
	apiKey = DEFAULT_API_KEY;
	identifier = DEFAULT_IDENTIFIER;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
	return apiKey;
    }

    public String getApiSecret() {
	return apiSecret;
    }

    public String getBrowserCMD() {
	return cmd;
    }

    public String getHost() {
	return host;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
	return identifier;
    }

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

    public String getSecret() {
	return secret;
    }

    public String getToken() {
	return token;
    }

    public void openURLinBrowser(String url) {
	
	boolean fail = false;
	if (getBrowserCMD() != null && getBrowserCMD().length() > 0) { 
	    try {
		String cmd2 = getBrowserCMD().replace("%url", url);
		Runtime.getRuntime().exec(cmd2);
	    } catch (IOException e) {
		logger.error(NotificationAppender.MARKER,"Exception while opening configured browser browser.\nSee log for details.\nTrying system standard-browser...",e);
		fail = true;
	    }
	} else {
	    fail = true;
	}
	if (fail) {
	    if (Desktop.isDesktopSupported()) {
		try {
		    Desktop.getDesktop().browse(new URI(url));
		} catch (IOException e) {
		    logger.error(NotificationAppender.MARKER,"Exception while opening standard browser. See log for details.",e);
		} catch (URISyntaxException e) {
		    logger.error(NotificationAppender.MARKER,"Trying to open invalid url. See log for details.",e);

		}
	    } else {
		logger.error(NotificationAppender.MARKER,"No valid browser command configured and not able to open standard browser");
	    }
	}
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
	    properties.setProperty(KEY_API_KEY, getApiKey());
	    properties.setProperty(KEY_IDENTIFIER, getIdentifier());
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

    /**
     * @param apiKey
     *            the apiKey to set
     */
    public void setApiKey(String apiKey) {
	if (apiKey != null) {
	    this.apiKey = apiKey;
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
     * @param cmd
     *            the cmd to set
     */
    public void setCmd(String cmd) {
	if (cmd != null) {
	    if (!cmd.contains("%url")) {
		cmd += " %url";
	    }
	    this.cmd = cmd;
	}
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
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(String identifier) {
	if (identifier != null) {
	    this.identifier = identifier;
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

    /**
     * @param secret
     *            the secret to set
     */
    public void setSecret(String secret) {
	if (secret != null) {
	    this.secret = secret;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Configuration [apiKey=" + apiKey + ", apiSecret=" + apiSecret + ", cmd=" + cmd
		+ ", host=" + host + ", identifier=" + identifier + ", oldlink=" + oldlink
		+ ", secret=" + secret + ", token=" + token + "]"; // TODO:
								   // improve?
    }

    public int getTimeBetweenLinks() {
	return 1000; // TODO: make configurable
    }

}
