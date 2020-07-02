package cn.zhg.simplelog;

public interface ILevel {
    /**
     * 等级级别,数字表示
     * @return
     */
    int level();

    /**
     * 判断级别是否在这个范围内
     * @param level 不能为null
     * @return
     */
   default boolean match(ILevel level){
       //这里默认级别数字从小到大，包容关系
      return level()<=level.level();
   }
}
