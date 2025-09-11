package org.duckdns.hjow.samples.charsetconv;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for converting files to another format.
 */
public interface FileConverter {
    /**
     * Convert the file to another format. The file content will be changed.
     * 
     * @param file : target file to convert
     */
    public void convert(File file);

    /**
     * Convert binaries from input stream to another format, then write to output stream.
     * 
     * @param read   : reading input stream
     * @param output : output stream to write
     */
    public void convert(InputStream read, OutputStream output);

    /**
     * Get configuration of works.
     * 
     * @param key : key of configuration
     * @return values
     */
    public String getProperty(String key);

    /**
     * Set some configs for works.
     * 
     * @param key : key of configuration
     * @param value : value to change
     */
    public void setProperty(String key, String value);
}
