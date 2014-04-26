package org.ties452.henry.Servers;

import java.net.URL;

public class StartCottageServer {

	/**
	 * @param args
	 */
	
	private final static String FILEPATH = "src/org/tie452/henry/RDGFiles/random.txt";
	public static void main(String[] args) {
		CottageServer server = new CottageServer();
		URL File_Path = server.getClass().getClassLoader().getResource(FILEPATH);
		System.out.println(File_Path); //getting null ..
		server.startRunning();
		

	}

}
