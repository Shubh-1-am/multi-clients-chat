import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    
    private String userName;
    public ClientThread(Socket socket) {

        try {
            this.socket = socket;
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userName = reader.readLine();
            broadcastMessage("Server: " +userName + " has joined the chat.");
        } catch (Exception e) {
            closeAll(socket,writer,reader);

        }
    }

    private void broadcastMessage(String s) {
        for (ClientThread client : Server.clients) {
            try {
                assert client.userName != null;
                if(!client.userName.equals(userName)) {
                    assert s != null;
                    client.writer.write(s);
                    client.writer.newLine();
                    client.writer.flush();
                }

            } catch (IOException e) {
                closeAll(socket,writer,reader);
            }
        }
    }
    @Override
    public void run() {
        String message;
        while (socket.isConnected()){
            try {
                message = reader.readLine();
                broadcastMessage(message);
            } catch (IOException e) {
                closeAll(socket,writer,reader);
                break;
            }
        }
    }

    public void removeClient() {
        Server.clients.remove(this);
        broadcastMessage("Server: " + userName + " has left the chat.");
    }

    public  void closeAll(Socket socket,BufferedWriter writer, BufferedReader reader){
        removeClient();
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
}
