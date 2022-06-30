package managers.commands.messageTypes;

import managers.commands.messageTypes.MessageType;

import java.net.InetAddress;

public class UsersListSender extends MessageType {

    public InetAddress inetAddress;
    public int port;

    public UsersListSender(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

}
