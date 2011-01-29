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

package org.GAEChannel4j.examples.chat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import org.GAEChannel4j.ChannelHandlerAdapter;
import org.GAEChannel4j.Connection;
import org.GAEChannel4j.IChannelHandler;
import org.jandroid2cloud.http.HTTPConnectionHelper;

public class MyFirstChatbot {
    public static void main(String[] args) throws MalformedURLException, IOException,
	    InterruptedException {
	HTTPConnectionHelper httpHelp = new HTTPConnectionHelper("floselltest.appspot.com", 80);
	String result = httpHelp.post("/action/login?attr=foo", "nick=bot".getBytes())
		.getAsString();
	String token = result.substring(result.indexOf("token=") + "token=".length(),
		result.indexOf("\";"));
	System.out.println("Extracted token " + token);
	IChannelHandler handler = new ChannelHandlerAdapter() {

	    @Override
	    public void open() {
		System.out.println("opened connection. Waiting for messages");
	    }

	    @Override
	    public void message(String msg) {
		System.out.println("<" + msg);
	    }
	};

	Connection connection = new Connection(token);
	connection.setHandler(handler);
	connection.open();
	String s = "";
	Scanner scanner = new Scanner(System.in);
	while (!s.equals("stop")) {
	    HTTPConnectionHelper newHelp = new HTTPConnectionHelper("floselltest.appspot.com", 80);
	    s = scanner.nextLine();
	    System.out.println(newHelp.post("/action/send?msg=" + s.replace(' ', '+'), new byte[0])
		    .getAsString());

	}
	connection.close();
    }
}
