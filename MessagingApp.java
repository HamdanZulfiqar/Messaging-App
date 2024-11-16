import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Contact{
    private String contactID;
    private String name;

    public Contact(String contactID, String name){
        this.contactID = contactID;
        this.name = name;
    }
    public String getContactID(){
        return contactID;
    }
    public String getName(){
        return name;
    }
    public void displayContactDetails(){
        System.out.println("Contact ID: " + contactID + ", Name: " + name);
    }
}
class Message{
    private String senderID;
    private String receiverID;
    private String text;
    private LocalDateTime timestamp;
    private boolean isRead;

    public Message(String senderID, String receiverID, String text){
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.text = text;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }
    public String getSenderID(){
        return senderID;
    }
    public String getReceiverID(){
        return receiverID;
    }
    public String getText(){
        return text;
    }
    public LocalDateTime getTimestamp(){
        return timestamp;
    }
    public boolean isRead(){
        return isRead;
    }
    public void markAsRead() {
        this.isRead = true;
    }
    public void markAsUnread() {
        this.isRead = false;
    }
    public void displayMessageDetails() {
        System.out.println("Sender: " + senderID + ", Receiver: " + receiverID + ", Text: " + text + ", Timestamp: " + timestamp + "Status:" + (isRead ? "Read" : "Unread"));         
    }
}
class MessageManager{
    private List<Message> messages;

    public MessageManager(){
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public void displayMessagesForContact(String contactID){
        boolean found = false;
        for (Message message : messages) {
            if (message.getReceiverID().equals(contactID)){
                message.displayMessageDetails();
                found = true;
            }
        }
        if (!found){
            System.out.println("No messages for contact ID: " + contactID);
        }
    }

    public void deleteMessagesForContact(String contactID){
        messages.removeIf(message -> message.getReceiverID().equals(contactID));
        System.out.println("All messages for contact ID " + contactID + " have been deleted.");
    }

    public void markMessagesAsRead(String contactID){
        for (Message message : messages) {
            if (message.getReceiverID().equals(contactID)){
                message.markAsRead();
            }
        }
        System.out.println("All messages for contact ID " + contactID + " marked as read.");
    }
    public void searchMessagesBySender(String senderID){
        boolean found = false;
        for (Message message : messages) {
            if (message.getSenderID().equals(senderID)){
                message.displayMessageDetails();
                found = true;
            }
        }
        if (!found){
            System.out.println("No messages found from sender ID: " + senderID);
        }
    }
}
class Sender{
    private static final String RECEIVER_IP = "192.168.100.223"; // Replace with Receiver's IP
    private static final int PORT = 12345; // Port number.

    public static void main(String[] args){
        try (Socket socket = new Socket(RECEIVER_IP, PORT)){
            System.out.println("Connected to receiver at " + RECEIVER_IP + ":" + PORT);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Scanner sc = new Scanner(System.in);
            String message;

            while (true){
                System.out.print("Enter message (type 'exit' to quit): ");
                message = sc.nextLine();
                out.println(message); // Send message to receiver

                if (message.equalsIgnoreCase("exit")){
                    System.out.println("Exiting chat...");
                    break;
                }
                // Receive acknowledgment
                String ack = in.readLine();
                System.out.println("Receiver: " + ack);
            }

        } 
        catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}
class Receiver{
    public void receiveMessages(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println();
            System.out.println("Receiver is running on port " + port + "...");
            System.out.println("Waiting for messages...");

            Socket socket = serverSocket.accept();
            System.out.println("Connection established!");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Receive messages
            String message;
            while ((message = in.readLine()) != null){
                System.out.println("Received message: " + message);
                if (message.equalsIgnoreCase("exit")){
                    System.out.println("Sender disconnected.");
                    break;
                }
            }

        } 
        catch (IOException e){
            System.err.println("Error in Receiver: " + e.getMessage());
        }
    }
}
public class MessagingApp{
    public static void main(String[] args){
        MessageManager messageManager = new MessageManager();
        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("1", "Alice"));
        contacts.add(new Contact("2", "Bob"));

        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.println("\n*=============================Welcome to Messaging Application=================================*");
            System.out.println();
            System.out.println("1) View Contacts.");
            System.out.println("2) Send Message.");
            System.out.println("3) Receive Messages.");
            System.out.println("4) View Messages.");
            System.out.println("5) Delete Messages.");
            System.out.println("6) Mark Messages as Read.");
            System.out.println("7) Search Messages by Sender.");
            System.out.println("8) Exit.");
            System.out.println();
            System.out.print("Select your choice from the above: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1 :{
                    System.out.println("Contacts:");
                    for (Contact contact : contacts){
                        contact.displayContactDetails();
                    }
                }
                case 2 :{
                    System.out.print("Enter receiver ID: ");
                    String receiverID = sc.nextLine();
                    System.out.print("Enter your message: ");
                    String text = sc.nextLine();
                    messageManager.addMessage(new Message("You", receiverID, text));
                }
                case 3 :{
                    Receiver receiver = new Receiver();
                    receiver.receiveMessages(12345); // Replace with your port
                }
                case 4 :{
                    System.out.print("Enter contact ID: ");
                    String contactID = sc.nextLine();
                    messageManager.displayMessagesForContact(contactID);
                }
                case 5 :{
                    System.out.print("Enter contact ID: ");
                    String contactID = sc.nextLine();
                    messageManager.deleteMessagesForContact(contactID);
                }
                case 6 :{
                    System.out.print("Enter contact ID: ");
                    String contactID = sc.nextLine();
                    messageManager.markMessagesAsRead(contactID);
                }
                case 7 :{
                    System.out.print("Enter sender ID: ");
                    String senderID = sc.nextLine();
                    messageManager.searchMessagesBySender(senderID);
                }
                case 8 :{
                    System.out.println("Exiting application...");
                    return;
                }
                default :{
                    System.out.println("Invalid choice. Please choose a valid option.");
                }
            }
        }
    }
}