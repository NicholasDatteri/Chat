
import java.io.IOException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultChatProtocol extends ChatProtocol {

    private Map<String, Command> commands;
    {
        commands = new HashMap<String, Command>();
        commands.put("list", new List());
        commands.put("msg",  new Msg());
        commands.put("user", new User());
        commands.put("quit", new Quit());
    }
    public DefaultChatProtocol (ChannelManager manager)
    {
        super(manager);
    }
    public void parserMessage(ThreadChannel ch, String str)
    {
        if(str.charAt(0)!='/')
        {
            ch.send("Sintassi errata");
        }else
        {
            Pattern pattern= Pattern.compile("\\/([^\\s]+)\\s(.*)");
            Matcher match= pattern.matcher(str);
            match.find();
            String command=match.group(1);
            if(commands.containsKey(command))
            {
                Command cmd= commands.get(command);
                cmd.execute(ch, match);
            }else{
                ch.send("Comando sconosciuto");
            }
        }
    }
    public void startMessage(ThreadChannel ch){
        ch.send("Benvenuto nella chat, i comandi sono /time /list /quit /user /msg \n");
    }
    protected void broadcast(String name, String msg, boolean mysend)
    {
        Set<String> set= manager.getAllName();
        for(String str: set){
            if(!str.equalsIgnoreCase(name) || mysend)
            {
                ThreadChannel channel=manager.getChannel(str);
                 channel.send(msg);
            }
        }
    }
    private class User implements Command{
        public void execute(ThreadChannel channel, Matcher match) {
            if(!channel.isLogin())
            {
                String name= match.group(2);
                if(name.length()==0)
                {
                    channel.send("Sintassi del comando errata");
                }else if(manager.addChannel(name.toLowerCase(), channel)){
                    System.out.println("Account collegato " + name);
                    broadcast(name, "L'Utente "+name+" si e' appena collegato alla chat", true);
                    channel.setName(name);
                    channel.setLogin(true);
                }else{
                    channel.send("Nome già in uso da un altro utente");
                }
            }else{  channel.send("Sei già loggato");
            }
        }
    }
    private class Msg implements Command{
        public void execute(ThreadChannel channel, Matcher match) {
            System.out.println("Lancio MSG");
            if(channel.isLogin()) {
                broadcast(channel.getName().toLowerCase(),"#"+channel.getName()+" "+match.group(2),true);
            }else {
                channel.send("Non sei loggato /user per loggare");
            }
        }
    }
    private class Quit implements Command
    {
        public void execute(ThreadChannel channel, Matcher match) {
            channel.send("By by ");
            broadcast(channel.getName().toLowerCase(),
                    "L'utente "+channel.getName()+ " si e' disconnesso!!", false);
            manager.removeChannel(channel.getName().toLowerCase());
            channel.closeChannel();
        }
    }
    private class List implements Command {
        public void execute(ThreadChannel channel, Matcher match) {
            Set <String> names = manager.getAllName();
            if (names.isEmpty()) {
                channel.send("Nessun utente collegato");
            } else {
                channel.send("Lista degli utenti connessi");
                for(String name:names)
                    channel.send(name);
            }
        }
    }
    public interface Command {
        public void execute(ThreadChannel channel, Matcher match);
    }
    public static void main(String [] argv) throws IOException {
        ChannelManager manager = new ChannelManager();
        DefaultChatProtocol protocol = new DefaultChatProtocol(manager);
        ChatServer server = new ChatServer(10000, manager);
        server.start();
    }



}
