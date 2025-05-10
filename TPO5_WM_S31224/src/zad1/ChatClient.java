/**
 *
 *  @author Wierciński Mateusz S31224
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    String host;
    int port;
    String id;
    SocketChannel socket;
    public ChatClient(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void login(){
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(false);
            socket.connect(new InetSocketAddress(host, port));
            while (!socket.finishConnect()) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            send(id + " logged in");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void logout(){
        try {
            send(id + " logged out");
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private Charset charset = StandardCharsets.UTF_8;

    public void send(String req){
        ByteBuffer buffer = charset.encode(req);
        try{
            socket.write(buffer);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //System.out.println("sending " + req);
    }

    public String getChatView(){
        return "aGetChatView";
    }



}
