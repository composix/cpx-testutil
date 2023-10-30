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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NodeTest {
    private final String[] source = new String[]{"title", "author", "year", "price"};
    private final Object[][] target = new Object[2][];

    private Tree<Integer,Tree<String,Object>> tree;

    @BeforeEach
    public void setup() {
        @SuppressWarnings("unchecked")
        final Tree<String,Object>[] nodes = new Node[target.length];
        for (int i = 0; i < target.length; ++i) {
            nodes[i] = new Node<>(source,target[i]);
            target[i][1] = new Node<>(new Integer[0], new String[5]);
        }
        tree = new Node<>(new Integer[0], nodes);
     }

    @Test
    public void testIndexOf() {
        assertEquals(0, tree.indexOf(0));
        assertEquals(1, tree.indexOf(1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            tree.indexOf(2);
        });

        assertFalse(tree.isLeafAt(0));
        assertFalse(tree.isLeafAt(1));

        final Tree<String,Object> treeA = tree.get(0);
        final Tree<String,Object> treeB = tree.get(1);

        assertEquals(0, treeA.indexOf("title"));
        assertEquals(1, treeA.indexOf("author"));
        assertEquals(2, treeA.indexOf("year"));
        assertEquals(3, treeA.indexOf("price"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            treeA.indexOf("test");
        });

        assertEquals(0, treeB.indexOf("title"));
        assertEquals(1, treeB.indexOf("author"));
        assertEquals(2, treeB.indexOf("year"));
        assertEquals(3, treeB.indexOf("price"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            treeB.indexOf("test");
        });
    }

    @Test
    public void testEntries() {
        final Iterator<Integer> iterator = List.of(0,1, 0, 1).iterator();
        tree.entries().forEach((entry) -> {
            assertEquals(iterator.next(), entry.getKey());

            final Iterator<Entry<String,Object>> entries = entry.getValue().entries().collect(Collectors.toList()).iterator();
            Entry<String,Object> entryX = entries.next();

            assertEquals("author", entryX.getKey());
            assertInstanceOf(Tree.class, entryX.getValue());
        });

        target[0][0] = target[1][0] = "Harry Potter";
        target[0][2] = target[1][2] = 2005;
        target[0][3] = target[1][3] = 29.99;

        tree.entries().forEach((entry) -> {
            assertEquals(iterator.next(), entry.getKey());

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
    }

    @Test
    public void testAssign() {
        final Object[] value = new Object[] {"Harry Potter", new String[]{"J.K. Rowling"}, 2005, 29.99};
        tree.get(0).assign(value);
        assertArrayEquals(value, (Object[]) target[0]);
    }
}
