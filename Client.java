import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String userName;

    public Client(Socket socket, String userName){
        try {
            this.socket = socket;
            this.userName = userName;
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeAll(socket,writer,reader);

        }
    }

    public void sendMessage(){
        try {
            writer.write(userName);
            writer.newLine();
            writer.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String message = scanner.nextLine();
                writer.write(userName +":"+message);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            closeAll(socket,writer,reader);
        }
    }

    public void listen(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageReceived;
                while (socket.isConnected()){
                    try {
                        messageReceived = reader.readLine();
                        System.out.println(messageReceived);
                    } catch (IOException e) {
                        closeAll(socket,writer,reader);
                    }
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedWriter writer, BufferedReader reader){
        try {
            if(writer != null) {
                writer.close();
            }
            if(reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String userName = scanner.nextLine();
        try {
            Socket socket = new Socket("localhost",8080);
            Client client = new Client(socket,userName);
            client.listen();
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
