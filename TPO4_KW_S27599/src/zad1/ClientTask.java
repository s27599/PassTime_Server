/**
 * @author Kaczor Wiktor S27599
 */

package zad1;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask implements Runnable {
    private Client c;
    private List<String> reqList;
    private boolean showRes;
    private String clog;

    public ClientTask(Client c, List<String> reqList, boolean showRes) {
        this.c = c;
        this.reqList = reqList;
        this.showRes = showRes;
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        return new ClientTask(c, reqList, showRes);
    }

    @Override
    public void run() {
        c.connect();
        c.send("login " + c.getId());
        for (String req : reqList) {
            String res = c.send(req);
            if (showRes) System.out.println(res);
        }
        clog = c.send("bye and log transfer");
    }

    public String get()throws InterruptedException, ExecutionException {
        return clog;
    }
}
