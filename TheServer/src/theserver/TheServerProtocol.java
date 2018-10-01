package theserver;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class TheServerProtocol extends JFrame {
    
    private final ArrayList<String> questions;        
    private ArrayList<String> answers;
    private JTextField serverInput;
    private JTextArea responses;
    private TheServer theServer;
    private Student student;

// run this to launch server
    public static void main(String[] args){
        TheServerProtocol theServerProtocol = new TheServerProtocol();
        theServerProtocol.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theServerProtocol.startServer();
    }
    
    private TheServerProtocol(){
        super("The Server");
        createGUI();
        
        this.questions = new ArrayList<>();
        this.questions.add("Please enter your student number");
        this.questions.add("Please enter your first name and surname");
        this.questions.add("Please enter your faculty, course and degree");
        this.questions.add("Retrieving personal code");
        this.questions.add("Please enter your student number, first and last names," + 
                " faculty, course, degree and personal code all together");
        this.questions.add("Communication was successful. Thank you. Press enter to view your results.");
        
        this.answers = new ArrayList<>();
        this.student = new Student();
        
        this.theServer = new TheServer("1234");
    }
    
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
        add(this.serverInput, BorderLayout.NORTH);
        this.responses = new JTextArea();
        this.responses.setEditable(false);
        add(new JScrollPane(this.responses));
        setSize(900, 600);
        setVisible(true);
    }
    
    //    initial method run to establish connections and set up requirements before communication starts
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
        sendMessageToClient("Would you like to save student information? (Yes or No)");
        do{    
            try{
                message = (String) this.theServer.getInput().readObject();
                showMessageOnServerGUI("\n" + message);
                String[] temp = message.split(": ");
                message = temp[1];
                switch(message){
                    case "Yes":
                    case "yes":
                        enableTyping(false);
                        setVisible(false);
                        int i = 1;
                        sendMessageToClient("Hello Student");
                        sendMessageToClient(this.questions.get(0));
                        do {
                            try{
                                message = (String) this.theServer.getInput().readObject();
                                showMessageOnServerGUI("\n" + message);
                                if(i < 6){
                                    i = validation(i, message);
                                } else {
                                    break;
                                }
                            } catch(ClassNotFoundException classNotFoundException){
                                showMessageOnServerGUI("ERROR occured could not read object");
                            }
                        } while(!message.equals("CLIENT: END"));

                        sendMessageToClient("Responses you sent to the server");
    //                    for(int a = 0; a < this.answers.size(); a++){
    //                        sendMessageToClient(this.answers.get(a));
    //                    }

                        sendMessageToClient("Student Number: " + this.student.getNumber());
                        sendMessageToClient("Student Name: " + this.student.getFullName());
                        sendMessageToClient("Student Facult/Course/Degree: " + this.student.getFacultyCourseDegree());
                        sendMessageToClient("Student Personal Code: " + this.student.getPersonalCode());

                        break;
                    case "No":
                    case "no":
                        enableTyping(true);
                        do {
                            try{
                                message = (String) this.theServer.getInput().readObject();
                                showMessageOnServerGUI("\n" + message);
                            } catch(ClassNotFoundException classNotFoundException){
                                showMessageOnServerGUI("ERROR occured could not read object");
                            }
                        } while(!message.equals("CLIENT: END"));
                        break;
                    default:
                        sendMessageToClient("Undefined input");
                        whileConnectionIsStable();
                        break;
                }
            } catch(ClassNotFoundException classNotFoundException){
                showMessageOnServerGUI("ERROR occured could not read object");
            }
        } while(!message.equals("CLIENT: END"));
        whileConnectionIsStable();
    }
    
//    used to validate the input that is sent by the client
    private int validation(int i, String message) {
//        System.out.print(i);
        String temp[] = message.split(": ");
        message = temp[1];
        boolean allGood = false;
        if(i == 1){ //check for student number
            if(message.matches("^[0-9]{6}$")){
                this.answers.add(message);
                this.student.setNumber(message);
                sendMessageToClient(this.questions.get(i));
                allGood = true;
            } else {
                sendMessageToClient("Please enter a valid Student Number. (eg 090381)");
            }
        } else if(i == 2){ //check for student name
            if(message.matches("^[a-zA-Z]* [a-zA-Z]*$")){
                this.answers.add(message);
                this.student.setFullName(message);
                sendMessageToClient(this.questions.get(i));
                allGood = true;
            } else {
                sendMessageToClient("Please enter a valid Student Name. (eg Alexander Muriithi)");
            }
        } else if(i == 3){ //check for student faculty
            if(message.matches("^[a-zA-Z]* [a-zA-Z]* [a-zA-Z]*$")){
                this.answers.add(message);
                this.student.setFacultyCourseDegree(message);
                sendMessageToClient(this.questions.get(i));
                allGood = true;
            } else {
                sendMessageToClient("Please enter a valid Faculty, Course and Degree. (eg FIT ICS Undergraduate)");
            }
        } else if(i == 4){ //check for student personal code
            this.answers.add(message);
            this.student.setPersonalCode(message);
            sendMessageToClient(this.questions.get(i));
            allGood = true;
        } else if(i == 5){ //check for all the information
            int b = 0;
            for(int a = 0; a < this.answers.size(); a++){
                if(message.contains(this.answers.get(a))){
                    b++;
                }
            }
            if(b == this.answers.size() - 1){
                this.answers.add(message);
                sendMessageToClient(this.questions.get(i));
                allGood = true;
            } else {
                sendMessageToClient("The answers just provided do not match those provided before. Please make sure they match.");
            }
        }
        System.out.println(String.valueOf(allGood));
        return (allGood) ? ++i : i;
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

    private void showMessageOnServerGUI(final String string) {
        SwingUtilities.invokeLater(new Runnable() { 
            @Override
            public void run() {
                responses.append(string);
            }
        });
    }

    private void enableTyping(final boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                serverInput.setEditable(b);
            }
        });
    }
}
