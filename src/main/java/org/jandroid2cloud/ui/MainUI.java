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

package org.jandroid2cloud.ui;

import java.util.concurrent.Semaphore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.jandroid2cloud.JAndroid2Cloud;
import org.jandroid2cloud.configuration.Configuration;
import org.jandroid2cloud.ui.notifications.NotificationAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novocode.naf.swt.custom.BalloonWindow;

public class MainUI {
    private static final Logger logger = LoggerFactory.getLogger(MainUI.class);
    private static MainUI INSTANCE;
    private Display display;
    private Tray tray;
    private Shell shell;
    private GenericEventLoopThread eventLoop;
    
    private MainUI() {
	eventLoop = new GenericEventLoopThread();
	eventLoop.start();
	display=eventLoop.getDisplay();
	display.syncExec(new Runnable() {
	    @Override
	    public void run() {
		createSystemTray();
		
	    }
	});
    }
    
    public synchronized static MainUI getInstance() {
	if (INSTANCE==null) {
	    INSTANCE=new MainUI();
	}
	return INSTANCE;
    }

    private void createSystemTray() {
	shell = new Shell(display);
	tray = display.getSystemTray();
	TrayItem trayItem = new TrayItem(tray, SWT.NONE);
	trayItem.setText("JAndroid2Cloud");
	trayItem.setImage(new Image(display, JAndroid2Cloud.class.getResourceAsStream("/logo.gif")));
	final Menu menu = new Menu(shell, SWT.POP_UP);
	MenuItem item = new MenuItem(menu, SWT.PUSH);
	item.setText("Configuration...");
	item.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		new ConfigWindow(display);
	    }

	});
	
	item = new MenuItem(menu, SWT.PUSH);
	item.setText("Reconnect");
	item.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		logger.info(NotificationAppender.MARKER,"Reconnecting now");
		JAndroid2Cloud.connection.reconnect();
	    }
	});
	
	item = new MenuItem(menu, SWT.PUSH);
	item.setText("Relogin");
	item.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		logger.info(NotificationAppender.MARKER,"Cleared session data. Requesting new login.");
		JAndroid2Cloud.connection.reauth();
	    }
	});
	

	item = new MenuItem(menu, SWT.PUSH);
	item.setText("Exit");
	item.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		stopMainUI();
	    }
	});
	
	

	trayItem.addMenuDetectListener(new MenuDetectListener() {
	    @Override
	    public void menuDetected(MenuDetectEvent e) {
		menu.setVisible(true);
	    }
	});

    }

    public void showNotification(final String title, final String msg, final int msec,
	    final int iconInformation) {
	display.asyncExec(new Runnable() {
	    @Override
	    public void run() {
		final BalloonWindow win = new BalloonWindow(shell, SWT.TITLE);
		Composite composite = win.getContents();
		composite.setLayout(new FillLayout());
		Label l = new Label(composite, SWT.LEFT);
		final Color c = new Color(shell.getDisplay(), 255, 255, 225);
		l.setBackground(c);
		l.setText(msg);

		if (iconInformation == SWT.ICON_INFORMATION) {
		    win.setImage(new Image(display, JAndroid2Cloud.class
			    .getResourceAsStream("/info.gif")));
		} else if (iconInformation == SWT.ICON_ERROR) {
		    win.setImage(new Image(display, JAndroid2Cloud.class
			    .getResourceAsStream("/error.gif")));
		}

		composite.pack(true);
		win.setText(title);

		display.timerExec(msec, new Runnable() {
		    public void run() {
			win.close();
		    }
		});

		Point position = new Point(display.getBounds().width, display.getBounds().height);
		logger.debug(
			"Showing notification at position: {}. Title:{} Message: {} Showing for {} milliseconds. ",
			new Object[] { position, title, msg, msec });
		win.setLocation(position);
		win.open();
	    }

	});
    }

    public Display getDisplay() {
	return display;
    }

    public void stopMainUI() {
	eventLoop.stopEventLoop();
    }
}
