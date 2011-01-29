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

package org.jandroid2cloud.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * This class is a wrapper to use GET and POST more conveniently
 */
public class HTTPConnectionHelper {
    private static final int DEFAULT_TIMEOUT = 5000; // TODO: use configuration
    // file instead
    private int timeout;
    private String host;
    private int port;

    public HTTPConnectionHelper(String host,int port) {
	this.host = host;
	this.port=port;
    }

    public HTTPResult get(String path) throws MalformedURLException, IOException {
	URLConnection con = getURLConnection(path,port);
	return new HTTPResult(con);
    }

    private URLConnection getURLConnection(String path, int port) throws MalformedURLException, IOException {
	String url = "http://"+host +":"+port+ path; // TODO: checking
	URLConnection con;
	con = (new URL(url)).openConnection();
	con.setConnectTimeout(getTimeout()); // TODO:Macht das sinn? Connection bereits offen?
	con.setReadTimeout(getTimeout());
	return con;
    }

    public String getHost() {
	return host;
    }

    public int getTimeout() {
	if (timeout < 0) {
	    return DEFAULT_TIMEOUT;
	} else
	    return timeout;
    }

    public HTTPResult post(String path, byte[] data) throws MalformedURLException, IOException {
	URLConnection con = getURLConnection(path,port);
	con.setDoOutput(true);

	for (byte b : data) {
	    con.getOutputStream().write(b);
	}
	// TODO: braucht man das? macht es probleme?
	con.getOutputStream().flush();
	con.getOutputStream().close();
	return new HTTPResult(con);

    }

    public void setTimeout(int timeout) {
	this.timeout = timeout;
    }
}


