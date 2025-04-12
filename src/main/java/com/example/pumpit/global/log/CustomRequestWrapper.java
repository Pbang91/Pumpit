package com.example.pumpit.global.log;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Getter
public class CustomRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] cachedBody;

    public CustomRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream); // body를 byte[]로 캐싱
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public String getCachedBodyAsString() {
        return new String(this.cachedBody, StandardCharsets.UTF_8);
    }
}
