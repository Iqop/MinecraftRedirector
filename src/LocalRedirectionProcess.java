import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class LocalRedirectionProcess {
    public static void main(String args[]){
        String redirectionServerPublicIP;
        int redirectionServerConnectionPort;
        int minecraftServerPort;
        String minecraftServerBindedIP;
        try{
            redirectionServerPublicIP = args[0];
            redirectionServerConnectionPort = new Integer(args[1]);
            minecraftServerBindedIP = args[2];
            minecraftServerPort = new Integer(args[3]);
        }catch (Exception e){
            System.out.println("The format is <Redirection Server Public IP> <Redirection Server Connection Port> <Minecraft server binded IP> <Minecraft server port>");
            return;
        }
        try {
            System.out.println("Welcome to minecraft redirector (local server)");
            System.out.println("Connecting to outside redirection server");
            //Outside server connection
            Socket outsideServerSocket = new Socket(InetAddress.getByName(redirectionServerPublicIP),redirectionServerConnectionPort);
            System.out.println("Connected to outside redirection server");


            System.out.println("Connecting to the local Minecraft server binded to "+minecraftServerBindedIP);
            //Minecraft server
            Socket localMinecraftServerSocket = new Socket(InetAddress.getByName(minecraftServerBindedIP),minecraftServerPort);
            System.out.println("Connected to the local Minecraft server");

            System.out.println("Forwarding communications");
            while(outsideServerSocket.isConnected() && localMinecraftServerSocket.isConnected()){
                int available = outsideServerSocket.getInputStream().available();
                if (available>0) {
                    byte[] b = new byte[available];
                    outsideServerSocket.getInputStream().read(b);
                    localMinecraftServerSocket.getOutputStream().write(b);
                    localMinecraftServerSocket.getOutputStream().flush();
                }

                int available2 = localMinecraftServerSocket.getInputStream().available();
                if (available2>0) {
                    byte[] b = new byte[available2];
                    localMinecraftServerSocket.getInputStream().read(b);
                    outsideServerSocket.getOutputStream().write(b);
                    outsideServerSocket.getOutputStream().flush();
                }
            }
            System.out.println("Remote client closed connection, exiting...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
