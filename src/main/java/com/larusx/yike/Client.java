package com.larusx.yike;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * 下载器
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws IOException {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();
        
        // Cookie
        String cookie = dotenv.get("COOKIE");
        // bdstoken
        String bdstoken = dotenv.get("BDSTOKEN");
        // 目标路径
        String targetRootPath = dotenv.get("TARGET_ROOT_PATH", "./pictures");
        // 并发下载线程数
        int threadCount = Integer.parseInt(dotenv.get("THREAD_COUNT", "200"));

        log.info("开始下载, 请关注日志。");
        new CursorTraverser(
                threadCount,
                bdstoken,
                cookie,
                targetRootPath
        ).traverse();
    }
}
