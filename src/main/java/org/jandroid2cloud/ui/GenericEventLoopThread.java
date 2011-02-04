package org.jandroid2cloud.ui;

import java.util.concurrent.Semaphore;

import org.eclipse.swt.widgets.Display;

public class GenericEventLoopThread extends Thread{
    private boolean done=false;
    private Display display;
    private Semaphore sema = new Semaphore(0);
    
    public GenericEventLoopThread() {
	setName("GenericEventLoop");
    }
    @Override
    public void run() {
	display=Display.getDefault();
	sema.release();
	while (!done) {
	    if (!display.readAndDispatch())  {
		display.sleep();
	    }
	}
    }
    
    public void stopEventLoop() {
	done=true;
	display.wake();
    }

    public Display getDisplay() {
	try {
	    sema.acquire();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	sema.release();
	return display;
    }
}
