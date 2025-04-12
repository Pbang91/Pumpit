package com.example.pumpit.global.log;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.IOException;
import java.io.OutputStream;

public class CachedBodyServletOutputStream extends ServletOutputStream {
    private final OutputStream cachedOutputStream;

    public CachedBodyServletOutputStream(OutputStream cachedOutputStream) {
        this.cachedOutputStream = cachedOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        cachedOutputStream.write(b);
    }

    @Override
    public boolean isReady() {
        return true; // 항상 준비 완료 상태로 처리
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        // NOTE: 비동기 io 해줄려면 설정
    }
}
