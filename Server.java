import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    static List<ClientThread> clients = new ArrayList<ClientThread>();
    private ServerSocket serverSocket;

    public Server(int port)  {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {

            try {

                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    System.out.println("A new client is connected :" );
                    ClientThread clientThread = new ClientThread(socket);
                    clients.add(clientThread);
                    new Thread(clientThread).start();
                }
            } catch (IOException e) {
                closeServerSocket();
            }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}
