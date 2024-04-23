/**
 * @author Kaczor Wiktor S27599
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client implements Runnable {

    private String host;
    private int port;
    private String id;
    private SocketChannel socketChannel;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void connect() {
        try {
            if (!socketChannel.isOpen())
                socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String send(String req) {
        return "";
    }

    @Override
    public void run() {

    }
}
