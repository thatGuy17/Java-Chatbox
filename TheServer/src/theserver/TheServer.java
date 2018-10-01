package theserver;

import java.io.*;
import java.net.*;

public class TheServer {
    
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;    
    private int portNumber;
    private String ipAddress;
    
//    constructor specifies ipaddress and port number to listen on
    public TheServer(String ipAddress, String portNumber){
        this.ipAddress = ipAddress;
        this.portNumber = Integer.parseInt(portNumber);
    }
    
    //    constructor specifies on port number to listen on (assumes ipaddress is localhost -> 127.0.0.1)
    public TheServer(String portNumber){
        this.portNumber = Integer.parseInt(portNumber);
    }
    
//listens for connection from client and then connects to them
    public void waitForConnection() throws IOException{
        this.server = new ServerSocket(this.portNumber);
        System.out.println("Waiting for client to connect. . .\n");
        this.connection = this.server.accept();
        System.out.println("Connected to " + this.connection.getInetAddress().getHostName());
    }
    
// allow client and server to send and recieve data
    public void setupIOStreams() throws IOException{
        this.output = new ObjectOutputStream(this.connection.getOutputStream());
        this.output.flush();
        
        this.input = new ObjectInputStream(this.connection.getInputStream());
        System.out.println("\nInput and Output Streams now ready.");
    }
    
//    closes all streams and sockets after chatting
    public void killConnectionToClient() {
        System.out.println("\nClosing connections. . . \n");
        try {
            this.output.close();
            this.input.close();
            this.connection.close();
        } catch(IOException iOException){
            iOException.printStackTrace();
        }
    }
    
    public ServerSocket getServer() {
        return this.server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
    
    public ObjectOutputStream getOutput() {
        return this.output;
    }

    public ObjectInputStream getInput() {
        return this.input;
    }

    public Socket getConnection() {
        return this.connection;
    }
}
