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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;

@WireMockTest
@ExtendWith(DefaultInvocationInterceptor.class)
class PetstoreDataTest extends TestCase {

  static final String PETSTORE_API =
     "https://petstore.swagger.io/v2/swagger.json";
    // "https://petstore3.swagger.io/api/v3/openapi.json";
  static TestData testData;

  @BeforeAll
  static void beforeAll(WireMockRuntimeInfo wm)
    throws IOException, URISyntaxException {
    testData = DEFAULT.testData(PETSTORE_API, Optional.of(wm));
    DEFAULT.extendA(PETSTORE_API).extend(B, testData);
    testData
      .select("~", "pet", "findByStatus", "?status", null)
      .select("=available")
      .select("=pending")
      .select("=sold")
      .select("~", "store", "order", ":orderId", null)
      .select("=00")
      .select("=01")
      .select("=02")
      .select("=03")
      .select("=04")
      .select("=05")
      .select("=06")
      .select("=07")
      .select("=08")
      .select("=09")
      .select("=10")
      .select("=11")
      .select("=12")
      .select("=13")
      .select("=14")
      .select("=15");
    testData.select("~", "pet", "findByStatus", "?status").refresh(Pet.class);
    testData.select("~", "store", "order", ":orderId").refresh(Order.class);
    testData.readOnly();
  }

  @Test
  void testEmpty() {}
}
