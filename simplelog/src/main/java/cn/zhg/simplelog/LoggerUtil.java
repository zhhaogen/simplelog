package cn.zhg.simplelog;

/**
 * 接口工具,加入这个接口就可以直接使用log了
 */
public interface LoggerUtil {
    Logger log=Logger.getLogger(LoggerUtil.class.getName());
}
