package cn.zhg.simplelog;

public enum Level implements ILevel {
    TRACE,DEBUG,INFO,WARN,ERROR;
    public int level() {
        return ordinal();
    }
}
