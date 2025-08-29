package org.duckdns.hjow.samples.cryptor.modules;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESDecryptor extends AESEncryptor {
    private static final long serialVersionUID = -4472795707416498577L;

    @Override
    public String name() {
        return "AES Decryptor";
    }
    
    @Override
    public String convert(String before, String key, Properties prop) throws Exception {
        SecretKeySpec scKeySpec = prepareKey(key);
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, scKeySpec);
        byte[] ciphered = cipher.doFinal(Base64.getDecoder().decode(before));
        return new String(ciphered, "UTF-8");
    }

	@Override
	public byte[] convert(byte[] before, String key, Properties prop) throws Exception {
        SecretKeySpec scKeySpec = prepareKey(key);
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, scKeySpec);
        return cipher.doFinal(before);
	}

	@Override
	public void convert(InputStream inputs, OutputStream outputs, String key) throws Exception {
        SecretKeySpec scKeySpec = prepareKey(key);
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, scKeySpec);
        
        byte[] buffer1 = new byte[64];
        byte[] buffer2;
        int read;
        while(true) {
        	read = inputs.read(buffer1, 0, buffer1.length);
        	if(read < 0) break;
        	buffer2 = cipher.update(buffer1, 0, read);
        	if(buffer2 != null) outputs.write(buffer2);
        }
        buffer2 = cipher.doFinal();
        if(buffer2 != null) outputs.write(buffer2);
        outputs.flush();
	}
}
