package com.demo.springboot.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author - Jianghj
 * @since - 2020-01-07 16:46
 * 自定义加密算法
 */
public class CipherUtil {

    /**
     * 使用 Shiro 的 MD5 进行盐值加密</br>
     *
     * @param target         加密目标，如用户密码
     * @param salt           加密盐值
     * @param hashIterations 加密迭代次数
     * @return 加密字符串结果
     * @author - Jhjing
     * @since - 2020.1.7 007
     */
    public static String shiroMd5Encrypt(String target, ByteSource salt, int hashIterations) {
        /*
         *  利用 SimpleHash 来设置 md5 加密
         *      - 第一个参数是算法名称（md5、sha..），第二个是要加密的目标，第三个参数是盐，第四个是散列次数
         */
        SimpleHash hash = new SimpleHash("md5", target, salt, hashIterations);
        return hash.toString();
    }
}
