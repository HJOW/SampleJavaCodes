package org.duckdns.hjow.samples.cryptor.modules;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.duckdns.hjow.samples.interfaces.ProcessingStream;

public class AESEncryptor implements CypherModule {
    private static final long serialVersionUID = -4472795707416498577L;

    @Override
    public String name() {
        return "AES Encryptor";
    }
    
    protected SecretKeySpec prepareKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] digested = digest.digest(key.getBytes("UTF-8"));
        String dgKey = Base64.getEncoder().encodeToString(digested);
        digested = null;
        
        if(     dgKey.length() > 32) dgKey = dgKey.substring(0, 32);
        else if(dgKey.length() > 24) dgKey = dgKey.substring(0, 24);
        else if(dgKey.length() > 16) dgKey = dgKey.substring(0, 16);
        else {
            dgKey += "1234567890ABCDEF";
            dgKey = dgKey.substring(0, 16);
        }
        
        SecretKeySpec scKeySpec = new SecretKeySpec(dgKey.getBytes("UTF-8"), "AES");
        return scKeySpec;
    }

    @Override
    public String convert(String before, String key, Properties prop) throws Exception {
        SecretKeySpec scKeySpec = prepareKey(key);
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, scKeySpec);
        byte[] ciphered = cipher.doFinal(before.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(ciphered);
    }

    @Override
    public byte[] convert(byte[] before, String key, Properties prop) throws Exception {
        SecretKeySpec scKeySpec = prepareKey(key);
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, scKeySpec);
        return cipher.doFinal(before); 
    }

    @Override
    public void convert(InputStream inputs, OutputStream outputs, String key) throws Exception {
        convert(inputs, outputs, key, null);
    }
    
    @Override
    public void convert(InputStream inputs, OutputStream outputs, String key, ProcessingStream streamEvent) throws Exception {
        SecretKeySpec scKeySpec = prepareKey(key);
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, scKeySpec);
        
        byte[] buffer1 = new byte[64];
        byte[] buffer2;
        int read;
        while(true) {
            read = inputs.read(buffer1, 0, buffer1.length);
            if(read < 0) break;
            if(streamEvent != null) streamEvent.processing(buffer1, read);
            buffer2 = cipher.update(buffer1, 0, read);
            if(buffer2 != null) outputs.write(buffer2);
        }
        buffer2 = cipher.doFinal();
        if(buffer2 != null) outputs.write(buffer2);
        outputs.flush();
    }

    @Override
    public boolean supportStreamConvertion() {
        return true;
    }
}
