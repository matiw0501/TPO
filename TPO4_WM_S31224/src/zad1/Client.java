/**
 *
 *  @author
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getId() {
        return id;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    private String id;
    private SocketChannel socketChannel;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));
            while (!socketChannel.finishConnect()) {
                // Czekamy aż połączenie się zakończy
            }
        } catch (IOException e) {
            throw new RuntimeException("Connection error", e);
        }
    }

    public String send(String req) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap((req + "\n").getBytes(StandardCharsets.UTF_8));
            socketChannel.write(buffer);

            ByteBuffer responseBuffer = ByteBuffer.allocate(8192);
            StringBuilder response = new StringBuilder();
            int bytesRead;
            while ((bytesRead = socketChannel.read(responseBuffer)) <= 0) {
                // Czekamy aż przyjdzie odpowiedź
            }
            responseBuffer.flip();
            while (responseBuffer.hasRemaining()) {
                response.append((char) responseBuffer.get());
            }
            return response.toString().trim();
        } catch (IOException e) {
            throw new RuntimeException("Send/Receive error", e);
        }
    }
}