package cn.zhg.simplelog.demo;

import cn.zhg.simplelog.LoggerUtil;

/**
 * 一个非常用的例子
 */
public class Demo3 implements LoggerUtil {

    public static void main(String[] args) {
        new Demo3().sayOK().doRun();
    }

    public Demo3 sayOK() {
        class MethodLocalClass {
            public void sayIt() {
                log.info("方法本地类执行方法");
            }
        }
        new MethodLocalClass().sayIt();
        return this;
    }

    public Demo3 doRun() {
        new Thread(() -> {
            log.info("构造函数lambda语句");
        }).start();
        Runnable runnable = () -> {
            log.info("lambda语句");
        };
        runnable.run();
        new Thread().start();
        new Thread() {
            @Override
            public void run() {
                log.info("匿名类1方法");
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                log.info("匿名类2方法");
            }
        }.start();
        return this;
    }
}
