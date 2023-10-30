package nl.composix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.stream.Stream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;

import nl.composix.bookstore.Book;
import nl.composix.core.SubIterator;
import nl.composix.core.Tree;

public class Swagger {
    private static final ClassLoader loader = Swagger.class.getClassLoader();
    private static final Yaml yaml = new Yaml();

    
    public <T> List<Map<String, Object>> getSchemaExample(URI uri, Class<T> clazz) {
        String yamlString = select(uri);
        Map<String,List<Map<String,Object>>> result = yaml.load(yamlString);
        return result.get("example");
    }

    private String select(final URI uri) {
        if (uri.isAbsolute()) {
            throw new UnsupportedOperationException("not yet implemented");
        }
        Stream<String> stream = yamlLines(new BufferedReader(
            new InputStreamReader(
                loader.getResourceAsStream(uri.getPath())
            )
        ).lines());
        
        final String[] fragment = uri.getFragment().split("/");
        if (!fragment[0].isEmpty()) {
            throw new IllegalStateException("URI fragment must start with /");
        }
        for (int i = 1; i < fragment.length; ++i) {
            stream = select(stream, i - 1, fragment[i]);
        }        
        return current(stream, fragment.length - 2);
    }

    private Stream<String> select(final Stream<String> stream, final int depth, final String property) {
        return stream.dropWhile(line -> !line.startsWith(depth + "/" + property));
    }

    private String current(final Stream<String> stream, final int depth) {
        final StringWriter writer = new StringWriter();
        stream.takeWhile(line -> {
            String[] pair = line.split("/", 2);
            return Integer.parseInt(pair[0]) >= depth;
        })
            .forEach(line -> {
                String[] pair = line.split("/", 3);
                char[] indent = new char[(Integer.parseInt(pair[0]) - depth) << 1];
                for (int i = 0; i < indent.length; ++i) {
                    indent[i] = ' ';
                }
                if (pair.length > 2) {
                    if (Integer.parseInt(pair[1]) < 0) {
                        indent[indent.length - 2] = '-';
                        indent[indent.length - 1] = ' ';
                    }
                    pair[1] = "";
                }
                pair[0] = new String(indent);
                writer.write(String.join("", pair) + "\n");
            });
        return writer.toString();
    }

    private Stream<String> yamlLines(Stream<String> lines) {
        final Stack<Integer> tabs = new Stack<>();
        final Stack<Integer> indices = new Stack<>();
        tabs.push(0);
        return lines.map(line -> {
            int indent = -1, tab = tabs.peek();
            while (line.charAt(++indent) == ' ');
            if (line.charAt(indent) == '-') {
                while (line.charAt(++indent) == ' ');
                if (indent == -tab) {
                    int index = indices.pop();
                    indices.push(++index);
                    return (tabs.size() - 1) + "/" + -index + "/" + line.substring(indent); 
                }
                if (indent > Math.abs(tab)) {
                    tabs.push(-indent);
                    indices.push(1);
                    return (tabs.size() - 1) + "/-1/" + line.substring(indent);
                }
                throw new IllegalStateException("indentation error");
            }
            if (indent == -tab && indent > 0) {
                return (tabs.size() - 1) + "/" + indices.peek() + "/" + line.substring(indent);
            }
            while (indent < Math.abs(tabs.peek())) {
                if (tabs.pop() < 0) {
                    indices.pop();
                }
            }
            if (indent > Math.abs(tabs.peek())) {
                tabs.push(indent);
            }
            return (tabs.size() - 1) + "/" + line.substring(indent);
        });
    }

    private SubIterator<String> iterator(URI uri) {
        if (uri.isAbsolute()) {
            throw new UnsupportedOperationException("not yet implemented");
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(uri.getPath())));
        return new SubIterator<>(new Iterator<Entry<Integer,String>>() {
            private Entry<Integer,String> current = readLine();
            @Override
            public boolean hasNext() {
                if (current == null) {
                    try {
                        current = next();
                    } catch(NoSuchElementException e) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public Entry<Integer, String> next() {
                Entry<Integer,String> result = current;
                current = null;
                if (result == null) {
                    return readLine();
                }
                return result;
            }

            private Entry<Integer,String> readLine() {
                String line;
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    return null;
                }
                if (line == null) {
                    return null;
                }
                int i = 0;
                while (line.charAt(i++) == ' ');
                return new SimpleEntry<>(i,line.substring(i));
            }
        });
    }
}
