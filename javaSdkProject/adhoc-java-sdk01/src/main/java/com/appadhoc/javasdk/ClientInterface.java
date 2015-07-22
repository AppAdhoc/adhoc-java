package com.appadhoc.javasdk;

/**
 * Client side interface for Adhoc SDK. It defines basic functions to talk to Adhoc server
 * in remote machines.
 */
public interface ClientInterface {
	String send(String targetURL, String urlParameters, OnAdHocReceivedData listener);
}
