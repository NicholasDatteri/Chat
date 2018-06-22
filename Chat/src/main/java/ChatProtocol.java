import java.util.regex.Matcher;

public abstract class ChatProtocol {
    protected ChannelManager manager;
    public ChatProtocol(ChannelManager manasger){
        this.manager=manasger;
        manager.setChatProcol(this);
    }
    public abstract void startMessage(ThreadChannel ch);
    public abstract void parserMessage(ThreadChannel channel, String msg);


}

