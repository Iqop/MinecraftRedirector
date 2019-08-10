import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RedirectionServer {
    public static void main(String args[]){
        int localRedirectionPort;
        int clientConnectionPort;
        try{
            localRedirectionPort = new Integer(args[0]);
            clientConnectionPort = new Integer(args[1]);
        }catch (Exception e){
            System.out.println("The format is <Local Redirection Process connection port> <Minecraft client connection port>");
            return;
        }
        try {
            System.out.println("Welcome to minecraft redirector (RemoteNode)");
            System.out.println("Waiting to bind to the local redirection process in port "+localRedirectionPort);
            //Configuration Connection
            ServerSocket confServerSocket = new ServerSocket(localRedirectionPort);
            Socket confClientConnected = confServerSocket.accept();

            InetAddress clientAddress = confClientConnected.getInetAddress();
            int clientPort = confClientConnected.getPort();

            System.out.println("Binded to the configuration client with IP:  "+clientAddress.getHostAddress()+":"+clientPort);


            confClientConnected.setKeepAlive(true);



            System.out.println("Accepting a connection on "+clientConnectionPort);
            //Entry connection
            ServerSocket serverSocket = new ServerSocket(clientConnectionPort);
            Socket clientConnected = serverSocket.accept();
            System.out.println("Connection received from "+clientConnected.getInetAddress().getHostAddress());


            while(clientConnected.isConnected() && confClientConnected.isConnected()){
                if (clientConnected.isClosed()){
                    clientConnected = serverSocket.accept();
                }
                int available = clientConnected.getInputStream().available();
                if (available>0) {
                    byte[] b = new byte[available];
                    clientConnected.getInputStream().read(b);
                    confClientConnected.getOutputStream().write(b);
                    confClientConnected.getOutputStream().flush();
                }

                int available2 = confClientConnected.getInputStream().available();
                if (available2>0) {
                    byte[] b = new byte[available2];
                    confClientConnected.getInputStream().read(b);
                    clientConnected.getOutputStream().write(b);
                    clientConnected.getOutputStream().flush();
                }
            }
            System.out.println("Client closed connection, exiting...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
