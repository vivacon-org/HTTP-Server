package org.vivacon.contract;

import java.io.IOException;
import java.io.InputStream;

public interface Part {
    InputStream getInputStream() throws IOException;

    String getContentType();

    String getName();

    String getSubmittedFileName();

    long getSize();

    void write(String content) throws IOException;

    void delete() throws IOException;
}
