package org.duckdns.hjow.samples.cryptor.modules;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Properties;

import org.duckdns.hjow.samples.interfaces.ProcessingStream;

public class Digest implements CypherModule {
    private static final long serialVersionUID = -6171211831631500507L;

    @Override
    public String name() {
        return "Digest";
    }

    @Override
    public String convert(String before, String key, Properties prop) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(key);
        return Base64.getEncoder().encodeToString(digest.digest(before.getBytes("UTF-8")));
    }

    @Override
    public byte[] convert(byte[] before, String key, Properties prop) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(key);
        return digest.digest(before);
    }

    @Override
    public void convert(InputStream inputs, OutputStream outputs, String key) throws Exception {
        convert(inputs, outputs, key, null);
    }
    
    @Override
    public void convert(InputStream inputs, OutputStream outputs, String key, ProcessingStream streamEvent) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(key);
        byte[] buffer = new byte[1024];
        int read;
        while(true) {
            read = inputs.read(buffer, 0, buffer.length);
            if(read < 0) break;
            if(streamEvent != null) streamEvent.processing(buffer, read);
            digest.update(buffer, 0, read);
        }
        outputs.write(digest.digest());
    }

    @Override
    public boolean supportStreamConvertion() {
        return true;
    }
}
