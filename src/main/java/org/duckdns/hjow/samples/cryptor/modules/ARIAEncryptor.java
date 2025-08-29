package org.duckdns.hjow.samples.cryptor.modules;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

import org.duckdns.hjow.samples.interfaces.ProcessingStream;
import org.egovframe.rte.fdl.cryptography.impl.ARIACipher;

public class ARIAEncryptor implements CypherModule {
    private static final long serialVersionUID = -4472795707416498577L;

    @Override
    public String name() {
        return "ARIA Encryptor";
    }
    
    protected String prepareKey(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] digested = digest.digest(key.getBytes("UTF-8"));
        String dgKey = Base64.getEncoder().encodeToString(digested);
        return dgKey;
    }

    @Override
    public String convert(String before, String key, Properties prop) throws Exception {
        ARIACipher cipher = new ARIACipher();
        cipher.setPassword(prepareKey(key));
        
        byte[] ciphered = cipher.encrypt(before.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(ciphered);
    }

	@Override
	public byte[] convert(byte[] before, String key, Properties prop) throws Exception {
		ARIACipher cipher = new ARIACipher();
        cipher.setPassword(prepareKey(key));
        
        return cipher.encrypt(before);
	}
	
	@Override
	public void convert(InputStream inputs, OutputStream outputs, String key) throws Exception {
        convert(inputs, outputs, key, null);
	}
	
	@Override
	public void convert(InputStream inputs, OutputStream outputs, String key, ProcessingStream streamEvent) throws Exception {
        String dpKey = prepareKey(key);
        
        ARIACipher cipher = new ARIACipher();
        cipher.setPassword(dpKey);
        
        byte[] buffer1 = new byte[128];
        byte[] buffer2, buffer3;
        int read, idx;
        while(true) {
        	read = inputs.read(buffer1, 0, buffer1.length);
        	if(read < 0) break;
        	if(streamEvent != null) streamEvent.processing(buffer1, read);
        	buffer2 = new byte[read];
        	for(idx=0; idx<read; idx++) { buffer2[idx] = buffer1[idx]; }
        	buffer3 = cipher.encrypt(buffer2);
        	if(buffer3 != null) outputs.write(buffer3);
        }
        outputs.flush();
	}

	@Override
	public boolean supportStreamConvertion() {
		return true;
	}
}
