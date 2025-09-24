package org.duckdns.hjow.samples.cryptor.modules;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;

import org.egovframe.rte.fdl.cryptography.impl.ARIACipher;

public class ARIADecryptor extends ARIAEncryptor {
    private static final long serialVersionUID = -4472795707416498577L;

    @Override
    public String name() {
        return "ARIA Decryptor";
    }

    @Override
    public String convert(String before, String key, Properties prop) throws Exception {
        ARIACipher cipher = new ARIACipher();
        cipher.setPassword(prepareKey(key));
        
        byte[] ciphered = cipher.decrypt(Base64.getDecoder().decode(before));
        return new String(ciphered, "UTF-8");
    }

    @Override
    public byte[] convert(byte[] before, String key, Properties prop) throws Exception {
        ARIACipher cipher = new ARIACipher();
        cipher.setPassword(prepareKey(key));
        
        return cipher.decrypt(before);
    }

    @Override
    public void convert(InputStream inputs, OutputStream outputs, String key) throws Exception {
        String dpKey = prepareKey(key);
        
        ARIACipher cipher = new ARIACipher();
        cipher.setPassword(dpKey);
        
        byte[] buffer1 = new byte[128];
        byte[] buffer2, buffer3;
        int read, idx;
        while(true) {
            read = inputs.read(buffer1, 0, buffer1.length);
            if(read < 0) break;
            buffer2 = new byte[read];
            for(idx=0; idx<read; idx++) { buffer2[idx] = buffer1[idx]; }
            buffer3 = cipher.decrypt(buffer2);
            if(buffer3 != null) outputs.write(buffer3);
        }
        outputs.flush();
    }
}
