package org.cjl.summer.mybatis.cache;

/**
 * @Title: CacheKey
 * @Package: org.cjl.summer.mybatis.cache
 * @Description: mock the mybatis cacheKey
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class CacheKey {

    private static final int DEFAULT_HASHCODE = 17;
    private static final int DEFAULT_MULTIPLIER =37;

    private int hashCode;

    private int count;

    private int multiplier;

    public CacheKey() {
        this.hashCode = DEFAULT_HASHCODE;
        this.count = 0;
        this.multiplier = DEFAULT_MULTIPLIER;
    }

    public int getCode(){return this.hashCode;}

    public void update(Object obj){
        int baseHashCode = obj == null ? 1 : obj.hashCode();
        count++;
        baseHashCode *= count;
        hashCode = multiplier * hashCode + baseHashCode;
    }
}

