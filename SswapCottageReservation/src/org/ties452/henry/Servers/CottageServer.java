package org.ties452.henry.Servers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openrdf.repository.*;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFFormat;

public class CottageServer {
	private String RemoteServer_url = "http://localhost:9090/openrdf-sesame";
	private String Repository_id = "ties452";
	private String FilePath = "./src/..../RDGFiles/random.txt";
	public final static int FILE_SIZE = 6022386;

	private ObjectOutputStream output;
	/**
	 * the input objectInputStream
	 */
	private ObjectInputStream input;
	/**
	 * the server socket
	 */
	private ServerSocket server;
	/**
	 * 
	 */
	private Socket connection;

	FileInputStream fis = null;
	BufferedInputStream bis = null;
	int bytesRead;
	int current = 0;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;

	public CottageServer() {
		super();
		// TODO Auto-generated constructor stub
		connectToSesameRepository(RemoteServer_url, Repository_id); // connect
																	// to sesame
																	// repository
	}

	public void startRunning() {
		try {
			server = new ServerSocket(6789, 100);
			while (true) {
				try {
					waitForConnection(); // wait for someone to connect with
											// you!
					setUpStreams();// set up the input and output stream..
					WhileCommunicating();//used to send messages back and forth
				} catch (EOFException e) {
					System.out.println("\n server ended the connection!");
				} finally {
					closeStream(); // cleans up reserved resources..
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// end of the startRunning method

	/**
	 * Waits for a connection from any client
	 * 
	 * @throws IOException
	 */
	private void waitForConnection() throws IOException {
		System.out.println("waiting for someone to connect...");
		connection = server.accept();
		System.out.println("Now connected to "
				+ connection.getInetAddress().getHostName());

	}
	
	/**
	 * Communication channel between two sockets
	 */
	
	public void WhileCommunicating(){
		
		sendRDG();//send the RDG
		//GetRIG(); //gets the RIG
		//sendRRG(); //sends the RRG
		
		
	}

	/**
	 * Sets up the IO streams
	 * 
	 * @throws IOException
	 */
	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream()); // send
																		// stream..
		output.flush();// flush the extra bytes of infos...
		input = new ObjectInputStream(connection.getInputStream()); // get
																	// stream
		System.out.println("\n streams are now setup!");
	}

	/**
	 * close open streams
	 */
	private void closeStream() {
		System.out.println("\n closing connection.." + "\n");
		try {
			output.close();
			input.close();
			connection.close();
			System.out.println("connection closed now!" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends an RDG file
	 */
	private void sendRDG() {
		URL File_Path = this.getClass().getResource(
				"/org/tie452/henry/RDGFiles/random.txt");
		File myFile = new File(File_Path.getFile());
		byte[] mybytearray = new byte[(int) myFile.length()];
		try {
			fis = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bis = new BufferedInputStream(fis);
		try {
			bis.read(mybytearray, 0, mybytearray.length);
			output = (ObjectOutputStream) connection.getOutputStream();
			System.out.println("Sending RDF file right now....");
			output.write(mybytearray, 0, mybytearray.length);
			output.flush();
			System.out.println("Done.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} //

	/**
	 * Receives an RIG file
	 */
	private void GetRIG() {
		URL File_Path = this.getClass().getResource(
				"/org/tie452/henry/RDGFiles/random.txt");
		byte[] mybytearray = new byte[FILE_SIZE];
		InputStream is = null;
		try {
			is = connection.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos = new FileOutputStream(File_Path.getFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bos = new BufferedOutputStream(fos);
		try {
			bytesRead = is.read(mybytearray, 0, mybytearray.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		current = bytesRead;

		do {
			try {
				bytesRead = is.read(mybytearray, current,
						(mybytearray.length - current));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bytesRead >= 0)
				current += bytesRead;
		} while (bytesRead > -1);

		try {
			bos.write(mybytearray, 0, current);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("File " + File_Path.getFile() + " downloaded ("
				+ current + " bytes read)");
	}

	
	
	/**
	 * connects to the sesame repository
	 * 
	 * @param RemoteServer_url
	 *            server URL
	 * @param Repository_id
	 *            Repository ID
	 */

	private void connectToSesameRepository(String RemoteServer_url,
			String Repository_id) {
		RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(
				RemoteServer_url);

		// connect to a server side..
		Repository repo = new HTTPRepository(RemoteServer_url, Repository_id);
		try {
			// repositoryManager.initialize();
			repo.initialize();
			RepositoryConnection con = repo.getConnection();
			System.out.println("Connection was established successfully!");
		} catch (RepositoryException e) {
			e.printStackTrace();

		}

	}

}
