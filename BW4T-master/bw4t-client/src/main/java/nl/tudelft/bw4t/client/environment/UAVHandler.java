package nl.tudelft.bw4t.client.environment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import eis.iilang.Numeral;
import eis.iilang.Percept;
import nl.tudelft.bw4t.client.controller.ClientMapController;



/**
 * This is a separate thread fully dedicated to listening to the messages from
 * the NLAGE system. When created it creates a socket and starts listening to
 * the server. Incoming messages are parsed to Prolog and placed on a queue.
 * Using getPrologMessages() you can get them from the queue.
 * 
 * TODO killing the system from two ways.
 * 
 * @author W.Pasman 16jun2011 for the Alize project.
 * 
 */
public class UAVHandler implements Runnable {
	public static final int PORT = 6655;//   
	private Socket socket = null; // non-null when connected.
	private boolean running = true; // thread stops when this gets false.
	BufferedReader in = null;
	OutputStreamWriter out = null;
	Thread mythread;

	public static List<Percept> percepts = new ArrayList<Percept>();
	/**
	 * Calling the constructor launches a listener thread. You should call
	 * {@link #stop()} when you are done with the handler.
	 * 
	 * @throws IOException
	 */
	public UAVHandler() throws IOException {

		ServerSocket serverSocket;
		serverSocket = new ServerSocket(PORT);
		Socket server = serverSocket.accept();  
		socket = server;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new OutputStreamWriter(new Socket("localhost", 6656).getOutputStream()); 
        new Thread(this).start();				
	}

	/**
	 * This is started when the init succeeded and continues running as long as
	 * {@link #running} is true.
	 */
	public void run() {
		 try {    
//			 while (true)
			 {
				 String message=null;		 
				 message = in.readLine();   	
				 //解析传过来的环境数据成percept，并加到list中
				 percepts.addAll(UAVPerceptParser(message));
				 RemoteEnvironment.setPercepts(percepts);
//				 percepts.clear();
			 }
   
         } catch (Exception e) {    
             e.printStackTrace();    
         } finally {    
        	 //closeSockets();   
         }   
	}

	public List<Percept> getPercept()
	{
		return percepts;
		
	}
	private List<Percept> UAVPerceptParser(String ms_uav_percept)
	{

		List<Percept> pecepts = new ArrayList<Percept>();
		
		String[] ms = ms_uav_percept.split(";");
		
		Map data = new HashMap();
		for(String s:ms){
			String[] tmp = s.split(",");
			data.put(tmp[0], tmp[1]);
		}
		
		pecepts.clear();		
		if(data.containsKey("height"))//openness of garbage can
		{ 
//			try {
//				send("climb");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			String value = data.get("height").toString();
		
			pecepts.add(new Percept("height", new Numeral(Float.parseFloat(value))));			
		}
		if(data.containsKey("Latitude"))
		{ 
			String value = data.get("Latitude").toString();
			pecepts.add(new Percept("Latitude", new Numeral(Float.parseFloat(value))));			
		}	
		if(data.containsKey("Longitude"))
		{ 
			String value = data.get("Longitude").toString();
			pecepts.add(new Percept("Longitude", new Numeral(Float.parseFloat(value))));			
		}
		return pecepts;
	}

	/**
	 * Send a message over the socket of the handler.
	 * 
	 * @param message
	 *            is string message to be sent.
	 */
	public void send(String message) throws IOException {
		try {
			out.write(message + "\n");
			out.flush();
		} catch (NullPointerException e) {
			throw new IOException("socket is not connected or was closed.");
		}
	}

	/**
	 * Terminates threads, closes sockets.
	 */
	public void stop() {
		running = false; // theoretically this should stop the thread within a
							// second.
	}

	/**
	 * Get list of currently avaiable percepts. Percepts are all buffered until
	 * GOAL requests them.
	 * 
	 * @return LinkedList with percepts. List may be empty. Note, it seems
	 *         needless to require LinkedList but EIS interface specifies that
	 */
	public LinkedList<Percept> getPercepts() {
		LinkedList<Percept> currentpercepts = new LinkedList<Percept>();
		while (!percepts.isEmpty()) {
//			currentpercepts.add(percepts.poll());
		}
		return currentpercepts;
	}

	/**
	 * Test function. Connects to NLAGE and sends 'hello'.
	 * 
	 * @param args
	 *            not used.
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {

	}
}
