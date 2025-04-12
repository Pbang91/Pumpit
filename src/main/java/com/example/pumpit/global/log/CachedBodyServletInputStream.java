package com.example.pumpit.global.log;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CachedBodyServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream inputStream;

    public CachedBodyServletInputStream(byte[] cacheBody) {
        this.inputStream = new ByteArrayInputStream(cacheBody);
    }

    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        // NOTE: 비동기 io 해줄려면 설정
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}
