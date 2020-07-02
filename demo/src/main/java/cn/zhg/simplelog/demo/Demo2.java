package cn.zhg.simplelog.demo;

import cn.zhg.simplelog.Level;
import cn.zhg.simplelog.LoggerUtil;

import java.util.Date;

/**
 * 一个稍微复杂的语句
 */
public class Demo2 implements LoggerUtil {
    /**
     * 构造函数
     */
    public Demo2() {
        this("mmm");
        log.debug("构造函数1");
    }

    public Demo2(String ss) {
        super();
        log.debug("构造函数2");
    }

    /**
     * 混合语句
     *
     * @param msg
     */
    Demo2 test1(String msg) {
        log.debug("测试1" + msg);
        log.info(msg);
        System.out.println(new Date());
        return this;
    }
    Demo2 test2( ) {
       new Inner1().test2();
       new Inner2().test3();
        return this;
    }
    public static void main(String[] args) {
        log.setLevel(Level.DEBUG);
        new Demo2().test1("aaa").test2();
    }
    /**
     * 静态语句
     */
    static {
        log.debug("静态区块,currentTimeMillis=" + System.currentTimeMillis());
    }

    class Inner1 {
        /**
         * 内部子类
         */
        private void test2() {
            log.debug("内部子类方法");
        }
    }

    static class Inner2 {
        /**
         * 内部静态子类
         */
        private void test3() {
            log.debug("内部静态子类方法");
        }
    }
}
