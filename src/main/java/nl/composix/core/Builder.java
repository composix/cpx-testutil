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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface Builder<A,B> {
    static <T> int forEachIndex(Stream<T> stream, BiConsumer<Integer,T> consumer) {
        final AtomicInteger index = new AtomicInteger(-1);
        stream.forEach(item -> consumer.accept(index.incrementAndGet(), item));
        return index.incrementAndGet();
    }

    static void forEachIndex(final int size, Consumer<Integer> consumer) {
        for (int i = 0; i < size; ++i) {
            consumer.accept(i);
        }
    }

    static <T> Builder<Integer,T> targets(T[][] targets) {
        return new NodeBuilder<>(targets);
    }

    Builder<String,B> keys(String... keys);

    Tree<Integer,Tree<A,B>> build();
}
