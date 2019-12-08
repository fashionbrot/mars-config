package com.gitee.mars.common.util;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;


public class PasswordUtils {
    private static RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    private static String algorithmName = "md5";
    private static int hashIterations = 2;

    public static String getSalt(){
        return randomNumberGenerator.nextBytes().toHex();
    }

    public static String encryptPassword(String password, String salt) {
        return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(salt), hashIterations).toHex();
    }

    public static void main(String[] args) {
        System.out.println(encryptPassword("123456","709dda768e94a3d62680786dac0e72a0"));
    }
}
