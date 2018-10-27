package com.translate.manga.front.util;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class UnzipFile {

    public List<byte[]> readZipStream(InputStream in) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(in);

        ZipEntry entry;

        List<byte[]> bytesList=new ArrayList<>();


        while ((entry = zipIn.getNextEntry()) != null) {
            byte[] bytes=IOUtils.toByteArray(zipIn);
            bytesList.add(bytes);

            zipIn.closeEntry();
        }
        return bytesList;
    }
}
