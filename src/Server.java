import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    /***
     * Listens for clients to connect
     */
    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public void startServer() throws IOException, InterruptedException {
        try {
            System.out.println("Server listening!..");


            while(!serverSocket.isClosed()){
                Socket connectionSocket = serverSocket.accept();
                //TODO get the requests here from the client and add the request as a parameter to the constructor
                // for the clientHandler class
                System.out.println("A new client has joined");

                System.out.println("From Server class: " + "ok");
                ClientHandler clientHandler = new ClientHandler(connectionSocket);
                System.out.println("After creating the client hander");
                Thread thread = new Thread(clientHandler);
                System.out.println("After creating the thread");

                thread.start();
                System.out.println("After starting the thread");

                //connectionSocket.close();

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void closerServerSocket(){
        try{
            if (serverSocket != null){
                serverSocket.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[]args){
        try {
            System.out.println("The server is listening on port 3000....");
            ServerSocket serverSocket = new ServerSocket(5000);
            Server server = new Server(serverSocket);
            server.startServer();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}

