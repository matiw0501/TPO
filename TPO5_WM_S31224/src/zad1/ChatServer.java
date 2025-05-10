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
import java.util.Iterator;
import java.util.Set;
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
    private ExecutorService executorService = Executors.newCachedThreadPool();
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
            if(executorService != null){
                executorService.shutdownNow();
            }
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
        String request = charBuffer.toString();
        String response;


    }
    public String getServerLog(){
        return "aGetServer";
    }
}
