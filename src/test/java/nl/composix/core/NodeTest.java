/**
 * MIT License
 *
 * Copyright (c) 2023 ComPosiX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */

package nl.composix.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NodeTest {
    private final int size = 2;
    private final String[] source = new String[]{"title", "author", "year", "price"};
    private final Object[][] targets = new Object[size][source.length];

    private Tree<Integer,Tree<String,Object>> array;
    private Tree<String,Tree<String,Object>> tree;

    @BeforeEach
    public void setup() {
        array = Node.createNodes(targets, source);
        tree = Node.createTree(targets, source);
     }

    @Test
    public void testSizing() {
        assertEquals(size, array.capacity());
        assertEquals(size, tree.capacity());
        assertEquals(size, array.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testIsLeafAt() {
        Builder.forEachIndex(2, i -> {
            assertFalse(array.isLeafAt(i));
            assertFalse(tree.isLeafAt(i));

            Builder.forEachIndex(4, j -> {
                assertTrue(array.isLeafAt(i, j));
                assertTrue(tree.isLeafAt(i, j));
            });
        });
    }

    @Test
    public void testIndexOf() {
        final String[] keys = new String[] {"A", "B"};

        Builder.forEachIndex(2, i -> {
            assertEquals(i,tree.indexOf(keys[i]));
        });
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            tree.indexOf("C");
        });
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            array.indexOf(2);
        });

        Builder.forEachIndex(2, i -> {
            assertEquals(i, array.indexOf(i));
            assertEquals(i, tree.indexOf(keys[i]));
            assertNotSame(tree.get(keys[i]), array.get(i));
            assertTrue(tree.get(keys[i]).isEmpty());
            assertTrue(array.get(i).isEmpty());
        });
        
        final Tree<String,Object> arrayA = array.get(0);
        final Tree<String,Object> arrayB = array.get(1);
        final Tree<String,Object> treeA = tree.get("A");
        final Tree<String,Object> treeB = tree.get("B");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            arrayA.indexOf("test");
        });
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            arrayB.indexOf("test");
        });
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            treeA.indexOf("test");
        });
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            treeB.indexOf("test");
        });

        assertEquals(0, arrayA.indexOf("title"));
        assertEquals(1, arrayA.indexOf("author"));
        assertEquals(2, arrayA.indexOf("year"));
        assertEquals(3, arrayA.indexOf("price"));

        assertEquals(0, arrayB.indexOf("title"));
        assertEquals(1, arrayB.indexOf("author"));
        assertEquals(2, arrayB.indexOf("year"));
        assertEquals(3, arrayB.indexOf("price"));

        assertEquals(0, treeA.indexOf("title"));
        assertEquals(1, treeA.indexOf("author"));
        assertEquals(2, treeA.indexOf("year"));
        assertEquals(3, treeA.indexOf("price"));

        assertEquals(0, treeB.indexOf("title"));
        assertEquals(1, treeB.indexOf("author"));
        assertEquals(2, treeB.indexOf("year"));
        assertEquals(3, treeB.indexOf("price"));
    }

    @Test
    public void testEntries() {
        Stream.of(tree, array).forEach(node -> {
            Builder.forEachIndex(node.entries(), (i, entry) -> {
                if (entry.getKey() instanceof Integer) {
                    assertEquals(i, entry.getKey());
                } else {
                    assertNull(entry.getKey());
                }

                assertEquals(0, entry.getValue().entries().collect(Collectors.toList()).size());
            });
        });

        targets[0][0] = targets[1][0] = "Harry Potter";
        targets[0][1] = targets[1][1] = new Node<>(new String[]{"J. K. Rowling"});
        targets[0][2] = targets[1][2] = 2005;
        targets[0][3] = targets[1][3] = 29.99;

        Stream.of(tree, array).forEach(node -> {
            Builder.forEachIndex(node.entries(), (i, entry) -> {
                if (entry.getKey() instanceof Integer) {
                    assertEquals(i, entry.getKey());
                } else {
                    assertNull(entry.getKey());
                }

                final Iterator<Entry<String,Object>> entries = entry.getValue().entries().collect(Collectors.toList()).iterator();
                Entry<String,Object> entryX;

                entryX = entries.next();
                assertEquals("title", entryX.getKey());
                assertInstanceOf(String.class, entryX.getValue());
                entryX = entries.next();
                assertEquals("author", entryX.getKey());
                assertInstanceOf(Tree.class, entryX.getValue());
                entryX = entries.next();
                assertEquals("year", entryX.getKey());
                assertInstanceOf(Integer.class, entryX.getValue());
                entryX = entries.next();
                assertEquals("price", entryX.getKey());
                assertInstanceOf(Double.class, entryX.getValue());
            });
        });
    }

    @Test
    public void testAssign() {
        Object[] valueA = new Object[] {
            "Harry Potter",
            new String[]{"J. K. Rowling"},
            2005,
            29.99
        };
        Object[] valueB = new Object[] {
            "XQuery Kick Start",
            new String[] {
                "James McGovern",
                "Per Bothner",
                "Kurt Cagle",
                "James Linn",
                "Vaidyanathan Nagarajan"
            },
            2003,
            49.99
        };

        array.assign(valueA, valueB);

        assertSame(valueA[0], targets[0][0]);
        assertSame(valueA[1], targets[0][1]);
        assertSame(valueA[2], targets[0][2]);
        assertSame(valueA[3], targets[0][3]);

        assertSame(valueB[0], targets[1][0]);
        assertSame(valueB[1], targets[1][1]);
        assertSame(valueB[2], targets[1][2]);
        assertSame(valueB[3], targets[1][3]);
    }
}
