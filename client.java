import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            output.writeObject(username);

            new Thread(new ReceiveMessages(input)).start();

            while (true) {
                System.out.println("Type a message (or type 'send media' to send an image): ");
                String messageText = scanner.nextLine();

                if (messageText.equalsIgnoreCase("send media")) {
                    // Handle media sending
                    System.out.println("Enter path to image:");
                    String imagePath = scanner.nextLine();
                    File imageFile = new File(imagePath);
                    byte[] imageBytes = new byte[(int) imageFile.length()];
                    FileInputStream fis = new FileInputStream(imageFile);
                    fis.read(imageBytes);
                    fis.close();
                    
                    Message message = new Message(username, imageBytes);
                    output.writeObject(message);
                } else {
                    // Handle text message
                    String encryptedMessage = CryptoUtils.encrypt(messageText);
                    Message message = new Message(username, encryptedMessage);
                    output.writeObject(message);
                }

                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ReceiveMessages implements Runnable {
        private ObjectInputStream input;

        public ReceiveMessages(ObjectInputStream input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                Message message;
                while ((message = (Message) input.readObject()) != null) {
                    String sender = message.getSender();

                    if (message.getMessage() != null) {
                        String decryptedMessage = CryptoUtils.decrypt(message.getMessage());
                        System.out.println(sender + ": " + decryptedMessage);
                    } else if (message.getMedia() != null) {
                        System.out.println(sender + " sent an image");
                        FileOutputStream fos = new FileOutputStream("received_image.jpg");
                        fos.write(message.getMedia());
                        fos.close();
                        System.out.println("Image saved as received_image.jpg");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
