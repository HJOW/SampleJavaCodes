package org.duckdns.hjow.samples.ddlutil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** JSON 데이터를 읽어 테이블 CREATE DDL문을 생성하는 Util. 이 객체 하나에 테이블 정보 하나를 담는다. */
public class DDLUtil {
    protected String tableName, primaryKeyName;
    protected List<Column> columns            = new ArrayList<Column>();         
    protected List<String> primaryKeyColumns  = new ArrayList<String>();
    
    /** 빈 객체를 만든다. 본격적으로 사용하려면 read(json) 메소드를 한번은 이용해야 한다. */
    public DDLUtil() {
        
    }
    
    /**
     * <pre> 
     * JSON 문자열로부터 테이블 정보를 읽어 새 객체를 만든다. 
     * 
     *    {
     *        "name"    : "테이블명"
     *      , "columns" : [
     *             {
     *                 "name"     : "컬럼명"
     *               , "types"    : "타입" 
     *               , "sizes"    : "크기"
     *               , "defaults" : "기본값"
     *             }
     *           , {
     *                 "name"     : "컬럼명"
     *               , "types"    : "타입"
     *               , "sizes"    : "크기" 
     *               , "defaults" : "기본값"
     *             }
     *           , ...
     *        ]
     *      , "primaryKey" : "PK제약조건명"
     *      , "primaryKeyColumns" : [ "PK컬럼명", "PK컬럼명", ... ]
     *    }
     * 
     * 
     * columns 내부 원소에 대한 설명
     * 
     *     name 에는 컬럼명을 넣는다.
     *     
     *     types 에는 varchar, numeric, date, datetime, text 5종류만 허용된다.
     *         varchar 의 크기가 4000을 초과한다면 text 를 대신 이용한다.
     *     
     *     sizes 는 선택사항이나, varchar 이나 numeric 사용 시에는 필수이다.
     *         varchar 사용 시에는 정수값을, 
     *         numeric 사용 시에는 정수값 하나 또는 두 개를 콤마로 구분하여 넣는다.
     *            이 값은 DDL 생성 시 괄호 안에 그대로 들어간다.
     *            
     *     defaults 는 선택 사항이다. 
     *         DDL 생성 시 해당 위치에 그대로 들어가므로,
     *         문자열 값을 기본값으로 사용할 경우 홑따옴표까지 포함하여 넣어야 한다. 
     *         
     * Primary Key 제약조건을 사용하는 경우 primaryKey 와 primaryKeyColumns 가 필수이다.
     * 
     *     primaryKey 에는 PK 제약조건 이름을 넣는다.
     *     primaryKeyColumns 에는 배열로, 원소로 컬렴명들을 넣는다.
     *     
     * JSON 문법에 맞지 않을 경우 RuntimeException 이 발생할 수 있다.
     *     실제 예외 객체는 Causes 로 들어간다.
     * 
     * </pre>
     */
    public DDLUtil(String json) {
        this();
        read(json);
    }
    
