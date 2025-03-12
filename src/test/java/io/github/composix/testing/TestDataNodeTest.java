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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestDataNodeTest extends TestCase {

  static TestData testData;
  static List<CharSequence[]> paths;

  @BeforeAll
  static void beforeAll() throws IOException, URISyntaxException {
    testData = DEFAULT.testData("./", Optional.empty());
    assertSame(TestDataRoot.class, testData.getClass());
    paths = ((TestDataRoot) testData).paths;
  }

  @Test
  void testToString() throws IOException, URISyntaxException {
    assertAllSame(
      all("pet","store"),
      all(
        testData("./", Optional.empty()).select("~", "pet").toString(),
        testData("./", Optional.empty()).select("~", "store").toString()
      )
    );
  }

  @Test
  void testSelect() {
    testData
      .select("~", "pet", "findByStatus", "?status", null)
      .select("=available")
      .select("=pending")
      .select("=sold")
      .select("~", "store", "order", ":orderId", null)
      .select("=00")
      .select("=01")
      .select("=02");

    final int size = paths.size();
    assertEquals(6, size);
    for (int i = 0; i < size; ++i) {
      assertAllSame(testData, paths.get(i)[0]);
      if (i < size >> 1) {
        assertAllSame(
          all("pet", "findByStatus", "?status"),
          all(
            (String) paths.get(i)[1],
            (String) paths.get(i)[2],
            (String) paths.get(i)[3].toString()
          )
        );
      } else {
        assertAllSame(
          all("store","order",":orderId"),
          all(
            (String) paths.get(i)[1],
            (String) paths.get(i)[2],
            (String) paths.get(i)[3].toString()            
          )
        );
      }
    }
  }

  @Test
  void testRefreshLines() throws IOException, URISyntaxException {
    TestData pwd = testData("./", Optional.empty());
    pwd.select("~", null)
      .select(".gitignore").refreshLines()
      .select("LICENSE").refreshLines();
    assertAllEquals(
      all("target/"), 
      all(pwd.select("~",".gitignore").collect().limit(1).toArray(String[]::new))
    );
    assertAllEquals(
      all("MIT License", "", "Copyright (c) 2023-2025 composix", ""),
      all(pwd.select("~","LICENSE").collect().limit(4).toArray(String[]::new))
    );
  }
}
