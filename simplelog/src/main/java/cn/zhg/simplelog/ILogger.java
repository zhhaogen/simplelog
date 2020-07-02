package cn.zhg.simplelog;

/**
 * 日志接口,api文档
 */
public interface ILogger {
    /**
     * 跟踪消息
     * @param msg
     */
   default void trace(String msg){
       tracep(null,null,null,-1,msg);
   }

    /**
     * 调试消息
     * @param msg
     */
   default void debug(String msg){
       debugp(null,null,null,-1,msg);
   }
    /**
     * 正常消息
     * @param msg
     */
   default void info(String msg){
       infop(null,null,null,-1,msg);
   }
    /**
     * 警告消息
     * @param msg
     */
  default   void warn(String msg){
      warnp(null,null,null,-1,msg);
  }
    /**
     * 异常消息
     * @param msg
     */
   default void error(String msg){
       errorp(null,null,null,-1,msg);
   }

    /**
     * 异常消息
     * @param msg
     * @param throwable 异常
     */
   default void error(String msg,Throwable throwable){
       errorp(null,null,null,-1,msg,throwable);
   }

    /**
     * 跟踪消息,带源码位置信息,p指position的意思
     * @param fileName 源码文件名
     * @param className 所在类名
     * @param methodName 所在方法名
     * @param lineNumber 所在行号
     * @param msg
     */
    void tracep(String fileName,String className,String methodName,int lineNumber,String msg);
    /**
     * 调试消息
     * @param fileName 源码文件名
     * @param className 所在类名
     * @param methodName 所在方法名
     * @param lineNumber 所在行号
     * @param msg
     */
    void debugp(String fileName,String className,String methodName,int lineNumber,String msg);
    /**
     * 正常消息
     * @param fileName 源码文件名
     * @param className 所在类名
     * @param methodName 所在方法名
     * @param lineNumber 所在行号
     * @param msg
     */
    void infop(String fileName,String className,String methodName,int lineNumber,String msg);
    /**
     * 警告消息
     * @param fileName 源码文件名
     * @param className 所在类名
     * @param methodName 所在方法名
     * @param lineNumber 所在行号
     * @param msg
     */
    void warnp(String fileName,String className,String methodName,int lineNumber,String msg);
    /**
     * 异常消息
     * @param fileName 源码文件名
     * @param className 所在类名
     * @param methodName 所在方法名
     * @param lineNumber 所在行号
     * @param msg
     */
    default void errorp(String fileName,String className,String methodName,int lineNumber,String msg){
       errorp(fileName, className, methodName, lineNumber, msg,null);
    }

    /**
     * 异常消息
     * @param fileName 源码文件名
     * @param className 所在类名
     * @param methodName 所在方法名
     * @param lineNumber 所在行号
     * @param msg
     * @param throwable 异常
     */
    void errorp(String fileName,String className,String methodName,int lineNumber,String msg,Throwable throwable);
}
