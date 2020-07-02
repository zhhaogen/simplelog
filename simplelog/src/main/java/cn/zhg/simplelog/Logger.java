package cn.zhg.simplelog;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger implements ILogger {

    private Level level;
    private boolean localtion;
    public Logger() {
        level = Level.INFO;
        localtion=false;
    }

    public Level getLevel() {
        return level;
    }

    public boolean isLocaltion() {
        return localtion;
    }

    /**
     * 在没有使用插件情况下是否使用源码定位,即使用StackTraceElement定位
     * @param localtion
     */
    public void setLocaltion(boolean localtion){
        this.localtion=localtion;
    }
    public void setLevel(Level level) {
        this.level = level;
    }

    public static Logger getLogger(String name) {
        return new Logger();
    }
    private StackTraceElement getCurrentStackTrace(){
        if(!localtion){
            return null;
        }
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if(sts.length==0){
            return null;
        }
        return sts[3];
    }
    @Override
    public void trace(String msg) {
        StackTraceElement  st = getCurrentStackTrace();
        if(st==null){
            tracep(null,null,null,-1,msg);
        }else{
            tracep(st.getFileName(),st.getClassName(),st.getMethodName(),st.getLineNumber(),msg);
        }
    }

    @Override
    public void debug(String msg) {
        StackTraceElement  st = getCurrentStackTrace();
        if(st==null){
            debugp(null,null,null,-1,msg);
        }else{
            debugp(st.getFileName(),st.getClassName(),st.getMethodName(),st.getLineNumber(),msg);
        }
    }

    @Override
    public void info(String msg) {
        StackTraceElement  st = getCurrentStackTrace();
        if(st==null){
            infop(null,null,null,-1,msg);
        }else{
            infop(st.getFileName(),st.getClassName(),st.getMethodName(),st.getLineNumber(),msg);
        }
    }

    @Override
    public void warn(String msg) {
        StackTraceElement  st = getCurrentStackTrace();
        if(st==null){
            warnp(null,null,null,-1,msg);
        }else{
            warnp(st.getFileName(),st.getClassName(),st.getMethodName(),st.getLineNumber(),msg);
        }
    }

    @Override
    public void error(String msg) {
        StackTraceElement  st = getCurrentStackTrace();
        if(st==null){
            errorp(null,null,null,-1,msg);
        }else{
            errorp(st.getFileName(),st.getClassName(),st.getMethodName(),st.getLineNumber(),msg);
        }
    }

    @Override
    public void error(String msg, Throwable throwable) {
        StackTraceElement  st = getCurrentStackTrace();
        if(st==null){
            errorp(null,null,null,-1,msg,throwable);
        }else{
            errorp(st.getFileName(),st.getClassName(),st.getMethodName(),st.getLineNumber(),msg,throwable);
        }
    }

    /**
     * 源码位置
     * @param fileName
     * @param className
     * @param methodName
     * @param lineNumber
     * @return
     */
    private String location(String fileName, String className, String methodName, int lineNumber) {
        if(fileName==null){
            return "";
        }
        return "\n"+className+"."+methodName+"("+fileName+":"+lineNumber+")";
    }

    /**
     * 异常输出
     * @param throwable
     * @return
     */
    private String exception(Throwable throwable) {
        if(throwable==null){
            return "";
        }
        try(StringWriter sb=new StringWriter();PrintWriter writer=new PrintWriter(sb)){
            throwable.printStackTrace(writer);
            return "\n"+sb.toString();
        }catch (Exception ignore){
            return "\n"+throwable.getMessage();
        }
    }
    @Override
    public void tracep(String fileName, String className, String methodName, int lineNumber, String msg) {
        if (!level.match(Level.TRACE)) {
            return;
        }
        System.out.println(msg+location(fileName,className,methodName,lineNumber));
    }

    @Override
    public void debugp(String fileName, String className, String methodName, int lineNumber, String msg) {
        if (!level.match(Level.DEBUG)) {
            return;
        }
        System.out.println(msg+location(fileName,className,methodName,lineNumber));
    }
    @Override
    public void infop(String fileName, String className, String methodName, int lineNumber, String msg) {
        if (!level.match(Level.INFO)) {
            return;
        }
        System.out.println(msg+location(fileName,className,methodName,lineNumber));
    }

    @Override
    public void warnp(String fileName, String className, String methodName, int lineNumber, String msg) {
        if (!level.match(Level.WARN)) {
            return;
        }
        System.out.println(msg+location(fileName,className,methodName,lineNumber));
    }

    @Override
    public void errorp(String fileName, String className, String methodName, int lineNumber, String msg, Throwable throwable) {
        if (!level.match(Level.ERROR)) {
            return;
        }
        System.err.println(msg+exception(throwable)+location(fileName,className,methodName,lineNumber));
    }

}
