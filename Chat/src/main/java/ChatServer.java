import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;
public class ChatServer extends Thread {

    private int port;
    private ServerSocket server;
    private ChannelManager manager;

    public ChatServer(int port, ChannelManager manager) throws IOException {
        System.out.println("Avvio server");
        this.port=port;
        this.manager=manager;
        server = new ServerSocket(port);
    }

    public ChatServer(int port) throws IOException{
        this(port, new ChannelManager());
    }
    public int getPort()
    {
        return this.port;
    }

    public void run()
    {
        try {
            while (true) {
                Socket socket = server.accept(); //Chiamata bloccante
                manager.initialite(socket);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }







}
