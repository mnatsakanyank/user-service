package com.soundcloud.user.socket;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

class ByteBufferInputStream extends InputStream {

    private ByteBuffer mBuf;
    private final StringBuilder lineBuilder = new StringBuilder();

    @Override
    public int read() {
        if (!mBuf.hasRemaining()) {
            return -1;
        }
        return mBuf.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int off, int len) {
        if (!mBuf.hasRemaining()) {
            return -1;
        }
        len = Math.min(len, mBuf.remaining());
        mBuf.get(bytes, off, len);
        return len;
    }

    public boolean hasRemaining() {
        return mBuf.hasRemaining();
    }

    public Optional<String> readLine() {

        while (mBuf.hasRemaining()) {
            char ch = (char) mBuf.get();
            lineBuilder.append(ch);
            if (ch == '\n') {
                break;
            }
        }

        if (isLine()) {
            String line = lineBuilder.toString();
            lineBuilder.setLength(0);
            return Optional.of(line);
        }

        return Optional.empty();
    }

    private boolean isLine() {
        String maybeLine = lineBuilder.toString();
        return maybeLine.endsWith("\n") || maybeLine.endsWith("\r");
    }

    public void setmBuf(ByteBuffer mBuf) {
        this.mBuf = mBuf;
    }
}