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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;


/**
 * Represents the result of a HTTP-Connection. Every the getters can be used
 * multiple times. <br>
 * <b>Note:</b>Keep in mind that the whole result is read from the Connection at creation and is kept in memory.
 * 
 * @author florian
 * 
 */

public class HTTPResult  {
    private URLConnection connection;

    /**
     * This constructor reads the full content from the URL-Connection and
     * creates a HTTP-Result from it. After reading the connection will be
     * closed.
     * 
     * @param connection
     *            the connection
     * @throws IOException
     *             if something goes wrong while reading from the connection.
     * @throws FileNotFoundException if the wrong url was given.
     */
    public HTTPResult(URLConnection connection) throws IOException {
	this.connection =connection;
    }


    public String getAsString() {
	if (connection==null) {
	    return null;
	}
	BufferedReader br;
	try {
	    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    String result = "";
	    String s = "";
	    while ((s = br.readLine()) != null)
		result += s + "\n";
	    return result;
	} catch (IOException e) {
	    // There should never be an IO-Exception in a Byte-Array
	    throw new RuntimeException(e);
	}
    }


}
