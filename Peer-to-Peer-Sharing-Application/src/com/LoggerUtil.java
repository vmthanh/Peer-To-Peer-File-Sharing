package com;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
public static final String LOGGERNAME = "peerbase.logging";
	
	static {
		Logger.getLogger(LOGGERNAME).setUseParentHandlers(false);
		Handler handler = new ConsoleHandler();
		handler.setLevel(Level.INFO);
		Logger.getLogger(LOGGERNAME).addHandler(handler);
	}
	
	public static void setHandlersLevel( Level level ) {
		Handler[] handlers = 
			Logger.getLogger( LOGGERNAME ).getHandlers();
		for (Handler h : handlers) 
			h.setLevel(level);
		
		Logger.getLogger( LOGGERNAME ).setLevel(level);
	}
	
	public static Logger getLogger() {
		return Logger.getLogger(LOGGERNAME);
	}

}
