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

    @SuppressWarnings("unchecked")
    static <K,V> Tree<Integer,Tree<K,V>> createNodes(V[][] targets, K[] source) {
        return new Node<>((Node<K,V>[]) nodes(targets, source));
    }

    @SuppressWarnings("unchecked")
    static <K,V> Tree<String,Tree<K,V>> createTree(V[][] targets, K[] source) {
        return new Node<>(new String[targets.length], (Node<K,V>[]) nodes(targets, source));
    }

    public Node(A[] source, B[] target) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (target == null) {
            throw new NullPointerException("target");
        }
        this.source = source;
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public Node(B[] target) {
        this((A[]) new Integer[0], target);
    }

    static private Tree<?,?>[] nodes(Object[][] values, Object[] keys) {
        final int size = values.length;
        final Tree<?,?>[] result = new Node[size];
        for (int i = 0; i < size; ++i) {
            result[i] = new Node<>(keys, values[i]);
        }
        return result;
    }

    @Override
    public int capacity() {
        return source.length > 0 ? Math.min(source.length, target.length) : target.length;
    }

    @Override
    public int indexOf(A key) {
        final int size = source.length;
        for (int i = 0; i < size; ++i) {
            if (key.equals(source[i])) {
                return i;
            }
        }
        if (source instanceof Integer[]) {
            if ((int) key < target.length) {
                return (int) key;
            }
        }
        for (int i = 0; i < size; ++i) {
            if (source[i] == null) {
                source[i] = key;
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public Stream<Entry<A,B>> entries() {
        if (source instanceof Integer[]) {
            return IntStream
                .range(0, target.length)
                .filter(i -> target[i] != null)
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(
                    sourceOf(i),
                    target[i]
                ));
        }
        return IntStream
            .range(0, capacity())
            .filter(i -> source[i] != null && target[i] != null)
            .mapToObj(i -> new AbstractMap.SimpleEntry<>(
                source[i],
                target[i]
            ));
    }

    @Override
    public boolean isLeafAt(int... index) {
        return !(valueAt(index) instanceof Tree<?,?>);
    }

    @Override
    public Map<?,?> mapAt(int... index) {
        return (Map<?,?>) valueAt(index);
    }

    @Override
    public Tree<?,?> nodeAt(int... index) {
        return (Tree<?,?>) valueAt(index);
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

    @Override
    public String assign(Object value) {
        if (value.getClass().isArray()) {
            Object[] aux = (Object[]) value;
            assign(aux);
            return aux[0].toString();
        }
        return null;
    }

    @Override
    public void assign(Object... values) {
        for (int i = 0; i < target.length; ++i) {
            if (target[i] instanceof Tree) {
                String key = ((Tree<?,?>) target[i]).assign(values[i]);
                if (key == null) {
                    assignTarget(i, values);
                } else {
                    if (source.getClass().getComponentType().isAssignableFrom(String.class)) {
                        assignSource(index, key);
                    }
                }
            } else {
                if (values[i].getClass().isArray()) {
                    values[i] = new Node<>((Object[]) values[i]);
                }
                assignTarget(i, values);
            }
        }
    }

    private Object valueAt(int... index) {
        Object result = this;
        final int size = index.length;
        for (int i = 0; i < size; ++i) {
            if (result instanceof Node<?,?>) {
                result = ((Node<?,?>) result).target[i];
            } else {
                throw new NoSuchElementException();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void assignSource(int index, Object key) {
        source[index] = (A) key;
    }

    @SuppressWarnings("unchecked")
    private void assignTarget(int index, Object[] value) {
        target[index] = (B) value[index];
    }

    @SuppressWarnings("unchecked")
    private A sourceOf(int index) {
        return index < source.length ? source[index] :(A) Integer.valueOf(index);
    }
}
