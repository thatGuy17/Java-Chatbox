package theserver;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class TheServerProtocol extends JFrame {
          
    private ArrayList<String> answers;
    private JTextField serverInput;
    private JTextArea responses;
    private TheServer theServer;

// run this to launch server
    public static void main(String[] args){
        TheServerProtocol theServerProtocol = new TheServerProtocol();
        theServerProtocol.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theServerProtocol.startServer();
    }
    
    private TheServerProtocol(){
        super("The Server");
        createGUI();
        this.theServer = new TheServer("1234"); // creates an instance of TheServer and sets the port number (ip address assumed to be localhost)
    }
    
//     creates the GUI for the server
    private void createGUI(){
        this.serverInput = new JTextField();
        this.serverInput.setEditable(false);
        this.serverInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessageToClient(e.getActionCommand());
                serverInput.setText("");
            }
        });
        add(this.serverInput, BorderLayout.SOUTH);
        this.responses = new JTextArea();
        this.responses.setEditable(false);
        add(new JScrollPane(this.responses));
        setSize(900, 600);
        setVisible(true);
    }
    
    //    initial method run to establish connections and set up I/O streams before communication starts
    private void startServer(){
        try {
            while(true){
                try{
                    this.theServer.waitForConnection();
                    this.theServer.setupIOStreams();
                    whileConnectionIsStable();
                } catch(EOFException eOFException){
                    showMessageOnServerGUI("\nERROR: Server ended the connection.");
                } finally{
                    enableTyping(false);
                    this.theServer.killConnectionToClient();
                }
            }
        } catch(IOException iOException){
            
            
            iOException.printStackTrace();
        }
    }
    
//    method run to update screen with new messages while connection is running
    private void whileConnectionIsStable() throws IOException{
        String message = "You are now connected.";
        System.out.println(message);
        do{    
            try{
                message = (String) this.theServer.getInput().readObject();
                showMessageOnServerGUI("\n" + message);
            } catch(ClassNotFoundException classNotFoundException){
                showMessageOnServerGUI("ERROR occured could not read object");
            }
        } while(!message.equals("CLIENT: END"));
    }
    
//    sends message to the client
    private void sendMessageToClient(String message){
        try {
            this.theServer.getOutput().writeObject("SERVER: " + message);
            this.theServer.getOutput().flush();
            showMessageOnServerGUI("\nServer: " + message);
        } catch(IOException exception){
            this.responses.append("\nERROR: occured unable to send message");
        }
    }

//     displays message on the GUI
    private void showMessageOnServerGUI(final String string) {
        SwingUtilities.invokeLater(new Runnable() { 
            @Override
            public void run() {
                responses.append(string);
            }
        });
    }

//     enables and disables typing in the text view
    private void enableTyping(final boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                serverInput.setEditable(b);
            }
        });
    }
}
