package org.duckdns.hjow.samples.listmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListManager {
    public static void main(String[] args) throws IOException {
        process(new File("D:\\배포 대상 소스\\20250403\\yeyak (learning 프로젝트)\\list.txt"), new File("D:\\배포 대상 소스\\20250403\\yeyak (learning 프로젝트)"), "UTF-8");
    }
    
    /** 해당 directory 에 있는 모든 파일 (디렉토리 제외) 목록을 만들어 resultFile 에 텍스트 파일로 저장 */
    public static void process(File resultFile, File directory, String charset) throws IOException {
        if(! directory.exists()) throw new FileNotFoundException("There is no directory " + directory.getAbsolutePath());
        if(! directory.isDirectory()) throw new IllegalArgumentException("There is no directory " + directory.getAbsolutePath());
        IOException caused = null;
        
        FileOutputStream   out1 = null;
        OutputStreamWriter out2 = null;
        BufferedWriter     out3 = null;
        
        List<File> lists = new ArrayList<File>();
        
        try {
            out1 = new FileOutputStream(resultFile);
            out2 = new OutputStreamWriter(out1, charset);
            out3 = new BufferedWriter(out2);
            
            lists.addAll(search(directory));
            Collections.sort(lists);
            
            for(File f : lists) {
                out3.write(f.getCanonicalPath());
                out3.newLine();
            }
        } catch(IOException ex) {
            caused = ex;
        } finally {
            if(out3 != null) try { out3.close(); } catch(Exception ignores) {}
            if(out2 != null) try { out2.close(); } catch(Exception ignores) {}
            if(out1 != null) try { out1.close(); } catch(Exception ignores) {}
        }
        
        if(caused != null) throw caused;
    }
    
    /** 파일 목록 재귀 검색 */
    private static List<File> search(File dir) {
        List<File> lists = new ArrayList<File>();
        File[] children = dir.listFiles();
        for(File f : children) {
            if(f.isDirectory()) lists.addAll(search(f));
            else lists.add(f);
        }
        return lists;
    }
}
