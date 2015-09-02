package com.zigapk.wifipassextract;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class Network {
	String ssid;
	String key_mgmt;
	String password;
	String psk;
	String eap;
	String identity;
	String anonymous_identity;

	public static Network[] fromFile(String file) {
        int count = StringUtils.countMatches(file, "network={");
        MainActivity.numberOfNetworks = count;

		if(!file.contains("network={")) return new Network[0];
		file = file.substring(file.indexOf("network={"), file.length());
		Character customNewLine = file.charAt(file.indexOf("{") + 1);
		file = file.replaceAll(customNewLine.toString(), "\n");
		file = file.replaceAll(Pattern.quote("}"), "}\n");

		ArrayList<Network> result = new ArrayList<Network>();
		int start = 0;
		int stop;

		while(true) {
			start = file.indexOf("network={", start);
			if(start == -1) break;
			stop = file.indexOf("}", start) + 1;
			String current = file.substring(start, stop);			
			result.add(parseNetwork(current));
			start = stop;
		}

		return result.toArray(new Network[result.size()]);
	}

	private static Network parseNetwork(String string) {
		Network network = new Network();

		network.ssid = parseNetworkAttribute(string, "ssid", true);
		try {
			network.key_mgmt = parseNetworkAttribute(string, "key_mgmt", false);
		}catch (Exception e){
			System.out.print("sadf");
		}
		network.psk = parseNetworkAttribute(string, "psk", true);
		network.password = parseNetworkAttribute(string, "password", true);
		network.identity = parseNetworkAttribute(string, "identity", true);
		network.anonymous_identity = parseNetworkAttribute(string, "anonymous_identity", true);
		network.eap = parseNetworkAttribute(string, "eap", false);
		
		return network;
	}

	private static String parseNetworkAttribute(String string, String tag, boolean quoted) {
		String startQuote = "=";
		if(quoted) startQuote += "\"";
		String endQuote = "\n";
		if(quoted) endQuote = "\"\n";
		int start = string.indexOf(tag + startQuote);
		if(start == -1) return null;
		start += (tag + startQuote).length();
		int stop = string.indexOf(endQuote, start);

		return string.substring(start, stop);
	}
}
