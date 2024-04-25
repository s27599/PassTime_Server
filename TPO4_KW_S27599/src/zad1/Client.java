/**
 * @author Kaczor Wiktor S27599
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client implements Runnable {

    private String host;
    private int port;
    private String id;
    private SocketChannel socketChannel;
    private ByteBuffer inbuf;


    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
        this.inbuf = ByteBuffer.allocateDirect(1024);
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
            while (!socketChannel.finishConnect()) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String send(String req) {
        try {
            ByteBuffer buffer
                    = ByteBuffer.allocate(1024);
            buffer.put(req.getBytes());
            buffer.put((byte) '\n');
            buffer.flip();
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "1";
    }

    @Override
    public void run() {

    }
}
