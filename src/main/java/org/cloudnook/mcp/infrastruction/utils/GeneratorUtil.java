package org.cloudnook.mcp.infrastruction.utils;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-09 17:13
 * @Description: GeneratorUtil
 */
@Slf4j
public class GeneratorUtil {

    /**
     * 根据 name 和 endpoint 生成唯一的 serverId
     * 使用 SHA-256 哈希算法
     */
    public static String generateServerId(String name, String endpoint) {
        try {
            String source = name + ":" + endpoint;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串，取前 16 位
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 降级方案：使用简单的 hashCode
            log.warn("SHA-256 算法不可用，使用 hashCode 生成 serverId", e);
            return String.valueOf((name + "::" + endpoint).hashCode());
        }
    }

    /**
     * 生成唯一的调用 ID
     */
    public static String generateInvocationId() {
        return "inv-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
    }
}
