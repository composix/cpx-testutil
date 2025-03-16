/**
 * MIT License
 *
 * Copyright (c) 2025 ComPosiX
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
 */

package io.github.composix.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

class TestCaseTest extends TestCase {
  static byte[] bytes = all((byte) 0);
  static short[] shorts = all((short) 0);
  static int[] ints = all(0);
  static long[] longs = all(0L);
  static float[] floats = all(0F);
  static double[] doubles = all(0D);
  static boolean[] booleans = all(false);
  static char[] chars = all('0');
  static String[] strings = all("");

  @Test 
  void testAll_primitives() {
    assertSame(bytes, all(bytes));
    assertSame(shorts, all(shorts));
    assertSame(ints, all(ints));
    assertSame(longs, all(longs));
    assertSame(floats, all(floats));
    assertSame(doubles, all(doubles));
    assertSame(booleans, all(booleans));
    assertSame(chars, all(chars));
    assertSame(strings, all(strings));
  }

  @Test
  void testAll_objects() {
    assertAllSame(new byte[][] {bytes, bytes}, all(bytes, bytes));
    assertAllSame(new short[][] {shorts, shorts}, all(shorts, shorts));
    assertAllSame(new int[][] {ints, ints}, all(ints, ints));
    assertAllSame(new long[][] {longs, longs}, all(longs, longs));
    assertAllSame(new float[][] {floats, floats}, all(floats, floats));
    assertAllSame(new double[][] {doubles, doubles}, all(doubles, doubles));
    assertAllSame(new boolean[][] {booleans, booleans}, all(booleans, booleans));
    assertAllSame(new char[][] {chars, chars}, all(chars, chars));
    assertAllSame(new String[][] {strings, strings}, all(strings, strings));

    assertSame(strings, all(strings));
  }

  @Test
  void testAssertAllEquals() {
    assertAllEquals(all(1L, 2L, 3L), all(1L, 2L, 3L));
    assertEquals(
      "expected: <java.lang.String[]> but was: <java.lang.Object[]>",
      assertThrows(AssertionFailedError.class, () -> {
        assertAllEquals(
          all("a", "b", "c" ),
          new Object[] { "a", "b", "c" }
        );
      }).getMessage()
    );
  }
}
