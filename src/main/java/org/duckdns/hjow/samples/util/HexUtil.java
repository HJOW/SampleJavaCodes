package org.duckdns.hjow.samples.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class HexUtil {
    private static final byte[] PRESETS = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    
    public static String encode(byte[] originalBinaries) {
        byte[] hexes = new byte[originalBinaries.length * 2];
        
        for (int j = 0; j < originalBinaries.length; j++) {
            int v = originalBinaries[j] & 0xFF;
            hexes[j * 2] = PRESETS[v >>> 4];
            hexes[j * 2 + 1] = PRESETS[v & 0x0F];
        }
        
        return new String(hexes, StandardCharsets.UTF_8);
    }
    
    public static byte[] decode(String hexString) {
        return new BigInteger(hexString, 16).toByteArray();
    }
    
    public static String encodeString(String originalStr) {
        byte[] originalBinaries = originalStr.getBytes(StandardCharsets.UTF_8);
        return encode(originalBinaries);
    }
    
    public static String decodeString(String hexString) {
        return new String(decode(hexString), StandardCharsets.UTF_8);
    }
}
