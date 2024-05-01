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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class Server implements Runnable {

    private String host;
    private int port;
    private ServerSocketChannel serverSocketChannel = null;
    private Selector selector = null;
    Thread thread;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private StringBuilder request = new StringBuilder();

    private String user;
    private static Map<String, List<String>> clientLogs = new HashMap<>();
    private static List<String> serverLog = new ArrayList<>();

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
        StringBuilder sb = new StringBuilder();
        for (String str : serverLog) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
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
            int n = socketChannel.read(buffer);
            if (n > 0) {
                buffer.flip();
                CharBuffer decoded = StandardCharsets.UTF_8.decode(buffer);
                while (decoded.hasRemaining()) {
                    char ch = decoded.get();
                    request.append(ch);
                    if (Character.toString(ch).equals("\r") || Character.toString(ch).equals("\n")) {
                        String response = processRequest(request.toString());
                        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                        socketChannel.write(responseBuffer);
                        request.setLength(0);
                    }

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
            try {
                socketChannel.close();
                socketChannel.socket().close();
            } catch (IOException ex) {
            }
        }
    }

    private String processRequest(String request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        request = request.replace("\n", "");
        if (request.startsWith("login")) {
            user = request.split(" ")[1];
            clientLogs.put(user, new ArrayList<>());
            clientLogs.get(user).add("=== "+user+" log start ===");
            clientLogs.get(user).add("logged in");
            serverLog.add(user + " logged in at "+ LocalDateTime.now().format(formatter));
            return "logged in";
        } else if (request.equals("bye")) {
            clientLogs.get(user).add("logged out");
            serverLog.add(user + " logged out at "+ LocalDateTime.now().format(formatter));
            return "logged out";
        } else if (request.equals("bye and log transfer")) {
            clientLogs.get(user).add("logged out");
            serverLog.add(user + " logged out at "+ LocalDateTime.now().format(formatter));
            StringBuilder sb = new StringBuilder();
            for (String str: clientLogs.get(user)){
                sb.append(str).append("\n");
            }
            return sb.toString();
        } else if (request.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}.*")) {
            String[] requestSplit = request.split(" ");
            String result = Time.passed(requestSplit[0], requestSplit[1]);
            clientLogs.get(user).add("Request: "+request);
            clientLogs.get(user).add("Result:\n"+result);
            serverLog.add(user+" request at "+LocalDateTime.now().format(formatter)+" \""+request+"\"");
            return result;
        }
        //garbage
        return request;

    }
}
