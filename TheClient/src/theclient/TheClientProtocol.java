package theclient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class TheClientProtocol extends JFrame {
    
    private final TheClient theClient;
    private JTextField userInput;
    private JTextArea response;

// Run this to launch client module
    public static void main(String[] args){
        TheClientProtocol theClientProtocol = new TheClientProtocol();
        theClientProtocol.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theClientProtocol.startClient();
    }

//     constructor used to create the GUI and assign a name to it
    private TheClientProtocol() {
        super("The Client");
        createGUI();
        this.theClient = new TheClient("127.0.0.1", "1234"); // creates an instance of TheClient class and sets the ip adress and port number
    }
    
    //   Creates the GUI for the client
    private void createGUI(){
        this.userInput = new JTextField();
        this.userInput.setEditable(false);
        this.userInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessageToServer(e.getActionCommand());
                userInput.setText("");
            }
        });
        
        add(this.userInput, BorderLayout.SOUTH);
        this.response = new JTextArea();
        add(new JScrollPane(this.response), BorderLayout.CENTER);
        setSize(900,600);
        setVisible(true);
   }
    
    //    used to attempt to create a connection 
    private void startClient(){
        try {
            this.theClient.connectToServer();
            this.theClient.setupIOStreams();
            whileConnectionIsStable();
        }catch(EOFException eOFException){
            showMessageOnClientGUI("\nERROR: Client Terminated the communication");
        }catch(IOException exception){
            exception.printStackTrace();
        }finally{
            this.theClient.killConnectionToServer();
            allowInput(false);
        }
    }
    
    //    communicates with server while connection is available
    private void whileConnectionIsStable() throws IOException {
        String message = "";
         allowInput(true);
        do {
            try{
                message = (String) this.theClient.getInput().readObject();
                showMessageOnClientGUI ("\n" + message);
            } catch(ClassNotFoundException classNotFoundException){
                showMessageOnClientGUI("\nERROR: object type unknown.");
            }
        } while(!message.equals("SERVER: END")); //used to terminate the conversation and exit the chat box
    }

//    displays message on screen
    private void showMessageOnClientGUI(String message) {
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    response.append(message);
                }
            }
        );
    }
    
//    enables client to type in field and send message to server
    private void allowInput(final boolean b){
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    userInput.setEditable(b);
                }
            }
        );
    }
    
    //    sends message to server
    private void sendMessageToServer(String message){
        try{
            this.theClient.getOutput().writeObject("CLIENT: " + message);
            this.theClient.getOutput().flush();
            showMessageOnClientGUI("\nCLIENT: " + message);
        } catch(IOException exception){
            this.response.append("\nERROR: Something went wrong sending message.");
        }
    }
}
