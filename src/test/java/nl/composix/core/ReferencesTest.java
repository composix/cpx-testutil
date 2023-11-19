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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReferencesTest {
    static final byte position = 0;

    References<String> refs;
    byte posA, posB, posC;

    @BeforeEach
    void setup() {
        refs = new References<>();
    }

    @Test 
    void testInvalidPosition() {
        assertThrows(IndexOutOfBoundsException.class, () -> refs.indexOf((byte) 1, "test"));
        assertThrows(IllegalStateException.class, () -> refs.register(8));
    }

    @Test
    void testIndexOfEmpty() {
        posA = refs.register(16);
        posB = refs.register(24);
        posC = refs.register(32);

        assertEquals(-1, refs.indexOf(posA, "testA"));
        assertEquals(-1, refs.indexOf(posB, "testB"));
        assertEquals(-1, refs.indexOf(posC, "testC"));

        assertEquals(-1, refs.indexOf(posA, null));
        assertThrows(NullPointerException.class, () -> refs.indexOf(posA, null, 0));

        assertThrows(IllegalStateException.class, () -> refs.register(8));
    }

    @Test
    void testIndexOfLimit() {
        assertEquals(0, refs.indexOf(position, "testA", 0));
        assertEquals(0, refs.indexOf(position, "testA"));
        assertEquals(0, refs.indexOf(position, "testA", 0));
        assertEquals(0, refs.indexOf(position, "testA", 1));
        assertEquals(0, refs.indexOf(position, "testA", Byte.MAX_VALUE - Byte.MIN_VALUE));
        assertEquals(0, refs.indexOf(position, "testA", 1024));

        assertEquals(1, refs.indexOf(position, "testB", 1));
        assertEquals(1, refs.indexOf(position, "testB"));
        assertEquals(1, refs.indexOf(position, "testB", 0));
        assertEquals(1, refs.indexOf(position, "testB", 2));

        assertEquals(255, refs.indexOf(position, "testC", Byte.MAX_VALUE - Byte.MIN_VALUE));
        assertEquals(255, refs.indexOf(position, "testC"));
    }

    @Test
    void testIndexOfWithOverflow() {
        posA = refs.register(32);
        assertEquals(0, refs.indexOf(posA, "testA", 0));
        assertEquals(Byte.MAX_VALUE, refs.indexOf(posA, "testB", Byte.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, refs.indexOf(posA, "testC", Integer.MAX_VALUE));


        assertEquals(-1, refs.indexOf(position, "testA"));
        assertEquals(-1, refs.indexOf(position, "testB"));
        assertEquals(-1, refs.indexOf(position, "testC"));

        assertEquals(0, refs.indexOf(posA, "testA"));
        assertEquals(Byte.MAX_VALUE, refs.indexOf(posA, "testB"));
        assertEquals(Integer.MAX_VALUE, refs.indexOf(posA, "testC"));
    }
}
