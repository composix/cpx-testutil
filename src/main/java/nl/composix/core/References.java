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

import java.util.HashMap;

public final class References<T> extends HashMap<T,int[]> {
    private static final byte BITS = 32;

    private byte[] sizing = new byte[] {8};
    private boolean initialized = false;

    public byte register(int bits) {
        if (initialized) {
            throw new IllegalStateException("registration not allowed after initialization");
        }
        final int size = sizing.length;
        final byte[] result = new byte[size + 1];
        System.arraycopy(sizing, 0, result, 0, size);
        sizing = result;
        sizing[size] = (byte) bits;
        return (byte) size;
    }

    public int indexOf(byte pos, T reference) {
        validate(pos);
        int[] ref = get(reference);
        if (ref == null || (ref[0] & (1 << pos)) == 0) {
            return -1;
        }
        final int offset = offset(pos, ref),
            index = offset / BITS,
            shift = offset % BITS;
        int result = ref[index] >>> shift;
        int overflow = (shift + sizing[pos]) - BITS;
        if (overflow > 0) {
            result |= ref[index + 1] << (BITS - shift);
        } else {
            result &= (1 << sizing[pos]) - 1;
        }
        return result;
    }

    public int indexOf(byte pos, T reference, int limit) {
        if (reference == null) {
            throw new NullPointerException();
        }
        int[] ref = get(reference);
        if (ref == null) {
            validate(pos);
            final int shift = sizing.length;
            if (shift + sizing[pos] > BITS) {
                ref = new int[] {limit << shift | (1 << pos), limit >>> (BITS - shift)};
            } else {
                ref = new int[] {limit << shift | (1 << pos)};
            }
            put(reference,ref);
            return limit;
        }
        int result = indexOf(pos, reference);
        if (result < 0) {
            final int offset = offset(pos, limit),
                index = offset / BITS,
                shift = index == 0 ? (offset % BITS) + sizing.length : offset % BITS;
            ref[index] = limit << shift;
            if (shift + sizing[index] > BITS) {
                ref[index + 1] = limit >>> (BITS - shift);
            }
            result = limit;
        }
        return result;
    }

    private void validate(byte pos) {
        initialized = true;
        if (pos >= sizing.length || pos < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    private int offset(byte pos, int... index) {
        int offset = 0, mask = 1;
        for (int i = 0; i < pos; ++i) {
            if ((index[0] & mask) != 0) {
                offset += sizing[i];
            }
            mask <<= 1;
        }
        return offset + sizing.length;
    }
}
