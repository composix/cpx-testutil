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

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;

import io.github.composix.math.Args;
import io.github.composix.math.ArgsOrdinal;
import io.github.composix.math.MutableOrder;
import io.github.composix.math.Ordinal;
import io.github.composix.math.SafeMatrix;

public class TestCase implements ArgsOrdinal{
    protected static final TestCase DEFAULT = new DefaultTestCase();
    private static final MutableOrder ORDER = OMEGA.order();

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
    };

    public void register(TestCase factory) {
        instance = factory;
    }

    public TestData testData(WireMockRuntimeInfo wm, String baseUrl, boolean wiremock) throws IOException, URISyntaxException {
        return instance.testData(wm, baseUrl, wiremock);
    }

    @Override
    public Args extend(Ordinal col, Object... arrays) {
        return new SafeMatrix().extend(col, arrays);
    }
}
