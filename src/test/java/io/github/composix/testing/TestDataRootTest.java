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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDataRootTest extends TestCase {

  TestDataRoot testData;
  List<CharSequence[]> paths;

  @BeforeEach
  void beforeEach() throws IOException, URISyntaxException {
    testData = (TestDataRoot) DEFAULT.testData(".", Optional.empty());
    paths = testData.paths;
    assertEquals(0, paths.size());
    assertFalse(testData.slash);
  }

  @Test
  void testToString() {
    assertSame("~", testData.toString());
  }

  @Test
  void testSelect() {
    assertSame(testData, testData.select("~"));
    assertEquals(0, paths.size());
    assertFalse(testData.slash);
    assertThrows(IllegalArgumentException.class, () -> testData.select("pet"));
    assertThrows(IllegalArgumentException.class, () -> testData.select("store"));

    TestData pet = testData.select("~", "pet");
    TestData store = testData.select("~", "store");

    assertSame(pet, testData.select("~", "pet"));
    assertSame(store, testData.select("~", "store"));
    // assertSame(pet, testData.select("~", null).select("pet"));
    // assertSame(store, testData.select("~", null).select("store"));
  }
}
