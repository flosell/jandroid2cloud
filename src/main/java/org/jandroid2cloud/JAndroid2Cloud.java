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
import java.io.File;
import java.io.IOException;

import org.jandroid2cloud.configuration.Configuration;
import org.jandroid2cloud.connection.Android2CloudServerConnection;
import org.jandroid2cloud.exceptions.NetworkException;
import org.jandroid2cloud.linkhandling.LinkConsumer;
import org.jandroid2cloud.ui.MainUI;
import org.jandroid2cloud.ui.notifications.NotificationAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;

public class JAndroid2Cloud {
    private static final File CONFIG_FILE = new File(System.getProperty("user.home")
	    + "/.jandroid2cloud.properties");
    private static final Logger logger = LoggerFactory.getLogger(JAndroid2Cloud.class);

    public static void main(String[] args) {
	configureLogger(args);
	logger.info("Starting JAndroid2Cloud");
	Configuration.initializeInstance(CONFIG_FILE);

	MainUI mainUI = MainUI.getInstance();
	final Configuration configuration = Configuration.getInstance();
	
	Runtime.getRuntime().addShutdownHook(new Thread("Cleanup Thread") {
	    
	    @Override
	    public void run() {
		logger.info("Detected closing of application. Saving configuration.");
		configuration.saveConfiguration(CONFIG_FILE);
	    }
	});
	
	LinkConsumer consumer = new LinkConsumer();
	Thread linkConsumerThread = new Thread(consumer,"Link Consumer");
	linkConsumerThread.setDaemon(true);
	linkConsumerThread.start();
	
	Android2CloudServerConnection connection = new Android2CloudServerConnection(configuration,mainUI.getDisplay());
	
	boolean connected=false;
	while (!connected)
	try {
	    connection.open();
	    connected=true;
	} catch (NetworkException e) {
	    long sleeptime = configuration.getTimeBetweenReconnects();
	    logger.error(NotificationAppender.MARKER,"Could not open connection to server.\nTrying again in "+sleeptime/1000+" seconds",e);
	    try {
		Thread.sleep(sleeptime);
	    } catch (InterruptedException e1) {
		logger.warn("Sleep between two connection results was interrupted. This is strange but should not be a problem",e1);
	    }
	}
    }

    private static void configureLogger(String[] args) {
	ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	for (int i=0;i<args.length;i++) {
	    if (args[i].equalsIgnoreCase("-debug")) {
		rootLogger.setLevel(Level.DEBUG);
	    }
	    if (args[i].equalsIgnoreCase("-logfile")) {
		// TODO: this does not work
		FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
		PatternLayoutEncoder encoder =new PatternLayoutEncoder();
		encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		fileAppender.setEncoder(encoder);
		String filename = args[++i]; 
		Context context = rootLogger.getLoggerContext();
		fileAppender.setContext(context);
		fileAppender.setFile(filename);
//		fileAppender.setName("fileAppender");
		rootLogger.addAppender(fileAppender);
		boolean b=rootLogger.isAttached(fileAppender);
		fileAppender.start();
		logger.debug("logging to file "+filename+" attached: "+b);
	    }
	    
	}
	
    }

    
}
