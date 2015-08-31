package com.zigapk.wifipassextract;
import java.io.*;

public class RootAccess {
	public static void exec(String command) throws NotRootException {
		try {
			Process p;
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes(command + "\n");
			// Close the terminal     
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				if(p.exitValue() != 0) {
					throw new NotRootException();
				}
			} catch(InterruptedException e) {
				throw new NotRootException();
			}
		} catch(IOException e) {
			throw new NotRootException();
		}
	}
}
