/**
 * @author
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client {
    public String id;
    private String host;
    private int port;
    private SocketChannel socket = null;
    private Charset charset = StandardCharsets.UTF_8;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(false);
            socket.connect(new InetSocketAddress(host, port));
            while (!socket.finishConnect()) {
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String send(String req) {
        String resp = null;
        ByteBuffer buffer = charset.encode(CharBuffer.wrap(req));
        try {
            socket.write(buffer);
            resp = getResponse(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }


    private String getResponse(SocketChannel socket) throws Exception {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        if (!socket.isOpen()) return "*** channel closed";
        readBuffer.clear();
        int n = socket.read(readBuffer);
        if (n < 0) {
            return "*** channel reach end of stream";
        }
        int i = 0;
        while (n == 0) {
            Thread.sleep(10);
            n = socket.read(readBuffer);
            i++;
            if (i > 200) return "*** no response";
        }
        readBuffer.flip();
        CharBuffer cbuf = charset.decode(readBuffer);
        return cbuf.toString();
    }

}