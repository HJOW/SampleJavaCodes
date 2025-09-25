package org.duckdns.hjow.samples.util;

import java.nio.charset.StandardCharsets;

public class HexUtil {
    public static String encode(byte[] originalBinaries) {
        StringBuilder res = new StringBuilder("");
        for(byte b : originalBinaries) {
            res = res.append(String.format("%02x", b & 0xff));
        }
        return res.toString().trim();
    }
    
    public static byte[] decode(String hexString) {
        if(hexString.length() % 2 != 0) hexString = "0" + hexString;
        byte[] res = new byte[hexString.length() / 2];
        for(int idx=0; idx<hexString.length(); idx+= 2) {
            String byteStr = hexString.substring(idx, idx + 2);
            int byteVal = Integer.parseInt(byteStr, 16);
            res[idx/2] = (byte) byteVal;
        }
        
        return res;
    }
    
    public static String encodeString(String originalStr) {
        byte[] originalBinaries = originalStr.getBytes(StandardCharsets.UTF_8);
        return encode(originalBinaries);
    }
    
    public static String decodeString(String hexString) {
        return new String(decode(hexString), StandardCharsets.UTF_8);
    }
}
