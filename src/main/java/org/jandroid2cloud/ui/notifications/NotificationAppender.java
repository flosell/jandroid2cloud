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

package org.jandroid2cloud.ui.notifications;

import org.eclipse.swt.SWT;
import org.jandroid2cloud.ui.MainUI;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class NotificationAppender extends AppenderBase<ILoggingEvent> {
    public static Marker MARKER = MarkerFactory.getMarker("NOTIFY");

    @Override
    protected void append(ILoggingEvent eventObject) {
	Marker marker = eventObject.getMarker();
	if (marker != null && marker.contains(MarkerFactory.getMarker("NOTIFY"))) {
	    int icon = eventObject.getLevel().equals(Level.ERROR) ? SWT.ICON_ERROR
		    : SWT.ICON_INFORMATION;
	    MainUI.getInstance()
		    .showNotification("JAndroid2Cloud", eventObject.getMessage(), 5000, icon);
	}
    }

}
