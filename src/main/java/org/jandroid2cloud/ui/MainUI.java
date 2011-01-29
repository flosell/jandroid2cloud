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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novocode.naf.swt.custom.BalloonWindow;

public class MainUI {
    private static final Logger logger = LoggerFactory.getLogger(MainUI.class);
    public static MainUI INSTANCE = new MainUI();
    private Display display;
    private Tray tray;
    private Shell shell;

    private MainUI() {
	display = Display.getDefault();
	createSystemTray();
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
	item.setText("Exit");
	item.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		System.exit(0);
	    }
	});

	trayItem.addMenuDetectListener(new MenuDetectListener() {

	    @Override
	    public void menuDetected(MenuDetectEvent e) {
		menu.setVisible(true);
	    }
	});

    }

    public void showNotification(String title, String msg, int msec, int iconInformation) {
	final BalloonWindow win = new BalloonWindow(shell, SWT.TITLE);
	Composite composite = win.getContents();
	composite.setLayout(new FillLayout());
	Label l = new Label(composite, SWT.LEFT);
	final Color c = new Color(shell.getDisplay(), 255, 255, 225);
	l.setBackground(c);
	l.setText(msg);

	if (iconInformation == SWT.ICON_INFORMATION) {
	    win.setImage(new Image(display, JAndroid2Cloud.class.getResourceAsStream("/info.gif")));
	} else if (iconInformation == SWT.ICON_ERROR) {
	    win.setImage(new Image(display, JAndroid2Cloud.class.getResourceAsStream("/error.gif")));
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

    // private static void createSystemTray() {
    // if (SystemTray.isSupported()) {
    // SystemTray tray = SystemTray.getSystemTray();
    //
    // PopupMenu menu = new PopupMenu();
    // MenuItem exitItem = new MenuItem("Exit");
    // exitItem.addActionListener(new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent arg0) {
    // System.exit(0);
    // }
    // });
    //
    // menu.add(exitItem);
    //
    //
    // TrayIcon trayicon;
    // try {
    // trayicon = new
    // TrayIcon(ImageIO.read(JAndroid2Cloud.class.getResource("/logo.gif")),"JAndroid2Cloud",menu);
    // trayicon.setImageAutoSize(true);
    // tray.add(trayicon);
    // } catch (IOException e1) {
    // logger.error("Error while setting up tray-icon",e1);
    // } catch (AWTException e) {
    // logger.error("Error while setting up tray-icon",e);
    // }
    // } else {
    // logger.error("System Tray is not supported!");
    // // FIXME: what happens here?
    // }
    // }

    public Display getDisplay() {
	return display;
    }

    public void executeEventLoop() {
	while (!tray.isDisposed()) {
//	    logger.debug("In main event loop");
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	display.dispose();
    }
}
