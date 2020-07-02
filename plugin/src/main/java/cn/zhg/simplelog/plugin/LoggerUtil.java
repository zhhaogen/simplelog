package cn.zhg.simplelog.plugin;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface LoggerUtil {
        Logger log= LoggerFactory.getLogger(LoggerUtil.class.getName());
//    ILogger log = new ILogger() {
//        @Override
//        public void debug(String msg) {
//            System.out.println(msg);
//        }
//    };
//
//    public static interface ILogger {
//         void debug(String msg);
//    }
}
