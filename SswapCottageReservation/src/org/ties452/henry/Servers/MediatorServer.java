package org.ties452.henry.Servers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import info.sswap.api.http.HTTPProvider;
import info.sswap.api.model.*;
import info.sswap.impl.empire.Vocabulary;
import info.sswap.impl.empire.model.ModelUtils;
import info.sswap.ontologies.sswapmeet.SSWAPMeet.Exec;
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

/**
 * Servlet implementation class ReservationServlet
 */
@WebServlet(description = "This servlet makes reservation for users searching for a summer cottage", urlPatterns = { "/ReservationServlet" })
public class MediatorServer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String RemoteServer_url = " http://localhost:8080/Reservation/getAvaliableCottageService";
	private String Repository_id = "ties452";
	public final static int FILE_SIZE = 6022386;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private String message = "";
	private String serverIP;
	FileInputStream fis = null;
	BufferedInputStream bis = null;
	int bytesRead;
	int current = 0;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;


	private static final String bookingNumber = "ReservationID";
	private static final String bookerFirstName = "Edris";
	private static final String reservationStartDate = "2014-07-02";
	private static final String reservationEndDate = "2014-07-12";
	private static final String cottageAddress = "";
	private static final String cottageCity = "Jyvaskyla";
	private static final String cottageDistanceToNearCity = "4km";
	private static final String cottageDistanceToNearLake = "3km";
	private static final String cottageImage = "http://localhost:8080/reservationService/images/1.gif";
	private static final String cottageName = "coconutHeaven";
	private static final String cottageOccupancy = "";
	private static final String cottageOccupancyBedrooms = "6";
	private static final String cottageAvalaibleEndDate = "2014-06-03";
	private static final String cottageAvalaibleStartDate = "2014-06-03";
	private static final String maximumShift = "+/-3";
	private static final String ReservationService = "http://localhost:8080/Reservation/getAvaliableCottageService";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MediatorServer() {
		super();
		// TODO Auto-generated constructor stub
		startRunning();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//connect to the CottageRepository
		connectToCottageServer(RemoteServer_url,Repository_id);
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		//
		
		//ParseRDG();
		//SendToBrowser();
		//createRIG();
		//sendRIG();
		//ReceiveRRG();
		//SendToBrowser();

		

	}
	
	public void startRunning(){
		try{
			connectToCottageServer();
			setupStream();
			WhileCommunicating();//used to send messages back and forth
		}catch(IOException e){
			
		}
}
	
	/**
	 * communication channel method
	 */
public void WhileCommunicating(){
		
	     getRDG(); //get the RDG file.
		//sendRIG(); //sends the RIG
	    //GetRRG(); //gets the RRRG
		
	}

	/**
	 * connects to the cottage server
	 */
	private void connectToCottageServer(){
		System.out.println("connecting to a server...");
		   try {
			connection = new Socket(InetAddress.getByName(serverIP),6789);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   System.out.println("you are connected! " + connection.getInetAddress().getHostName());
		    
		 //setupStream();
	}
	
	/**
	 * setup the output and input streams..
	 */
	private void setupStream()throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		System.out.println("\nstreams are now connected!");
	}
	
	/**
	 * get the RDG
	 */
	public void getRDG(){
		
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
	
	private void connectToCottageServer(String RemoteServer_url,
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
