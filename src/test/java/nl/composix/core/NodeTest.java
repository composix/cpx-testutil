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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NodeTest {

    private Tree<String,Tree<String,Object>> tree;

    @BeforeEach
    public void setup() {
        final String[] source = new String[]{"title", "author", "year", "price"};

        tree = tree(2,
                any(source,
                    "Harry Potter",
                    any(1,"Rowling"),
                    2005,
                    29.99
                ),
                any(source,
                    "Harry Potter",
                    any(1,"Rowling"),
                    2005,
                    29.99
                )
            );
     }

    @Test
    public void testIndexOf() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            tree.indexOf("test");
        });

        final Tree<String,Object> treeA = (Tree<String, Object>) tree.nodeAt(0);
        final Tree<String,Object> treeB = (Tree<String, Object>) tree.nodeAt(1);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            treeA.indexOf("test");
        });
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            treeB.indexOf("test");
        });

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
        tree.entries().forEach((entry) -> {
            assertNull(entry.getKey());
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

     private static Tree<String,Tree<String,Object>> tree(int size, Tree<?,?>... x) {
        return tree(new String[size],x);
     }

     private static Tree<String,Tree<String,Object>> tree(String[] keys, Tree<?,?>... x) {
        return new Node<>(keys,(Tree<String,Object>[]) x);
     }

     private static Tree<String,Object> any(String[] keys, Object... x) {
        return new Node<>(keys,x);
     }

     private static Tree<String,Object> any(int size, Object... x) {
        return any(new String[size], x);
     }
}
