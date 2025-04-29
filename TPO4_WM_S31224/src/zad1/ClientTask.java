package zad1;

import java.util.List;
import java.util.concurrent.*;

public class ClientTask extends FutureTask<String> {



    public static ClientTask create(Client c, List<String> reqs, boolean showRes) {
       Callable<String> code = () -> {
           String log  = "log is empty";
           c.connect();
           c.send("login " + c.getId());
           try{
               for(String req : reqs){
                   if (Thread.interrupted()) return "interrupted";
                   String res = c.send(req);
                   if (showRes) System.out.println(res);
               }
               log = c.send("bye and log transfer");
           }    catch (Exception e){
               return "interrupted";
           }
           return log;
        };
       return new ClientTask(code);
    }
    public ClientTask(Callable<String> code) {
        super(code);
    }



}
