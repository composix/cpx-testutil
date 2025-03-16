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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import io.github.composix.math.Args;
import io.github.composix.math.ArgsOrdinal;
import io.github.composix.math.MutableOrder;
import io.github.composix.math.Ordinal;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class TestCase implements ArgsOrdinal {

  protected static final TestCase DEFAULT = new DefaultTestCase();
  private static final MutableOrder ORDER = OMEGA.order();

  protected static byte[] all(byte... values) {
    return values;
  }

  protected static short[] all(short... values) {
    return values;
  }

  protected static int[] all(int... values) {
    return values;
  }

  protected static long[] all(long... values) {
    return values;
  }

  protected static float[] all(float... values) {
    return values;
  }

  protected static double[] all(double... values) {
    return values;
  }

  protected static boolean[] all(boolean... values) {
    return values;
  }

  protected static char[] all(char... values) {
    return values;
  }

  protected static <T> T[] all(T... values) {
    return values;
  }

  protected static void assertAllEquals(Object expected, Object actual) {
    assertSame(expected.getClass(), actual.getClass());
    switch (expected) {
      case Object[] expectedObjects:
        assertArrayEquals(expectedObjects, (Object[]) actual);
        break;
      case byte[] expectedBytes:
        assertArrayEquals(expectedBytes, (byte[]) actual);
        break;
      case short[] expectedShorts:
        assertArrayEquals(expectedShorts, (short[]) actual);
        break;
      case int[] expectedInts:
        assertArrayEquals(expectedInts, (int[]) actual);
        break;
      case long[] expectedLongs:
        assertArrayEquals(expectedLongs, (long[]) actual);
        break;
      case float[] expectedFloats:
        assertArrayEquals(expectedFloats, (float[]) actual);
        break;
      case double[] expectedDoubles:
        assertArrayEquals(expectedDoubles, (double[]) actual);
        break;
      case boolean[] expectedBooleans:
        assertArrayEquals(expectedBooleans, (boolean[]) actual);
        break;
      case char[] expectedChars:
        assertArrayEquals(expectedChars, (char[]) actual);
        break;
      default:
        assertEquals(expected, actual);
        break;
    }
  }

  protected static void assertAllSame(Object expected, Object actual) {
    assertAllEquals(expected, actual);
    if (expected.getClass().isArray()) {
      assertAllEquals(expected, actual);
      switch (expected) {
        case Object[] expectedObjects:
          for (int i = 0; i < expectedObjects.length; ++i) {
            assertSame(expectedObjects[i], ((Object[]) actual)[i]);
          }
          break;
        default:
          break;
      }
    } else {
      assertSame(expected, actual);
    }
  }

  private TestCase instance;

  protected TestCase() {
    instance = DEFAULT;
  }

  @Override
  public MutableOrder clone() throws CloneNotSupportedException {
    return (MutableOrder) OMEGA.clone();
  }

  @Override
  public MutableOrder order() {
    return ORDER;
  }

  public void register(TestCase factory) {
    instance = factory;
  }

  public TestData testData(
    String baseUrl,
    Optional<WireMockRuntimeInfo> wiremock
  ) throws IOException, URISyntaxException {
    return instance.testData(baseUrl, wiremock);
  }

  @Override
  public Args extend(Ordinal col, Object... arrays) {
    return A.extend(col, arrays);
  }
}
