/**
 *
 *  @author Kaczor Wiktor S27599
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public class Tools {
    public static Options createOptionsFromYaml(String fileName) throws Exception {
        FileInputStream io = new FileInputStream(fileName);
        Map<String, Object> load = new Yaml().load(io);
        io.close();
        return new Options((String) load.get("host"), (int) load.get("port"), (boolean) load.get("concurMode"),
                (boolean) load.get("showSendRes"), (Map<String, List<String>>) load.get("clientsMap"));

    }
}
