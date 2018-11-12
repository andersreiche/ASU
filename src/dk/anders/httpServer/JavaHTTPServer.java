package dk.anders.httpServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

// Each Client Connection will be managed in a dedicated Thread
public class JavaHTTPServer implements Runnable{ 
	
	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "index.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	static final int PORT = 8080;
	
	
	static final boolean DEBUG_ENABLED = true; //debug mode outputs information to console
	private Socket connect; //Client Connection via Socket Class
	
	public JavaHTTPServer(Socket c) {
		connect = c;
	}
	
	public static void main(String[] args) {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			
			
			while (true) { //listen until user halts server execution
				JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());
				
				if (DEBUG_ENABLED) {
					System.out.println("Connecton opened. (" + new Date() + ")");
				}
				Thread thread = new Thread(myServer); //create dedicated thread to manage the client connection
				thread.start();
			}
			
		} catch (IOException e) {
			System.out.println("Server Connection error : " + e.getMessage());
		}
	}
	
	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		String fileRequested = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream())); 	//read chars from the client via in-stream on the socket
			out = new PrintWriter(connect.getOutputStream()); 					//get character out-stream to client (for headers)
			dataOut = new BufferedOutputStream(connect.getOutputStream()); 		// get binary out-stream to client (for requested data)
			
			String input = in.readLine(); //get first line of the request from the client
			StringTokenizer parse = new StringTokenizer(input); //parse the request with a string tokenizer
			String method = parse.nextToken().toUpperCase(); //get the HTTP method of the client
			fileRequested = parse.nextToken().toLowerCase(); //get file requested
			
			// we support only GET and HEAD methods, we check
			if (!method.equals("GET")  &&  !method.equals("HEAD")) {
				if (DEBUG_ENABLED) {
					System.out.println("501 Not Implemented : " + method + " method.");
				}
				File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED); 	//return the not supported file to the client
				int fileLength = (int) file.length();
				byte[] fileData = readFileData(file, fileLength); 		//read content to return to client
					
				//send HTTP Headers with data to client
				out.println("HTTP/1.1 501 Not Implemented");
				out.println("Server: Java HTTP Server from SSaurel : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: text/html");
				out.println("Content-length: " + fileLength);
				out.println(); 	//blank line between headers and content
				out.flush();
				dataOut.write(fileData, 0, fileLength); //file
				dataOut.flush();
				
			} else {
				// GET or HEAD method
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}
				
				File file = new File(WEB_ROOT, fileRequested);
				int fileLength = (int) file.length();
				
				if (method.equals("GET")) { 							// GET method returns content
					byte[] fileData = readFileData(file, fileLength); 	// read content to return to client
					
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Server: Java HTTP Server : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: text/html");
					out.println("Content-length: " + fileLength);
					out.println(); 	//blank line between headers and content
					out.flush();
					dataOut.write(fileData, 0, fileLength); //file
					dataOut.flush();
					
					if (DEBUG_ENABLED) {
						System.out.println("File " + fileRequested + " returned");
					}
				} else if (method.equals("HEAD")) {						// HEAD method do not return content
					
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Server: Java HTTP Server : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: text/html");
					out.println("Content-length: " + fileLength);
					out.println(); 	//blank line between headers and content
					out.flush();
				}
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println("File not found exception : " + fnfe);
		} catch (IOException ioe) {
			System.out.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); //close socket connection
			} catch (Exception e) {
				System.out.println("Error closing stream : " + e.getMessage());
			} 
			
			if (DEBUG_ENABLED) {
				System.out.println("Connection closed.\n");
			}
		}
	}

	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		return fileData;
	}
}