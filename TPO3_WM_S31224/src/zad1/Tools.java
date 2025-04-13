/**
 *
 *  @author Wierciński Mateusz S31224
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

public class Tools {
    static Options createOptionsFromYaml(String fileName) throws Exception {
            String yaml = Files.readAllLines(Paths.get(fileName)).stream().collect(Collectors.joining(System.lineSeparator()));
            Map<String, Object> map = new Yaml().loadAs(yaml, Map.class);
        return new Options((String) map.get("host"), (int)map.get("port"), (boolean)map.get("concurMode"), (boolean) map.get("showSendRes"), (Map<String, List<String>>)map.get("clientsMap"));
    }
}
