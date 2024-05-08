/**
 * @author Kaczor Wiktor S27599
 */

package zad1;

import java.util.List;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {

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

    }
    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        return new ClientTask(c, reqList, showRes);
    }
}
