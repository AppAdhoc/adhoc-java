package com.appadhoc.javasdk;

import java.util.logging.Level;
import java.util.logging.Logger;

public class T {
    private static String AppName = "ADHOC_SDK";
    private static Logger log = Logger.getLogger(AppName);
    public static Boolean DEBUG = false;

    public static void i(String string) {
        log.setLevel(Level.INFO);
        if (DEBUG) {
            log.info(string);
        }
    }


    public static void w(String string) {
        log.setLevel(Level.WARNING);
        if (DEBUG) {
            log.warning(string);
        }
    }

    public static void e(Exception exception) {
        log.setLevel(Level.SEVERE);
//        if(DEBUG){
            log.severe(exception.toString());
//        }
    }

    public static void e(String exception) {
        log.setLevel(Level.SEVERE);
        if(DEBUG){
            log.severe(exception);
        }
    }
}
