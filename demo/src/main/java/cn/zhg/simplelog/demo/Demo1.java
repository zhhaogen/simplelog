package cn.zhg.simplelog.demo;


import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import cn.zhg.simplelog.LoggerUtil;

/**
 * 一个最简单的例子
 */
public class Demo1 implements LoggerUtil {
    public static void main(String[] args) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ArrayList<URL> urls = Collections.list(loader.getResources("."));
        System.out.println("当前class加载路径:"+urls);
        log.setLocaltion(false);
        log.trace("跟踪消息");
        log.debug("调试消息");
        log.info("正常消息");
        log.warn("警告消息");
        log.error("异常消息",new NullPointerException("测试异常"));
    }
}
