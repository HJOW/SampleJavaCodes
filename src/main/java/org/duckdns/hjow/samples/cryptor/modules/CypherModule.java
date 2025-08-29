package org.duckdns.hjow.samples.cryptor.modules;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;

import org.duckdns.hjow.samples.interfaces.ProcessingStream;

/** 문자열 변환을 지원하는 클래스임을 표시할 수 있는 인터페이스 */
public interface CypherModule extends Serializable {
    public String name();
    public String convert(String before, String key, Properties prop) throws Exception;
    public byte[] convert(byte[] before, String key, Properties prop) throws Exception;
    public void convert(InputStream inputs, OutputStream outputs, String key) throws Exception;
    public void convert(InputStream inputs, OutputStream outputs, String key, ProcessingStream streamEvent) throws Exception;
    public boolean supportStreamConvertion();
}
