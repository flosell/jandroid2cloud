package org.jandroid2cloud.ui.notifications;

import org.jandroid2cloud.ui.MainUI;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class NotificationAppender extends  AppenderBase<ILoggingEvent>{
    public static Marker MARKER = MarkerFactory.getMarker("NOTIFY");

    @Override
    protected void append(ILoggingEvent eventObject) {
	eventObject.getMarker().contains(MarkerFactory.getMarker("NOTIFY"));
	MainUI.INSTANCE.showNotification("JAndroid2Cloud", eventObject.getMessage(), 5000);
    }

}