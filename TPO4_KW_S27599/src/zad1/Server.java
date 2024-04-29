/**
 * @author Kaczor Wiktor S27599
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {

    private String host;
    private int port;
    private ServerSocketChannel serverSocketChannel = null;
    private Selector selector = null;
    Thread thread;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    private StringBuilder request = new StringBuilder();

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
            selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread = new Thread(this);
        thread.start();
    }

    public void stopServer() {
        thread.interrupt();
    }

    public String getServerLog() {
        return "";
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key =iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        continue;
                    }
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        serviceRequest(socketChannel);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void serviceRequest(SocketChannel socketChannel) {
        if (!socketChannel.isOpen())
            return;
        request.setLength(0);
        buffer.clear();
        try {
//            readLoop:
//            while (true) {
                int n = socketChannel.read(buffer);
                if (n>0){
                    buffer.flip();
                    CharBuffer decoded = Charset.forName("ISO-8859-2").decode(buffer);
                    while (decoded.hasRemaining()){
                        char ch = decoded.get();
                        request.append(ch);
                        if(Character.toString(ch).equals("\r")||Character.toString(ch).equals("\n")) {
                            System.out.println(request);
                            request.setLength(0);
                        }
//                            break readLoop;
                    }
//                } else if (n == -1) {
////                    break;
//                }
            }
            System.out.println(request);
            String response = processRequest(request.toString());

            // Odpowiedź na żądanie
            ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
            socketChannel.write(responseBuffer);
            socketChannel.close();




        }catch (IOException e){
            e.printStackTrace();
            try {
                socketChannel.close();
                socketChannel.socket().close();
            }catch (IOException ex){}
        }
    }
    private String processRequest(String request) {
        return "Server received: " + request;
    }
}
