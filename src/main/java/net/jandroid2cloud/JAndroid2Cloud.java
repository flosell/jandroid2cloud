/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2010 Florian Sellmayr
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

package net.jandroid2cloud;
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

public class JAndroid2Cloud {
    private static final File CONFIG_FILE = new File(System.getProperty("user.home")
	    + "/.jandroid2cloud.properties");

    public static void main(String[] args) {
	final Configuration configuration = Configuration.getConfiguration(CONFIG_FILE);
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		configuration.saveConfiguration(CONFIG_FILE);
		System.out.println("saved configuration");
	    }
	});

	systemTray();

	Android2CloudServerConnection connection = new Android2CloudServerConnection(configuration);
	while (true) {
	    String link = connection.getLink();
	    if (!link.equals(configuration.getOldlink())) {
		configuration.openURLinBrowser(link);
//		System.out.println((new Date()).toString() + " found new link:" + link);
		configuration.setOldlink(link);
	    } else {
//		System.out.println((new Date()).toString() + " nothing new...");
	    }
	    try {
		Thread.sleep(15000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    private static void systemTray() {
	if (SystemTray.isSupported()) {
	    SystemTray tray = SystemTray.getSystemTray();
	    
	    PopupMenu menu = new PopupMenu();
	    MenuItem exitItem = new MenuItem("Exit");
	    exitItem.addActionListener(new ActionListener() {
	        
	        @Override
	        public void actionPerformed(ActionEvent arg0) {
	            System.exit(0);
	        }
	    });
	    
	    menu.add(exitItem);
	    
	    
	    TrayIcon trayicon;
	    try {
		trayicon = new TrayIcon(ImageIO.read(JAndroid2Cloud.class.getResource("/logo.gif")),"JAndroid2Cloud",menu);
		trayicon.setImageAutoSize(true);
		tray.add(trayicon);
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    } catch (AWTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
	}
    }
}
