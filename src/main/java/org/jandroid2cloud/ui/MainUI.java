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
	TrayItem trayItem = new TrayItem(tray,SWT.NONE);
	trayItem.setText("JAndroid2Cloud");
	trayItem.setImage(new Image(display,JAndroid2Cloud.class.getResourceAsStream("/logo.gif")));
	final Menu menu = new Menu(shell,SWT.POP_UP);
	MenuItem item = new MenuItem(menu,SWT.PUSH);
	item.setText("Exit");
	item.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		System.exit(0);
	    }
	});
	final ToolTip tooltip = new ToolTip(shell, SWT.BALLOON|SWT.ICON_INFORMATION);
	tooltip.setMessage("hello");
	tooltip.setText("world");
	trayItem.setToolTip(tooltip);
	tooltip.setLocation(100,100);
	item = new MenuItem(menu, SWT.PUSH);
	item.setText("Helloworld");
	item.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		showNotification("Hello","World text",5000);
	    }

	});
	
	trayItem.addMenuDetectListener(new MenuDetectListener() {
	    
	    @Override
	    public void menuDetected(MenuDetectEvent e) {
		menu.setVisible(true);
	    }
	});
	
    }
    
    public void showNotification(String title,String msg, int msec) {
	final BalloonWindow win = new BalloonWindow(shell,SWT.TITLE);
	Composite composite = win.getContents();
	composite.setLayout(new FillLayout());
	Label l = new Label(composite,SWT.CENTER);
	final Color c = new Color(shell.getDisplay(), 255, 255, 225);
	l.setBackground(c);
	l.setText(msg);
	
	composite.pack(true);
	win.setText(title);
	
	display.timerExec(msec, new Runnable() {
	    public void run() {
		win.close();
	    }
	});
	
	Point position = new Point(display.getBounds().width,display.getBounds().height);
	logger.debug("Showing notification at position: {}. Title:{} Message: {} Showing for {} milliseconds. ",new Object[]{position,title,msg,msec});
	win.setLocation(position);
	
	win.open();
    }
    
    
//    private static void createSystemTray() {
//	if (SystemTray.isSupported()) {
//	    SystemTray tray = SystemTray.getSystemTray();
//	    
//	    PopupMenu menu = new PopupMenu();
//	    MenuItem exitItem = new MenuItem("Exit");
//	    exitItem.addActionListener(new ActionListener() {
//	        @Override
//	        public void actionPerformed(ActionEvent arg0) {
//	            System.exit(0);
//	        }
//	    });
//	    
//	    menu.add(exitItem);
//	    
//	    
//	    TrayIcon trayicon;
//	    try {
//		trayicon = new TrayIcon(ImageIO.read(JAndroid2Cloud.class.getResource("/logo.gif")),"JAndroid2Cloud",menu);
//		trayicon.setImageAutoSize(true);
//		tray.add(trayicon);
//	    } catch (IOException e1) {
//		logger.error("Error while setting up tray-icon",e1);
//	    } catch (AWTException e) {
//		logger.error("Error while setting up tray-icon",e);
//	    }
//	} else {
//	    logger.error("System Tray is not supported!"); 
//	    // FIXME: what happens here?
//	}
//    }
    
    public Display getDisplay() {
	return display;
    }
    
    public void executeEventLoop() {
	while (!tray.isDisposed()) {
		if (!display.readAndDispatch()) {
		    // If no more entries in event queue
		    display.sleep();
		}
	    }
	    display.dispose();
    }
}
