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

import java.util.AbstractMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Node<A,B> extends AbstractMap<A,B> implements Tree<A,B> {
    protected final A[] source;
    protected final B[] target;

    protected int index;

    public Node(A[] source, B[] target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public int indexOf(A key) {
        final int size = source.length;
        for (int i = 0; i < size; ++i) {
            if (key.equals(source[i])) {
                return i;
            }
        }
        for (int i = 0; i < size; ++i) {
            if (source[i] == null && target[i] == null) {
                source[i] = key;
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public Stream<Entry<A,B>> entries() {
        return IntStream.range(0, target.length).filter(i -> target[i] != null).mapToObj(i -> new AbstractMap.SimpleEntry<>(i < source.length ? source[i] : null, target[i]));
    }

    @Override
    public boolean isLeafAt(int index) {
        return !(target[index] instanceof Map<?,?>);
    }

    @Override
    public Map<?,?> mapAt(int... index) {
        Map<?,?> result = this;
        final int size = index.length;
        for (int i = 0; i < size; ++i) {
            if (isLeafAt(index[i])) {
                throw new NoSuchElementException();
            }
            result = (Map<?, ?>) target[index[i]];
        }
        return result;
    }

    @Override
    public Tree<?,?> nodeAt(int... index) {
        return (Tree<?,?>) mapAt(index);
    }

    @Override
    public void clear() {
        final int size = target.length;
        for (int i = 0; i < size; ++i) {
            if (!isLeafAt(i)) {
                mapAt(i).clear();
            }
            target[i] = null;
        }
    }

    @Override
    public Set<Entry<A, B>> entrySet() {
        return entries().collect(Collectors.toSet());
    } 
}
