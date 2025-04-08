package com.example.pumpit.global.log;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.*;

public class CustomResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream cachedOutputStream = new ByteArrayOutputStream();
        private ServletOutputStream outputStream;
        private PrintWriter writer;
        private boolean isWriterUsed = false;
        private boolean isOutputStreamUsed = false;

        public CustomResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (isWriterUsed) {
                throw new IllegalStateException("getWriter() 가 이미 response에서 호출됨");
            }

            isOutputStreamUsed = true;

            if (outputStream == null) {
                outputStream = new CachedBodyServletOutputStream(cachedOutputStream);
            }

            return outputStream;
        }

        @Override
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (isOutputStreamUsed) {
                throw new IllegalStateException("getOutputStream() 가 이미 response에서 호출됨");
            }

            isWriterUsed = true;

            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(cachedOutputStream, getCharacterEncoding()), true);
            }
            return writer;
        }

        public byte[] getCachedBody() {
            if (writer != null) {
                writer.flush(); // 버퍼에 남은 데이터를 flush해서 body에 포함되게
            }

            return cachedOutputStream.toByteArray();
        }

        public void copyBodyToResponse() throws IOException {
            byte[] body = getCachedBody();

            HttpServletResponse response = (HttpServletResponse) getResponse();
            ServletOutputStream out = response.getOutputStream();

            out.write(body);
            out.flush();
        }
}
