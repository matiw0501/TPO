package zad1;

import java.util.List;
import java.util.concurrent.*;

public class ClientTask implements Runnable {

    private final Client client;
    private final List<String> requests;
    private final boolean showResponses;
    private final CompletableFuture<String> result = new CompletableFuture<>();

    private ClientTask(Client client, List<String> requests, boolean showResponses) {
        this.client = client;
        this.requests = requests;
        this.showResponses = showResponses;
    }

    public static ClientTask create(Client client, List<String> requests, boolean showResponses) {
        return new ClientTask(client, requests, showResponses);
    }

    @Override
    public void run() {
        try {
            client.connect();
            client.send("login " + client.getId());
            for (String req : requests) {
                String res = client.send(req);
                if (showResponses) {
                    System.out.println(res);
                }
            }
            String log = client.send("bye and log transfer");
            result.complete(log);
        } catch (Exception e) {
            result.completeExceptionally(e);
        }
    }

    public String get() throws InterruptedException, ExecutionException {
        return result.get();
    }
}
