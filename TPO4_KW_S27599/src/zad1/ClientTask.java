/**
 * @author Kaczor Wiktor S27599
 */

package zad1;


import java.awt.event.WindowListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {
    private Client c;
    private List<String> reqList;
    private boolean showRes;
//    private String clog;

    private ClientTask(Client c, List<String> reqList, boolean showRes) {
        super(() -> {
            String inLog="";
            try {
            c.connect();
            c.send("login " + c.getId());
            for (String req : reqList) {
                String res = c.send(req);
                if (showRes) System.out.println(res);
            }
            inLog = c.send("bye and log transfer");
        }catch (Exception e){
            e.printStackTrace();
        }
        return inLog;
        });
        this.c = c;
        this.reqList = reqList;
        this.showRes = showRes;
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        return new ClientTask(c, reqList, showRes);
    }



//    public String get() throws InterruptedException, ExecutionException {
//        return clog;
//    }
}
