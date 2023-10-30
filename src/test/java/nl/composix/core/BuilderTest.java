package nl.composix.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuilderTest {
    private final int size = 3;

    private final Object[][] targets = new Object[size][];

    private Tree<Integer,Tree<String,Object>> tree;

    @BeforeEach
    public void setup() {
        tree = Builder
            .targets(targets)
            .keys("title", "author", "year", "price")
        .build();
    }

    @Test
    public void testAlreadyInitialized() {
        assertThrows(IllegalArgumentException.class,
            () -> Builder.targets(targets));
    }

    @Test
    public void testBuilderResult() {
        assertEquals(size, tree.size(), "array should have declared size");

        assertEquals(size,Builder.forEachIndex(tree.entries(), (index,entry) -> {
            assertEquals(index, entry.getKey());
            assertTrue(tree.get(index).isEmpty(), "elements should be initialized empty");
        }));
        
        Builder.forEachIndex(size, (i) -> {
            assertAll("schemas should be included",
                () -> assertEquals(0, tree.get(i).indexOf("title")),
                () -> assertEquals(1, tree.get(i).indexOf("author")),
                () -> assertEquals(2, tree.get(i).indexOf("year")),
                () -> assertEquals(3, tree.get(i).indexOf("price"))
            );
        });
    }

    @Test
    public void testBuilderTarget() {
        final Object[] empty = new Object[4];
        assertArrayEquals(empty, (Object[]) targets[0]);
        assertArrayEquals(empty, (Object[]) targets[1]);

        final Object[] source = new Object[] {"Harry Potter", null, 2005, 29.99};
        final Object[] target = targets[0];
        target[0] = source[0];
        System.arraycopy(source, 0, target, 0, 4);
        
        Builder.forEachIndex(tree.entries(), (index,entry) -> {
            assertEquals(index, entry.getKey());
            if (index == 0) {
                assertEquals(4,entry.getValue().size());

                assertEquals("Harry Potter", entry.getValue().get("title"));
                assertArrayEquals(new String[]{"J.K. Rowling"}, (String[]) entry.getValue().get("author"));
                assertEquals(2005, entry.getValue().get("year"));
                assertEquals(29.99, entry.getValue().get("price"));
            } else {
                assertTrue(entry.getValue().isEmpty());
            }
        });
    }
}
