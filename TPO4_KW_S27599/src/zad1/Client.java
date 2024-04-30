/**
 * @author Kaczor Wiktor S27599
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {

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
                Thread.sleep(200);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String send(String req) {
        StringBuilder response = new StringBuilder();
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
        inbuf.clear();
        try {
            int n;
            do {
                n = socketChannel.read(inbuf);
            } while (n == 0);
            if (n > 0) {

                inbuf.flip();
                CharBuffer decoded = Charset.forName("ISO-8859-2").decode(inbuf);
                while (decoded.hasRemaining()) {
                    char ch = decoded.get();
                    if (Character.toString(ch).equals("\r") || Character.toString(ch).equals("\n"))
                        break;
                    response.append(ch);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return response.toString();
    }


}
