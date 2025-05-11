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
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    String host;
    int port;
    String id;
    StringBuffer clientLog;
    SocketChannel socket;
    public ChatClient(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
        clientLog = new StringBuffer();
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
            listener.start();
            send(id + " logged in");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void logout(){
        try {
            send(id + " logged out" + socket.getRemoteAddress().toString() ); //jak sie sypnie to sprawdz
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            listener.interrupt();
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
    }

    Thread listener = new Thread(new Runnable() {
        public void run() {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            while(socket.isOpen()){

                if (Thread.interrupted()) {
                    break;
                }


                try {
                    readBuffer.clear();
                    int n = socket.read(readBuffer);
                    if (n == -1) {
                        System.out.println("end of stream");
                        break;
                    }
                    if(n>0){
                        readBuffer.flip();
                        CharBuffer charBuffer = charset.decode(readBuffer);
                        clientLog.append(charBuffer+"\n");
                    }
                    //System.out.println("ebebe");
                    Thread.sleep(0);
                }
                catch (Exception e) {
                    //System.out.println("error");
                    //e.printStackTrace();
                    break;
                    //interuptuje aby przestal sluchac
                }
            }
        }
    });

    public String getChatView(){
        return "=== " + id +" chat view\n"+clientLog;
    }



}
