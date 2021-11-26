package com.nikondsl.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtil {
    public static byte[] zip(byte[] uncompressedBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Deflater compresser = new Deflater();
            compresser.setInput(uncompressedBytes);
            byte[] buffer = new byte[1024];
            compresser.finish();
            while (!compresser.finished()) {
                int count = compresser.deflate(buffer);
                baos.write(buffer, 0, count);
            }
            compresser.end();
        } finally {
            baos.flush();
        }
        return baos.toByteArray();
    }

    public static byte[] unZip(byte[] compressedBytes) throws IOException, DataFormatException {
        Inflater decompresser = new Inflater();
        decompresser.setInput(compressedBytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream(compressedBytes.length);
        try {
            byte[] buffer = new byte[1024];
            while (!decompresser.finished()) {
                int count = decompresser.inflate(buffer);
                out.write(buffer, 0, count);
            }
            out.flush();
        } finally {
            out.close();
        }
        return out.toByteArray();
    }
}
