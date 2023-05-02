import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static Scanner kb = new Scanner(System.in);
    private static String request = "";
    public static void main(String[]args) throws IOException {
        /***
        //0.Prompt the user for an entry:
        System.out.println("Please enter in a a valid HTTP Request [GET,PUT, UPDATE,DELETE");
        System.out.println("\nUsage:myClient <URL> <> <Server Port#> <HTTP REQUEST> <FILENAME>\nmyClient ");
        request= kb.nextLine();***/
        //1. parse command line args and error handle any incorrect formats
        if (args.length == 0){
            System.out.println("No arguments were passed");
            System.out.println("Usage:myClient <URL> <> <Server Port#> <HTTP REQUEST> <FILENAME>\nmyClient ");
        }
        // use this loop to check that all the args are correct later on --
        //TODO: create error handling method to ensure proper use
        //using tilde as a delimiter
        String url = "";
        String portNum = "~";
        String request = "~";
        String fileName = "~";

        for (int i = 0; i < args.length; i++){
            //System.out.println("Arguments " + (i) + ": " + args[i]);
            if (i == 0){url += args[i];}
            else if (i ==1) {portNum += args[i];}
            else if (i == 2){request += args[i];}
            else{fileName += args[i];}
        }
        Socket clientSocket;
        //2.Connect to server:


        if (!portNum.isBlank() && isNumericInt(portNum.substring(1))) {


            clientSocket = new Socket("localhost", Integer.parseInt(portNum.substring(1)));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            //System.out.println("what does the request look like: " + request);
            String input;
            String response = "";
            if (request.equals("~PUT")){
                // user should provide an absolute path when PUT is used
                String content = "";
                try{
                    //System.out.println("Inside PUT block");
                    content =  readFile(fileName.substring(1));//new String (Files.readString(Paths.get(fileName.substring(1))));
                    //System.out.println("content: " + content);
                    outToServer.write(url+ portNum+ request+ fileName + "~" + content);
                    outToServer.newLine();
                    outToServer.flush();
                    //System.out.println("wrote to the server");
                    //System.out.println("Lets see reply from if");
                    while ((input = inFromServer.readLine())!= null){
                        response += input;
                        System.out.println("Server resposne: " + response);
                        break;
                    }

                } catch (IOException e){
                    e.printStackTrace();
                }

            }
            else {
                outToServer.write(url + portNum + request + fileName);
                System.out.println("after writing the message!");
                outToServer.newLine();
                outToServer.flush();



                while ((input = inFromServer.readLine())!= null){
                    response += input;
                    System.out.println("Server response: " + response);
                    break;
                }
            }
            System.out.println("End of program!");
        }
        else{
            System.out.println("invalid command-line arguments!");
            System.exit(0);
        }
        //set up communication to ClientHandler?



        //outToServer.close();


        // see if we got a reply!


        //inFromServer.close();
        //clientSocket.close();

    }
    public static boolean isNumericInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static String readFile(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String content ="";
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                content +=line;
            }
            return content;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return"";
    }

}

