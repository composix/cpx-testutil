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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class TestDataNode extends CharSequenceNode implements TestData {

  static final int SHIFT = 24, MASK = (1 << SHIFT) - 1;

  private final TestDataRoot root;
  private final int pos;

  private Object[] data;

  TestDataNode(CharSequence segment, TestDataRoot root, int pos) {
    super(segment);
    this.root = root;
    this.pos = pos;
  }

  @Override
  public <R extends Record> TestData refresh(Class<R> type) throws IOException {
    refreshAny(type);
    return this;
  }

  @Override
  public TestData refreshLines() throws IOException {
    refreshAny(String.class);
    return this;
  }

  private void refreshAny(Class<?> type) throws IOException {
    if (root.readOnly) {
      throw new IllegalStateException();
    }
    int position = position();
    int index = position >>> SHIFT;
    position &= MASK;
    CharSequence[] path = root.paths.get(position);
    if (++index == path.length) {
      if (Record.class.isAssignableFrom(type)) {
        readData(type, path);
      } else {
        readLines(path);
      }
    } else {
      final List<CharSequence> prefix = Arrays.asList(path).subList(0, index);
      while (prefix.equals(Arrays.asList(path).subList(0, index))) {
        ((TestDataNode) path[path.length - 1]).refreshAny(type);
        try {
          path = root.paths.get(++position);
        } catch (IndexOutOfBoundsException e) {
          break;
        }
      }
    }
  }

  private void readData(Class<?> type, CharSequence... path)
    throws IOException {
    final int length = path.length;
    boolean multi = true;
    if (
      path[length - 1].charAt(0) == '=' && path[length - 2].charAt(0) == ':'
    ) {
      multi = false;
    }
    final URI uri = URI.create(pathToUrl((byte) length, path));
    if (multi) {
      data = (Object[]) root.mapper.readValue(
        root.base.resolve(uri).toURL(),
        type.arrayType()
      );
    } else {
      try {
        Object item = root.mapper.readValue(
          root.base.resolve(uri).toURL(),
          type
        );
        data = new Object[] { item };
      } catch (FileNotFoundException e) {
        data = null;
      }
    }
  }

  private void readLines(CharSequence... path) throws IOException {
    try (
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(
          URI.create(pathToUrl((byte) path.length, path)).toURL().openStream()
        )
      )
    ) {
      data = reader.lines().toArray(String[]::new);
    } catch (IOException e) {
      throw e;
    }
  }

  @Override
  public TestData select(CharSequence... path) {
    int position = position();
    int index = position >>> SHIFT;
    position &= MASK;
    CharSequence[] current = root.paths.get(position);
    if (path.length == 0) {
      return (TestData) current[--index];
    }
    if ("~".equals(path[0].toString())) {
      return root.select(path);
    }
    if (slash) {
      ++index;
      slash = false;
    }
    int lastIndex = index + path.length - 1;
    for (int i = 0; i < path.length; ++i) {
      if (current[index + i] == null) {
        current[index + i] = path[i];
      } else if (!current[index + i].equals(path[i])) {
        final int length = index + path.length;
        current = Arrays.copyOf(current, length);
        System.arraycopy(path, i, current, index + i, path.length - i);
        int pos = Collections.binarySearch(
          root.paths,
          current,
          TestDataRoot::compare
        );
        if (pos >= 0) {
          throw new IllegalStateException();
        }
        pos = -pos - 1;
        root.paths.add(pos, current);
        pos |= lastIndex << SHIFT;
        return (TestData) (current[length - 1] = new TestDataNode(
            path[path.length - 1],
            root,
            pos
          ));
      }
    }
    CharSequence result = current[lastIndex];
    if (result instanceof TestData) {
      return (TestData) result;
    }
    return (TestData) (current[lastIndex] = new TestDataNode(
        result,
        root,
        position | (lastIndex << SHIFT)
      ));
  }

  @Override
  public <T> Stream<T> collect() {
    CharSequence[] path = myFirstPath();
    return (Stream<T>) Stream.of(((TestDataNode) path[path.length - 1]).data);
  }

  private int position() {
    if (root.paths.get(pos & MASK)[pos >>> SHIFT] == this) {
      return pos;
    }
    throw new IllegalStateException();
  }

  private CharSequence[] myFirstPath() {
    final List<CharSequence[]> paths = root.paths;
    for (int i = 0; i < paths.size(); ++i) {
      final CharSequence[] path = paths.get(i);
      for (int j = 0; j < path.length; ++j) {
        if (path[j] == this) {
          return path;
        }
      }
    }
    throw new IllegalStateException();
  }

  private String pathToUrl(final byte size, final CharSequence... path) {
    final StringWriter writer = new StringWriter();
    boolean flag = false;
    boolean query = true;
    for (int i = 0; i < size; ++i) {
      final CharSequence part = path[i];
      switch (part.charAt(0)) {
        case '?':
          if (flag) {
            writer.append('&');
          }
        case '=':
          if (query) {
            writer.append(part);
          } else {
            writer.append('/');
            writer.append(part.subSequence(1, part.length()));
          }
          break;
        case '~':
          writer.append('.');
          break;
        case ':':
          query = false;
          break;
        default:
          writer.append('/');
          writer.append(part);
      }
    }
    return writer.toString();
  }

  @Override
  public void readOnly() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'readOnly'");
  }
}