    /**
     * <pre> 
     * JSON 문자열로부터 테이블 정보를 읽는다. 
     * 
     *    {
     *        "name"    : "테이블명"
     *      , "columns" : [
     *             {
     *                 "name"     : "컬럼명"
     *               , "types"    : "타입" 
     *               , "sizes"    : "크기"
     *               , "defaults" : "기본값"
     *             }
     *           , {
     *                 "name"     : "컬럼명"
     *               , "types"    : "타입"
     *               , "sizes"    : "크기" 
     *               , "defaults" : "기본값"
     *             }
     *           , ...
     *        ]
     *      , "primaryKey" : "PK제약조건명"
     *      , "primaryKeyColumns" : [ "PK컬럼명", "PK컬럼명", ... ]
     *    }
     * 
     * 
     * columns 내부 원소에 대한 설명
     * 
     *     name 에는 컬럼명을 넣는다.
     *     
     *     types 에는 VARCHAR, NUMERIC, DATE, DATETIME, TEXT 5종류만 허용된다.
     *         VARCHAR 의 크기가 4000을 초과한다면 TEXT 를 대신 이용한다.
     *     
     *     sizes 는 선택사항이나, VARCHAR 이나 numeric 사용 시에는 필수이다.
     *         VARCHAR 사용 시에는 정수값을, 
     *         NUMERIC 사용 시에는 정수값 하나 또는 두 개를 콤마로 구분하여 넣는다.
     *            이 값은 DDL 생성 시 괄호 안에 그대로 들어간다.
     *         DATETIME 사용 시, DBMS에 따라서는 TIMESTAMP 로 생성될 수 있다.
     *         TEXT 사용 시, DBMS에 따라서는 CLOB로 생성될 수 있다.
     *            
     *     defaults 는 선택 사항이다. 
     *         DDL 생성 시 해당 위치에 그대로 들어가므로,
     *         문자열 값을 기본값으로 사용할 경우 홑따옴표까지 포함하여 넣어야 한다. 
     *         값으로 SYSDATE 를 넣은 경우 특이 케이스로 판단하며, 해당 컬럼이 date 혹은 datetime 인 경우, "현재날짜" 가 들어가도록 변환된다.
     *         
     * Primary Key 제약조건을 사용하는 경우 primaryKey 와 primaryKeyColumns 가 필수이다.
     * 
     *     primaryKey 에는 PK 제약조건 이름을 넣는다.
     *     primaryKeyColumns 에는 배열로, 원소로 컬렴명들을 넣는다.
     * 
     * JSON 문법에 맞지 않을 경우 RuntimeException 이 발생할 수 있다.
     *     실제 예외 객체는 Causes 로 들어간다.
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public void read(String json) {
        try {
            setTableName(null);
            setColumns(new ArrayList<Column>());
            setPrimaryKeyName(null);
            setPrimaryKeyColumns(new ArrayList<String>());
            
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> roots = mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
            
            setTableName(roots.get("name").toString().trim());
            
            List<Map<String, Object>> columns = (List<Map<String, Object>>) roots.get("columns");
            for(Map<String, Object> col : columns) {
                Column column = new Column();
                
                column.setName(col.get("name").toString());
                column.setTypes(col.get("types").toString());
                if(col.get("sizes"   ) != null) column.setSizes(col.get("sizes").toString());
                if(col.get("defaults") != null) column.setDefaultValue(col.get("defaults").toString());
                
                getColumns().add(column);
            }
            
            if(roots.get("primaryKey") != null)  setPrimaryKeyName(roots.get("primaryKey").toString());
            
            List<String> primaryKeys = (List<String>) roots.get("primaryKeyColumns");
            if(primaryKeys != null) {
                setPrimaryKeyColumns(primaryKeys);
                if(getPrimaryKeyName() == null) setPrimaryKeyName("PK_" + getTableName());
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /** 현재의 테이블 정보를 JSON 문자열로 반환 */
    public String json() {
        Map<String, Object> roots = new HashMap<String, Object>();
        roots.put("name", getTableName());
        
        List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
        for(Column c : getColumns()) {
            Map<String, Object> columnOne = new HashMap<String, Object>();
            columnOne.put("name" , c.getName());
            columnOne.put("types", c.getTypes());
            if(c.getSizes()        != null) columnOne.put("sizes"   , c.getSizes());
            if(c.getDefaultValue() != null) columnOne.put("defaults", c.getDefaultValue());
            columns.add(columnOne);
        }
        roots.put("columns", columns);
        
        if(getPrimaryKeyName() != null) roots.put("primaryKey", getPrimaryKeyName());
        if(getPrimaryKeyColumns() != null) roots.put("primaryKeyColumns", getPrimaryKeyColumns());
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(roots);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /** DDL문을 작성한다. dbmsType 에는 oracle, sqlserver, mariadb, cubrid, h2 가 허용된다. */
    public String ddl(String dbmsType) {
        dbmsType = dbmsType.trim().toLowerCase();
        
        StringBuilder res = new StringBuilder("");
        
        res = res.append(" CREATE TABLE ").append(getTableName()).append("(");
        for(Column c : getColumns()) {
            res = res.append("\n    ").append(c.getName()).append(" ").append(convertColumnType(dbmsType, c.getTypes()));
            
            if(c.getSizes()        != null) res = res.append("(").append(c.getSizes()).append(")");
            if(c.getDefaultValue() != null) {
                res = res.append(" DEFAULT ");
                
                if(c.getDefaultValue().equals("SYSDATE")) {
                    res = res.append(convertColumnDefaultDates(dbmsType, c.getTypes().equalsIgnoreCase("DATETIME")));
                } else { 
                    res = res.append(c.getDefaultValue()); 
                }
            }
        }
        
        res = res.append("\n);");
        return res.toString().trim();
    }
    
    /** DBMS 유형에 맞게 컬럼 타입명 변환 */
    public String convertColumnType(String dbmsName, String originalTypeName) {
        dbmsName         = dbmsName.trim().toLowerCase();
        originalTypeName = originalTypeName.trim().toUpperCase();
        String typeName  = originalTypeName;
        
        if(originalTypeName.equals("VARCHAR")) {
            if(dbmsName.equals("oracle")) {
                typeName = "VARCHAR2";
            }
        }
        
        if(originalTypeName.equals("NUMERIC")) {
            if(dbmsName.equals("oracle")) {
                typeName = "NUMBER";
            }
        }
        
        if(originalTypeName.equals("DATETIME")) {
            if(dbmsName.equals("h2")) {
                typeName = "TIMESTAMP";
            }
        }
        
        return typeName;
    }
    
    /** DBMS 유형에 맞게 "현재 날짜/시각" 함수를 반환 */
    public String convertColumnDefaultDates(String dbmsName, boolean isDatetime) {
        dbmsName = dbmsName.trim().toLowerCase();
        
        if(dbmsName.equals("oracle")) return "SYSDATE";
        
        if(dbmsName.equals("sqlserver")) {
            if(isDatetime) return "SYSDATETIME()";
            else return "GETDATE()";
        }
        
        if(dbmsName.equals("mariadb")) {
            if(isDatetime) return "CURRENT_TIMESTAMP()";
            else return "SYSDATE()";
        }
        
        if(dbmsName.equals("cubrid")) {
            if(isDatetime) return "SYS_DATETIME";
            else return "SYSDATE";
        }
        
        if(dbmsName.equals("h2")) {
            if(isDatetime) return "CURRENT_TIMESTAMP()";
            else return "CURRENT_DATE()";
        }
        
        return "SYSDATE";
    }
    
    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<String> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void setPrimaryKeyColumns(List<String> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }
}

class Column implements Serializable {
    private static final long serialVersionUID = 2169514817919112331L;
    protected String name, types, sizes, defaultValue;
    
    public Column() {
        
    }
    
    public Column(String name, String types, String sizes, String defaultValue) {
        super();
        this.name = name;
        this.types = types;
        this.sizes = sizes;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getTypes() {
        return types;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}