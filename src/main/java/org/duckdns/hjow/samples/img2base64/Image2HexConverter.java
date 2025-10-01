package org.duckdns.hjow.samples.img2base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.StringTokenizer;

import org.duckdns.hjow.commons.util.HexUtil;

/** 이미지 (jpg, png) 파일을 입력받아 HEX 문자열 출력 */
public class Image2HexConverter {
    /** 이미지 (jpg, png) 파일을 입력받아 HEX 문자열 출력, 확장자를 따로 입력해 주어야 함 (jpg, png만 허용) */
    public String convert(File file, String ext) {
        if(file == null) throw new RuntimeException("There is no file !");
        if(! file.exists()) throw new RuntimeException("There is no file at " + file.getAbsolutePath());
        if(file.isDirectory()) throw new RuntimeException("Please input image file !");
        
        FileInputStream fileIn = null;
        ByteArrayOutputStream collector = new ByteArrayOutputStream();
        StringBuilder res = new StringBuilder("");
        byte[] buffer1 = new byte[2048];
        try {
            fileIn = new FileInputStream(file);
            int r;
            
            while(true) {
                r = fileIn.read(buffer1, 0, buffer1.length);
                if(r < 0) break;
                
                collector.write(buffer1, 0, r);
            }
            
            fileIn.close(); fileIn = null;
            res = res.append( HexUtil.encode(collector.toByteArray()) );
            collector = null;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if(fileIn != null) { try { fileIn.close(); } catch(Exception ignores) {} }
        }
        
        return res.toString().trim();
    }
    
    /** 이미지 (jpg, png) 파일을 입력받아 HEX 문자열 출력, 파일 이름으로 확장자를 판단 */
    public String convert(File file) {
        if(file == null) throw new RuntimeException("There is no file !");
        if(! file.exists()) throw new RuntimeException("There is no file at " + file.getAbsolutePath());
        if(file.isDirectory()) throw new RuntimeException("Please input image file !");
        
        String lasts = getExt(file);
        
        if(lasts.equals("jpg")) return convert(file, "jpg");
        if(lasts.equals("png")) return convert(file, "png");
        
        throw new IllegalArgumentException("Unknown image ext");
    }
    
    /** 해당 파일의 확장자를 반환 */
    public static String getExt(File file) {
        String name = file.getName().toLowerCase();
        String lasts = "";
        boolean firsts = true;
        
        StringTokenizer dotTokenizer = new StringTokenizer(name, ".");
        while(dotTokenizer.hasMoreTokens()) {
            if(firsts) { firsts = false; continue; }
            lasts = dotTokenizer.nextToken();
        }
        
        return lasts.trim();
    }
}
