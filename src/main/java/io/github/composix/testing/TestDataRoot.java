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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;

class TestDataRoot extends CharSequenceNode implements TestData {
    final WireMockServer server;
    final ObjectMapper mapper;
    final URI base, uriSwagger;
    final JsonNode swagger;
    final List<CharSequence[]> paths;

    boolean readOnly = false;

    static int compare(CharSequence[] lhs, CharSequence[] rhs) {
        return Arrays.compare(lhs, rhs, Comparator.comparing(Object::toString));
    }

    TestDataRoot(WireMockRuntimeInfo wm, URI uri, boolean wiremock) throws IOException, URISyntaxException {
        super("~");
        server = new WireMockServer();
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final URI base = uri.resolve("");
        uriSwagger = base.relativize(uri);
        if (wiremock) {
            URI baseWM = new URI(wm.getHttpBaseUrl());
            final URL url = baseWM.resolve(uriSwagger).toURL();
            JsonNode swagger;
            try {
                swagger = mapper.readTree(url);
                try {
                    if (!swagger.equals(mapper.readTree(uri.toURL()))) {
                        throw new IllegalStateException("OpenAPI spec has changed: " + uri);
                    }
                } catch(IOException e) {
                    // silently ignore
                }
            } catch (IOException e) {
                swagger = mapper.readTree(uri.toURL());
                final String baseUrl = base.toString();
                server.start();
                server.startRecording(baseUrl.substring(0, baseUrl.length() - 1));
                baseWM = new URI("http://localhost:8080");
                if (!swagger.equals(mapper.readTree(baseWM.resolve(uriSwagger).toURL()))) {
                    throw new IllegalStateException("OpenAPI spec has changed: " + uri);
                }
            }
            this.base = baseWM;
            this.swagger = swagger;
        } else {
            this.base = base;
            swagger = mapper.readTree(uri.toURL());
        }
        paths = new ArrayList<>();
    }

    @Override
    public TestData select(CharSequence... path) {
        final int length = path.length;
        int index = Collections.binarySearch(paths, path, TestDataRoot::compare);
        CharSequence[] rhs;
        if (index < 0) {
            index = -index - 1;
            if (index == paths.size()) {
                if (!"~".equals(path[0])) {
                    throw new IllegalArgumentException();
                }    
                paths.add(path);
            }
            rhs = paths.get(index);
            for (int i = 0; i < length; ++i) {
                if (path[i] != rhs[i] && !rhs[i].equals(path[i])) {
                    if (!"~".equals(path[0])) {
                        throw new IllegalArgumentException();
                    }    
                    paths.add(index, path);
                    rhs = path;
                    break;
                }
            }
        } else {
            rhs = paths.get(index);
        }
        int lastIndex = length - 1;
        int pos = index;
        CharSequence result = rhs[lastIndex];
        if (result == null) {
            result = rhs[--lastIndex];
            if (!(result instanceof TestData)) {
                pos |= lastIndex << TestDataNode.SHIFT;
                result = rhs[lastIndex] = new TestDataNode(result, this, pos);
            }
            ((TestDataNode) result).slash = true;
        } else if (result instanceof TestData) {
            ((TestDataNode) result).slash = false;
        } else {
            pos |= (short) (lastIndex << TestDataNode.SHIFT);
            result = rhs[lastIndex] = new TestDataNode(result, this, pos);
        }
        return (TestData) result;
    }

    @Override
    public <R extends Record> TestData refresh(Class<R> recordClass) throws IOException {
        if (readOnly) {
            throw new IllegalStateException();
        }
        try {
            paths.forEach(path -> {
                final int length = path.length;
                for (int i = 0; i < length; ++i) {
                    CharSequence segment = path[i];
                    if (segment instanceof TestData) {
                        try {
                            ((TestData)segment).refresh(recordClass);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            });    
        } catch(IllegalStateException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
        return this;
    }

    @Override
    public <T> Stream<T> collect() {
        return Stream.empty();
    }

    @Override
    public void readOnly() {
        readOnly = true;
        if (server.isRunning()) {
            server.stopRecording();
        }
    }
}
