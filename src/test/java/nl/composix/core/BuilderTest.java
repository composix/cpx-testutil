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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
        assertArrayEquals(empty, targets[0]);
        assertArrayEquals(empty, targets[1]);

        targets[0][0] = "Harry Potter";
        targets[0][1] = new Node<>(new String[]{"J. K. Rowling"});
        targets[0][2] = 2005;
        targets[0][3] = 29.99;
        
        Builder.forEachIndex(tree.entries(), (index,entry) -> {
            assertEquals(index, entry.getKey());
            if (index == 0) {
                assertEquals(4,entry.getValue().size());

                assertSame(targets[0][0], entry.getValue().get("title"));
                assertSame(targets[0][1], entry.getValue().get("author"));
                assertSame(targets[0][2], entry.getValue().get("year"));
                assertSame(targets[0][3], entry.getValue().get("price"));
            } else {
                assertTrue(entry.getValue().isEmpty());
            }
        });
    }
}
