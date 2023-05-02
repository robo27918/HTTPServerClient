

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.common.primitives.Bytes;

import static com.google.common.primitives.Bytes.indexOf;


//TODO: 1)Find a way to get the request from the client into a variable in this class
        //2)
public class ClientHandler implements Runnable{
    //holds all the clients that are being served by the server
    // belongs to the entire class, not just one particular instance
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader buffReader;
    private BufferedWriter buffWriter;
    private String clientName;
    private String fileRespone="X";
    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            //add this  clientHandler to arraylist
            clientHandlers.add(this);

        }
        catch(Exception e){
            closeAll (socket, buffReader, buffWriter);

        }
    }
    public void removeClientHandler(){
        clientHandlers.remove(this);

    }
    public void closeAll(Socket socket, BufferedReader buffReader, BufferedWriter buffWriter){
        removeClientHandler();
        try{
            if(buffReader!=null){
                buffReader.close();
            }
            if (buffWriter!=null){
                buffWriter.close();
            }
            if(socket!=null){
                socket.close();
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public void sendResponse(String resp) throws IOException {
        buffWriter.write(resp);
        buffWriter.newLine();
        buffWriter.flush();
    }
    public void processRequest(String msg) throws IOException {
        /**
         * this method splits the msg into usable tokens and handles the request appropriately
         *
         *  myclient www.cnn.com 80 GET index.html
         *  ---- for GET Request
         * 0: host
         * 1: port number
         * 2: REQUEST TYPE GET or PUT
         * 3: filename
         *
         * myclient host port_number PUT filename
         * ---- for PUT request
         *
         */

         //TODO: get the correct request type and send out
         String [] processMe = msg.split("~");
         //System.out.println("checking length of processMe in ClientHandler: " + processMe.length);
         //for(int i = 0; i < processMe.length; i++){System.out.println(processMe[i]);}
         //System.out.println("finished showing contents of array");

         String RequestType = processMe[2];
         String host = processMe[0];
         String fileName = processMe[3];
         //String content = processMe[4];
         if (RequestType.equals("GET")){
             if (host.equals("pc1.cs.cpp.edu")){
                 //file is local file on server
                 getLocalFile(fileName);
                 System.out.println("Got the local file");
             }
             else{
                 getFromHost(host, fileName);
                 System.out.println("got the file from the host");
             }
         }
         else{
             String content = processMe[4];
             //System.out.println("content from processRequest method: " + content);
             putToServer (fileName, content);
             //System.out.println("the else statment");
         }


    }
    public void getFromHost(String host, String fileName) throws IOException {
        // Establish a TCP connection to the web server
        System.out.println("Printing from getFromHost method");
        Socket socket = new Socket(host, 80);

        // Send an HTTP GET request for the file
        String request = "GET " + host + "/" + fileName +" HTTP/1.1\r\n" +
                "Host: " +host + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(request.getBytes());
        outputStream.flush();

        // Read the HTTP response from the server using a BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String resp="";
        String line;
        boolean headersComplete = false;
        //ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        while ((line = reader.readLine()) != null) {
            // Headers are terminated by a blank line
            if (line.isEmpty()) {
                headersComplete = true;
            }
            if (headersComplete) {
                //responseStream.write(line.getBytes());
                resp+=line;
            }
        }
        //byte[] responseBytes = responseStream.toByteArray();


        socket.close();
        this.fileRespone = resp;


    }

    public void putToServer(String fileName, String content) throws IOException {
        //
        //
        String [] correctFileName = fileName.split("/");
        //System.out.println("Filename in putToServer: " + correctFileName[correctFileName.length-1]);
        //System.out.println("Contents is: " + content);

        File directory = new File("src/files");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, correctFileName[correctFileName.length-1]);
        try {
            if (file.createNewFile()) {
                this.fileRespone = "OK: File successfully uploaded!";
                System.out.println("File created successfully.");
            } else {
                System.out.println("File already exists.");
                this.fileRespone = "File already exists";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write(content);
        bw.flush();
        bw.close();
        System.out.println("From putToServer: finished writing file...");
    }
    public void getLocalFile(String fileName) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/files/" + fileName));
            StringBuilder builder = new StringBuilder();
            String fileLine;

            while ((fileLine = reader.readLine()) != null) {
                builder.append(fileLine);
            }
            reader.close();
            System.out.println("printing builder");
            this.fileRespone = builder.toString();
        }
        catch(FileNotFoundException f){
            this.fileRespone ="Bad Request: no such file";
        }
    }
    @Override
    public void run() {

        System.out.println("A new client has entered the chat ... printing from run method in Clienthandler...");

        try{

            //buffReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            // Read lines from the client until the client disconnects

            String line;
            while ((line = this.buffReader.readLine()) != null) {
                // Process the client's message here
                System.out.println("Received message from client: " + line);
                // make a method that parses the string
                processRequest(line);
                System.out.println("About to send response...");
                sendResponse(this.fileRespone);
                break;
            }



            // Close the socket when the client disconnects
            //buffReader.close();
           // socket.close();

            }
            catch(Exception e){
                //closeAll(socket,buffReader,buffWriter);
                e.printStackTrace();
            }



    }
}