/**
 *
 *  @author Wierciński Mateusz S31224
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatServer implements Runnable {

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        SocketChannel socketChannel = null;
        while (true) {
            try{
                if (Thread.interrupted()) {
                    break;
                }
                if (!selector.isOpen()) {
                    break;
                }
                selector.select();
                if(Thread.interrupted()) {
                    break;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator iterator = selectionKeys.iterator();
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
            catch(Exception e){
                try{
                    if (socketChannel != null) {
                        socketChannel.close();
                        socketChannel.socket().close();
                    }

                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
    }

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    public ChatServer(String host, int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(host,port));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void startServer(){
        System.out.println("Server started\n");
        new Thread(this).start();
    }

    public void stopServer(){
        try{
            if(selector != null){
                selector.close();
            }
            if(serverSocketChannel != null){
                serverSocketChannel.close();
                serverSocketChannel.socket().close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Server stopped");
    }

    Charset charset = Charset.forName("UTF-8");
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    StringBuffer serverLog = new StringBuffer();
    Map<SocketChannel, String> channelMap = new HashMap<>();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public void serviceRequest(SocketChannel socketChannel) throws IOException {
        if (!socketChannel.isOpen()) {
            return;
        }
        byteBuffer.clear();
        int readBytes = socketChannel.read(byteBuffer);
        if (readBytes == -1) {
            return;
        }
        byteBuffer.flip();
        CharBuffer charBuffer = charset.decode(byteBuffer);
        String [] arrayRequests = charBuffer.toString().split("\n");
        String message;
        for (String request : arrayRequests) {
            if (request.startsWith("login") && !channelMap.containsKey(socketChannel)) {
                String[] splits = request.split("\\s+");
                channelMap.put(socketChannel, splits[1]);
                message = splits[1] + " logged in ";
                forMap(channelMap, message);
            } else if (request.startsWith("logout") && request.contains(serverSocketChannel.getLocalAddress().toString())) { //pomysl nad tym jak zabezpieczyc wylogowywanie sie wiadomoscia
                message = channelMap.get(socketChannel) + " logged out";
                forMap(channelMap, message);
//                try {
//                    Thread.sleep(0);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                channelMap.remove(socketChannel);
            } else {
                message = request;
                forMap(channelMap, channelMap.get(socketChannel) + ": " + message);
            }
        }
    }
        private void forMap(Map<SocketChannel, String> map, String response) throws IOException {
            serverLog.append(LocalTime.now().format(timeFormatter) + " " + response + "\n");
            for(SocketChannel socketChannel : map.keySet()){
                writeResponse(response,socketChannel);
            }
        }

    public String getServerLog(){
        return serverLog.toString();
    }
    public void writeResponse(String response, SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = charset.encode(response);
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);
        }
    }


}
