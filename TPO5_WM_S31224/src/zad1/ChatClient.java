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
import java.util.concurrent.CountDownLatch;

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
            listener.start();
            while (!socket.finishConnect()) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            send( "login " + id);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void logout(){
        try {
            send("logout" + id +  " " + socket.getRemoteAddress().toString() ); //jak sie sypnie to sprawdz
            Thread.sleep(100);
            listener.interrupt();
            try {
                listenerDone.await(); //sprawdzaj czy jest lock jak nie to ogien z ... jak tak to jakis nie kosztowny sposob na waita (nie udało sie, zapytaj)
            }
            catch (InterruptedException e) {
               listener.interrupt();
            }
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Charset charset = StandardCharsets.UTF_8;

    public void send(String req){
        ByteBuffer buffer = charset.encode(req+"\n");
        try{
            socket.write(buffer);}
        catch (Exception e){
            e.printStackTrace();
        }
    }

    CountDownLatch listenerDone = new CountDownLatch(1);
    volatile  boolean interrupted = false;
    Thread listener = new Thread(new Runnable() {
        public void run() {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            while(socket.isOpen()){
                //lock
                if (Thread.interrupted()) {
                    interrupted = true;
                }
                try {
                    readBuffer.clear();
                    int n = socket.read(readBuffer);
                    if (n == -1) {
                        System.out.println("end of stream");
                        //unlock
                        break;
                    }
                    if(n>0){
                        readBuffer.flip();
                        CharBuffer charBuffer = charset.decode(readBuffer);
                        clientLog.append(charBuffer+"\n");
                        //unlock
                    }

                    if (interrupted) {
                        break;
                    }
                    //System.out.println("ebebe");
                    //Thread.sleep(0);
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            listenerDone.countDown();
        }
    });

    public String getChatView(){
        return "=== " + id +" chat view\n"+clientLog;
    }



}
