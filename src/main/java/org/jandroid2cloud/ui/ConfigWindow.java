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
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jandroid2cloud.configuration.Configuration;

public class ConfigWindow {
    public ConfigWindow(final Display display) {
	final Shell shell = new Shell(display);
	shell.setText("JAndroid2Cloud - Configuration");
	shell.setLayout(new FillLayout());
	Composite mainComposite = new Composite(shell, SWT.NONE);
	mainComposite.setLayout(new GridLayout(3, false));

	Label lblDesc = new Label(mainComposite,SWT.LEFT|SWT.WRAP);
	lblDesc.setText("Please set the command to your favourite browser. ");
	GridData data = new GridData();
	data.horizontalSpan=((GridLayout)mainComposite.getLayout()).numColumns;
	data.horizontalAlignment=SWT.FILL;
	data.grabExcessHorizontalSpace=true;
//	data.grabExcessVerticalSpace=true;
	data.verticalAlignment=SWT.FILL;
	lblDesc.setLayoutData(data);
	
//	Label browserLabel = new Label(mainComposite, SWT.NONE);
//	browserLabel.setText("Browser");

	final Text browserText = new Text(mainComposite, SWT.NONE);
	final Configuration config = Configuration.getInstance();
	browserText.setText(config.getBrowserCMD());
	 data =new GridData();
	data.widthHint=300;
	browserText.setLayoutData(data);
	Button browserButton = new Button(mainComposite, SWT.PUSH);
	browserButton.setText("Browse...");
	browserButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		FileDialog fd = new FileDialog(shell);
//		fd.setFilterExtensions(new String[] { "*.exe", "*.*" });
		String newFile = fd.open();
		if (newFile != null) {
		    browserText.setText(newFile);
		}
	    }
	});
	
	Button testButton = new Button(mainComposite,SWT.PUSH);
	testButton.setText("Test");
	testButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        String oldCmd = config.getBrowserCMD();
	        config.setCmd(browserText.getText());
	        config.openURLinBrowser("http://code.google.com/p/android2cloud/");
	        config.setCmd(oldCmd);
	    }
	});
	
	Button okButton = new Button(mainComposite,SWT.PUSH);
	okButton.setText("Ok");
	okButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		config.setCmd(browserText.getText());
		shell.close();
	    }
	});
	data = new GridData();
	data.grabExcessHorizontalSpace=true;
	data.horizontalSpan=1;
	data.horizontalAlignment=SWT.FILL;
	okButton.setLayoutData(data);
	
	Button cancelButton = new Button(mainComposite,SWT.PUSH);
	cancelButton.setText("Cancel");
	cancelButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		shell.close();
	    }
	});
	data = new GridData();
	data.horizontalSpan=2;
	data.horizontalAlignment=SWT.FILL;
	data.grabExcessHorizontalSpace=true;

	cancelButton.setLayoutData(data);
	
	shell.pack();
	shell.open();

    }

    public static void main(String[] args) {
	Display display = new Display();
	ConfigWindow window = new ConfigWindow(display);
	while (!display.isDisposed()) {
	    if (!display.readAndDispatch())
		display.sleep();
	}
	display.dispose();

    }
}
