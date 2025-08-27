package org.duckdns.hjow.samples.propcryptor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.egovframe.rte.fdl.cryptography.EgovCryptoService;
import org.egovframe.rte.fdl.cryptography.EgovPasswordEncoder;
import org.egovframe.rte.fdl.cryptography.impl.EgovARIACryptoServiceImpl;

public class SimpleCryptor {
    protected EgovCryptoService    service = null;
    protected AlgorithmInformation algInfo = null;
    
    // 참고 : https://www.egovframe.go.kr/wiki/doku.php?id=egovframework:rte4.2:fdl:crypto
    
    public SimpleCryptor() throws InstantiationException, IllegalAccessException {
        setService(EgovARIACryptoServiceImpl.class);
    }
    
    /**
     * 환경설정 파일의 키값(항목)을 암호화
     * @param encryptTarget 암호화값
     * @return String
     */
    public String encrypt(String encryptTarget) {
        try {
            return URLEncoder.encode(new String(new Base64().encode(service.encrypt( encryptTarget.getBytes(StandardCharsets.UTF_8), algInfo.getKey()))), "UTF-8");
        } catch(IllegalArgumentException | UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 환경설정 파일의 키값(항목)을 복호화
     * @param encryptedString 복호화값
     * @return String
     */
    public String decrypt(String encryptedString) {
        try {
            return new String(service.decrypt(new Base64().decode(URLDecoder.decode(encryptedString,"UTF-8").getBytes(StandardCharsets.UTF_8)), algInfo.getKey()));
        } catch(IllegalArgumentException | UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public Properties encryptProperties(Properties prop, String ...targetKeys) {
        if(prop == null) return null;
        Properties newOne = new Properties();
        
        Set<String> keys = prop.stringPropertyNames();
        boolean process = false;
        for(String k : keys) {
            process = true;
            
            if(targetKeys != null) {
                if(targetKeys.length >= 1) {
                    process = false;
                    for(String t : targetKeys) { if(k.equals(t)) { process = true; break; } }
                }
            }
            
            if(process) newOne.setProperty(k, encrypt(prop.getProperty(k)));
            else        newOne.setProperty(k, prop.getProperty(k));
        }
        
        return newOne;
    }
    
    public Properties decryptProperties(Properties prop, String ...targetKeys) {
        if(prop == null) return null;
        Properties newOne = new Properties();
        
        Set<String> keys = prop.stringPropertyNames();
        boolean process = false;
        for(String k : keys) {
            process = true;
            
            if(targetKeys != null) {
                if(targetKeys.length >= 1) {
                    process = false;
                    for(String t : targetKeys) { if(k.equals(t)) { process = true; break; } }
                }
            }
            
            if(process) newOne.setProperty(k, decrypt(prop.getProperty(k)));
            else        newOne.setProperty(k, prop.getProperty(k));
        }
        
        return newOne;
    }
    
    public AlgorithmInformation getAlgorithmInformation() {
        return algInfo;
    }
    
    public void setService(Class<? extends EgovCryptoService> serviceClass) throws InstantiationException, IllegalAccessException {
        service = serviceClass.newInstance();
        setAlgorithmInformation(algInfo);
    }

    public void setAlgorithmInformation(AlgorithmInformation algInfo) {
        this.algInfo = algInfo;
        
        if(algInfo != null) {
            EgovPasswordEncoder enc = new EgovPasswordEncoder();
            enc.setAlgorithm(algInfo.getAlgorithm());
            enc.setHashedPassword(algInfo.getKeyHash());
            service.setPasswordEncoder(enc);
        }
    }

    public static void main(String[] args) {
        try {
            new GUICryptor().open();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error : " + e.getMessage());
        } 
    }
}
