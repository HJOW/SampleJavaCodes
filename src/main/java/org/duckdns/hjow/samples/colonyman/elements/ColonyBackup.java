package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;
import org.duckdns.hjow.commons.util.HexUtil;
import org.duckdns.hjow.samples.colonyman.ColonyClassLoader;
import org.duckdns.hjow.samples.colonyman.GlobalLogs;

/** 정착지 백업 객체를 위한 클래스, 정착지 목록과 이름, 설명, 생성일자를 포함 */
public class ColonyBackup implements Serializable {
    private static final long serialVersionUID = -1648482045060274157L;
    protected List<Colony> colonies = new Vector<Colony>();
    protected String name, description;
    protected long created = 0L;
    public ColonyBackup() { }
    
    /** JSON 으로부터 불러오기 (단, 암호화된 경우 이 메소드로 불러올 경우 정착지 목록은 비어있게 됨) */
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        setDescription(json.get("desc").toString());
        setCreated(Long.parseLong(json.get("created").toString()));
        
        String security = json.get("security").toString().trim();
        if(security == null  ) security = "";
        if(security == "null") security = "";
        security = security.trim();
        
        JsonArray list = null;
        if(security.equals("")) {
            list = (JsonArray) json.get("list");
            colonies.clear();
            
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Colony col = ColonyClassLoader.loadColony((JsonObject) o);
                        colonies.add(col);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, true);
                    }
                }
            }
        }
    }
    
    /** JSON 으로부터 불러오기 (암호화된 경우 이 메소드 사용) */
    public void fromJson(JsonObject json, String password) {
        setName(json.get("name").toString());
        setDescription(json.get("desc").toString());
        setCreated(Long.parseLong(json.get("created").toString()));
        
        String security = json.get("security").toString().trim();
        if(security == null  ) security = "";
        if(security == "null") security = "";
        security = security.trim();
        
        String strVer = json.get("backupver").toString().trim();
        int ver = Integer.parseInt(strVer);
        
        JsonArray list = null;
        if(security.equals("")) {
            list = (JsonArray) json.get("list");
            colonies.clear();
        } else if(security.equalsIgnoreCase("AES")) {
            String hexStr   = json.get("list").toString().trim();
            
            try {
                byte[] decoded = HexUtil.decode(hexStr);
                hexStr = null;
                
                // 비밀번호 해싱
                String padded = getHashLeftSalt(ver) + password + getHashRightSalt(ver);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] pwdigested = digest.digest(padded.getBytes("UTF-8"));
                String pwencoded  = HexUtil.encode(pwdigested).substring(0, 32);
                pwdigested = null;
                
                // 복호화
                SecretKeySpec scKeySpec = new SecretKeySpec(pwencoded.getBytes("UTF-8"), "AES");
                IvParameterSpec iv = new IvParameterSpec(getIv(ver).getBytes("UTF-8"));
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, scKeySpec, iv);
                byte[] ciphered = cipher.doFinal(decoded);
                decoded = null;
                
                String decryptedString = new String(ciphered, "UTF-8").trim();
                ciphered = null;
                cipher = null;
                
                list = (JsonArray) JsonObject.parseJson(decryptedString);
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Colony col = ColonyClassLoader.loadColony((JsonObject) o);
                        col.fromJson((JsonObject) o);
                        colonies.add(col);
                    } catch(Exception ex) {
                        GlobalLogs.processExceptionOccured(ex, true);
                    }
                }
            }
        }
    }
    
    /** JSON 빌드 (단, 이 메소드로 빌드 시 보안 옵션이 적용되지 않음) */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "ColonyGroup");
        json.put("name", getName());
        json.put("desc", getDescription());
        json.put("created", new Long(getCreated()));
        json.put("security", "");
        json.put("backupver", new Integer(getBackupVersion()));
        
        JsonArray list = new JsonArray();
        for(Colony c : getColonies()) { list.add(c.toJson()); }
        json.put("list", list);
        
        return json;
    }
    
    /** JSON 빌드 (보안 옵션 적용) */
    public JsonObject toJson(String security, String password) {
        JsonObject json = toJson();
        json.put("security", security);
        
        JsonArray list = (JsonArray) json.get("list");
        try {
            if(security.equalsIgnoreCase("AES")) {
                byte[] binaries = list.toJSON().getBytes("UTF-8");
                
                // byte 자리수를 16 배수로 맞추기
                int nowLen = binaries.length;
                int mods   = nowLen % 16;
                
                byte[] buffer = new byte[nowLen + (16-mods)];
                for(int idx=0; idx<buffer.length; idx++) {
                    if(idx < binaries.length) buffer[idx] = binaries[idx];
                    else                      buffer[idx] = (byte) ' ';
                }
                binaries = buffer;
                buffer = null;
                
                // 버전
                int ver = getBackupVersion();
                json.put("backupver", new Integer(ver));
                
                // 비밀번호 해싱
                String padded = getHashLeftSalt(ver) + password + getHashRightSalt(ver);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] pwdigested = digest.digest(padded.getBytes("UTF-8"));
                String pwencoded  = HexUtil.encode(pwdigested).substring(0, 32);
                pwdigested = null;
                
                // 암호화
                SecretKeySpec scKeySpec = new SecretKeySpec(pwencoded.getBytes("UTF-8"), "AES");
                IvParameterSpec iv = new IvParameterSpec(getIv(ver).getBytes("UTF-8"));
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, scKeySpec, iv);
                byte[] ciphered = cipher.doFinal(binaries);
                list = null;
                binaries = null;
                
                String hexStr = HexUtil.encode(ciphered);
                ciphered = null;
                cipher = null;
                
                json.put("list", hexStr);
            }
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
        return json;
    }

    public List<Colony> getColonies() {
        return colonies;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setColonies(List<Colony> colonies) {
        this.colonies = colonies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
    
    public Date getCreatedDate() {
        return new Date(getCreated());
    }
    
    public void setCreated(Date created) {
        this.created = created.getTime();
    }
    
    public int getBackupVersion() { return 1; }
    
    public String getHashLeftSalt(int version) {
        if(version == 1) return "ABS";
        return "";
    }
    
    public String getHashRightSalt(int version) {
        if(version == 1) return "XYH";
        return "";
    }
    
    public String getIv(int version) {
        if(version == 1) return "IVS4567890123456";
        return "IV34567890123456";
    }
}
