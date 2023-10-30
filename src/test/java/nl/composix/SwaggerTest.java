package nl.composix;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.composix.core.Tree;

public class SwaggerTest {
    @Test
    void testSwagger() throws URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        Swagger swagger = new Swagger();
        TreeNode tree = mapper.valueToTree(swagger.getSchemaExample(new URI("bookstore.yaml#/components/schemas/Books/example"), Object[].class));
        
    }
}
