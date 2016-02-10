/*
 * Copyright 2016 Jin Kwon &lt;jinahya_at_gmail.com&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jinahya.verbose.hex;


import static com.github.jinahya.verbose.hex.Decoded.BYTES;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.Channels.newChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.openjdk.jmh.annotations.Benchmark;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class WritableHexChannelBenchmark {


    private void copy(final ReadableByteChannel readable,
                      final WritableByteChannel writable)
        throws IOException {
        final ByteBuffer buffer = allocate(128);
        while (readable.read(buffer) != -1) {
            buffer.flip();
            writable.write(buffer);
            buffer.compact();
        }
        for (buffer.flip(); buffer.hasRemaining();) {
            writable.write(buffer);
        }
        buffer.clear();
    }


    @Benchmark
    public void aux(final Decoded decoded) throws IOException {
        try (ReadableByteChannel readable
            = newChannel(new ByteArrayInputStream(decoded.bytes))) {
            final WritableByteChannel channel
                = newChannel(new ByteArrayOutputStream(BYTES));
            final HexEncoder encoder = new HexEncoderImpl();
            try (WritableByteChannel writable
                = new WritableHexChannel(channel, encoder)) {
                copy(readable, writable);
            }
        }
    }


    public void buf(final Decoded decoded, final int capacity)
        throws IOException {
        try (ReadableByteChannel readable
            = newChannel(new ByteArrayInputStream(decoded.bytes))) {
            final WritableByteChannel channel
                = newChannel(new ByteArrayOutputStream(BYTES));
            final HexEncoder encoder = new HexEncoderImpl();
            try (WritableByteChannel writable
                = new WritableHexChannelEx(channel, encoder, capacity)) {
                copy(readable, writable);
            }
        }
    }


    @Benchmark
    public void buf64(final Decoded decoded) throws IOException {
        buf(decoded, 0x40);
    }


    @Benchmark
    public void buf128(final Decoded decoded) throws IOException {
        buf(decoded, 0x80);
    }


    @Benchmark
    public void buf192(final Decoded decoded) throws IOException {
        buf(decoded, 192);
    }


    @Benchmark
    public void buf256(final Decoded decoded) throws IOException {
        buf(decoded, 256);
    }

}

