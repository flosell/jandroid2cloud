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

package org.jandroid2cloud;
import java.awt.AWTException;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import org.jandroid2cloud.configuration.Configuration;
import org.jandroid2cloud.connection.Android2CloudServerConnection;
import org.jandroid2cloud.ui.MainUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAndroid2Cloud {
    private static final File CONFIG_FILE = new File(System.getProperty("user.home")
	    + "/.jandroid2cloud.properties");
    private static final Logger logger = LoggerFactory.getLogger(JAndroid2Cloud.class);

    public static void main(String[] args) {
	logger.info("Starting JAndroid2Cloud");
	Configuration.initializeInstance(CONFIG_FILE);
	final Configuration configuration = Configuration.getInstance();
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		logger.info("Detected closing of application. Saving configuration.");
		configuration.saveConfiguration(CONFIG_FILE);
	    }
	});

	MainUI mainUI = MainUI.INSTANCE;

	Android2CloudServerConnection connection = new Android2CloudServerConnection(configuration,mainUI.getDisplay());
	connection.open();
	mainUI.executeEventLoop();
    }

    
}