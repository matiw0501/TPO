/**
 * @author Wierciński Mateusz S31224
 */

package zad1;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<String> {
    ChatClient chatClient = null;
    public ChatClientTask(Callable<String> code) {
        super(code);
    }

    public static ChatClientTask create(ChatClient c, List<String> msgs, int wait) {
        Callable<String> code = () -> {
            c.login();
            if (wait > 0) {
                Thread.sleep(wait);
            }
            try {
                for (String msg : msgs) {
                    c.send(msg);
                    if (wait > 0) {
                        Thread.sleep(wait);
                    }
                }
            } catch (Exception e) {
                c.logout();
                return "interrupted";
            }
            c.logout();
            return "success";
        };
        ChatClientTask task = new ChatClientTask(code);
        task.chatClient = c;
        return task;
    }

    public ChatClient getClient(){
        return chatClient;
    }



}
