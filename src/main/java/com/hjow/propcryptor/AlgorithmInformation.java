package com.hjow.propcryptor;

import java.io.Serializable;

import org.egovframe.rte.fdl.cryptography.EgovPasswordEncoder;

public class AlgorithmInformation implements Serializable {
    private static final long serialVersionUID = -133352964180829457L;
    protected String algorithm, key;
    protected int    blockSize = 1024;
    public AlgorithmInformation() {
        super();
    }
    public AlgorithmInformation(String algorithm, String key) {
        this(algorithm, key, 1024);
    }
    public AlgorithmInformation(String algorithm, String key, int blockSize) {
        this();
        this.algorithm = algorithm;
        this.key = key;
        this.blockSize = blockSize;
    }
    public String getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public int getBlockSize() {
        return blockSize;
    }
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
    /** 계정 암호화 키의 해시값 반환 */
    public String getKeyHash() {
        EgovPasswordEncoder egovEncoder = new EgovPasswordEncoder();
        egovEncoder.setAlgorithm(getAlgorithm());
        return egovEncoder.encryptPassword(getKey());
    }
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("");
        res = res.append("\n").append("알고리즘 \t: ").append(getAlgorithm());
        res = res.append("\n").append("블럭 크기 \t: ").append(getBlockSize());
        res = res.append("\n").append("키 \t: ").append(getKey());
        res = res.append("\n").append("키 Hash \t: ").append(getKeyHash());
        
        return res.toString().trim();
    }
}
