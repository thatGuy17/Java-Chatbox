package theclient;

import java.io.*;
import java.net.*;

public class TheClient {
    
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String serverIP;
    private Socket connection;
    private String portNumber;
    
//    constructor defines ipadress and port number to send messages to
    public TheClient(String host, String port){
        this.serverIP = host;
        this.portNumber = port;
    }

//    attempting to connect to server with details provided in constructor
    public void connectToServer() throws IOException {
        System.out.println("Attempting to connect to server. . .");
        this.connection = new Socket(InetAddress.getByName(this.serverIP), Integer.parseInt(this.portNumber));
        System.out.println("\nYou are connected to " + this.connection.getInetAddress().getHostName());
    }

//    enabling client and server to read and write data to each other
    public void setupIOStreams() throws IOException {
        this.output = new ObjectOutputStream(this.connection.getOutputStream());
        this.output.flush();
        
        this.input = new ObjectInputStream(this.connection.getInputStream());
        System.out.println("\nStreams are now good to go");
    }
    
    //    closes down connections and stream readers
    public void killConnectionToServer() {
        System.out.println("\nClosing connections and streams");
        try{
            this.output.close();
            this.input.close();
            this.connection.close();
        }catch(IOException exception){
            exception.printStackTrace();
        }
    }
    
    public ObjectOutputStream getOutput() {
        return this.output;
    }

    public ObjectInputStream getInput() {
        return this.input;
    }

    public String getServerIP() {
        return this.serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public Socket getConnection() {
        return this.connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public String getPortNumber() {
        return this.portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }
}
