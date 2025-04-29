/**
 *
 *  @author
 *
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
import java.time.LocalTime;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable {
    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        SocketChannel socketChannel = null;
        while (true) {
            try {
                if(Thread.interrupted()) break;
                if(!selector.isOpen()) break;
                selector.select();
                if(Thread.interrupted()) break;
                Set selectedKeys = selector.selectedKeys();

                Iterator iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                    }
                    if (key.isReadable()) {
                        socketChannel = (SocketChannel) key.channel();
                        serviceRequest(socketChannel);
                    }
                }
            }
            catch (Exception e) {
                try{
                    if (socketChannel != null) {
                        socketChannel.close();
                        socketChannel.socket().close();
                    }
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    Map<SocketChannel, String> channelMap = new LinkedHashMap<>();
    StringBuffer serverLog = new StringBuffer();
    Map<SocketChannel, StringBuffer> respLog = new LinkedHashMap<>();
    Charset charset = Charset.forName("UTF-8");
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    private void serviceRequest(SocketChannel socketChannel)throws IOException {
        if (!socketChannel.isOpen()) return;
        byteBuffer.clear();
        int n = socketChannel.read(byteBuffer);
        if (n <= 0 ) return;
        byteBuffer.flip();
        CharBuffer charBuffer = charset.decode(byteBuffer);
        String request = charBuffer.toString();
        String response ;
        if (request.startsWith("login")){
            String[] split = request.split("\\s+");
            channelMap.put(socketChannel, split[1]);
            String info = split[1] + " logged in at " + LocalTime.now() + "\n";
            serverLog.append(info);
            respLog.put(socketChannel, new StringBuffer("=== " + channelMap.get(socketChannel) + " log start ===\nlogged in\n"));
            writeResponse("logged in ", socketChannel);
        }
        else if (request.startsWith("bye")){
            response = "logged out";
            if (request.contains("transfer")) {
                serverLog.append(channelMap.get(socketChannel) + " logged out at " + LocalTime.now() + "\n");
                respLog.get(socketChannel).append(response + "\n=== " + channelMap.get(socketChannel) + " log end ===\n");
                writeResponse(respLog.get(socketChannel).toString(), socketChannel);
            }
            else writeResponse(response, socketChannel);
            if(socketChannel.isOpen()){
                socketChannel.close();
                socketChannel.socket().close();
            }
            channelMap.remove(socketChannel);
            respLog.remove(socketChannel);
        }
        else {
            String info  = channelMap.get(socketChannel) + " request at " + LocalTime.now() + ": "+ '"' + request + '"' + "\n";
            serverLog.append(info);
            String [] dates = request.split("\\s+"); //do zmiany znak splita
            if (dates.length != 2) response = "*** Invalid request: " + request;
            else response = Time.passed(dates[0], dates[1]);
            respLog.get(socketChannel).append("Request: " + request + "\n");
            respLog.get(socketChannel).append("Result: \n" + response + "\n");
            writeResponse(response, socketChannel);
        }
    }


    ServerSocketChannel serverSocketChannel = null;
    private Selector selector = null;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    public Server(String host, int port) throws IOException {

            serverSocketChannel  = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


    }

    public void startServer() {
        new Thread(this).start();
    }

    public void stopServer() {
        try{
            if(executorService != null) executorService.shutdownNow();
            if(selector != null) selector.close();
            if(serverSocketChannel != null) {
                serverSocketChannel.close();
                serverSocketChannel.socket().close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    String getServerLog(){
        return serverLog.toString();
    }

    public void writeResponse(String response,SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = charset.encode(response);
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }

    }


}
