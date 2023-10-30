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

import java.lang.reflect.Array;

class NodeBuilder<A,B> implements Builder<A,B> {
    private final B[][] targets;
    private final int size;
    private final Class<B> type;

    private A[] keys;

    @SuppressWarnings("unchecked")
    NodeBuilder(B[][] targets) {
        size = targets.length;
        for (int i = 0; i < size; ++i) {
            if (targets[i] != null) {
                throw new IllegalArgumentException();
            }
        }
        this.targets = targets;
        this.type = (Class<B>) targets.getClass().getComponentType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Builder<String,B> keys(String... keys) {
        final int size = targets.length;
        for (int i = 0; i < size; ++i) {
            targets[i] = (B[]) Array.newInstance(type, size);
        }
        this.keys = (A[]) keys;
        return (Builder<String, B>) this;
    }

    @Override
    public Tree<Integer, Tree<A, B>> build() {
        final int size = targets.length;
        for (int i = 0; i < size; ++i) {
            assignArray(i, keys.length);
        }
        return Node.createNodes(targets, keys);
    }

    @SuppressWarnings("unchecked")
    private void assignArray(int index, int size) {
            targets[index] = (B[]) Array.newInstance(type, size);
    }
}
