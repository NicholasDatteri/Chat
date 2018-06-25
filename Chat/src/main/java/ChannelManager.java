import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChannelManager {

    protected Map <String, ThreadChannel> channels;
    protected ChatProtocol protocol= null;

    public ChannelManager()
    {
        channels= new HashMap<String, ThreadChannel>();
    }

    public void setChatProcol(ChatProtocol protocol){
        this.protocol=protocol;
    }

    public void initialite(Socket socket)
    {
        ThreadChannel channel = new ThreadChannel(this, socket);
        channel.start();
        protocol.startMessage(channel);
    }
    public synchronized boolean addChannel(String name,ThreadChannel channel) {
        if(!channels.containsKey(name)) {
            channels.put(name, channel);
            return true;
        } else return false;
    }

    public synchronized void processMessage(ThreadChannel ch, String str) {
        protocol.parserMessage(ch, str);
    }

    public synchronized void removeChannel(String name) {
        if(channels.containsKey(name)) {
            ThreadChannel ch = channels.remove(name);
            ch.interrupt();
        }
    }

    public synchronized Set<String> getAllName() {
        return channels.keySet();
    }

    public synchronized ThreadChannel getChannel(String name) {

        if(channels.containsKey(name))
            return channels.get(name);
        else return null;
    }
}

