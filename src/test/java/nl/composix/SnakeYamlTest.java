package nl.composix;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class SnakeYamlTest {
    private static final ClassLoader loader = SnakeYamlTest.class.getClassLoader();

    @Test
    void testSnakeYaml() {
        Yaml yaml = new Yaml();
        Map<String,?> swagger = yaml.load(loader.getResourceAsStream("bookstore.yaml"));
    }
}
