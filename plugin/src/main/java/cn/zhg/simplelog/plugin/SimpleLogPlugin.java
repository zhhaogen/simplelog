package cn.zhg.simplelog.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;

import java.util.ArrayList;
import java.util.List;

public class SimpleLogPlugin implements Plugin<Project> , LoggerUtil {
    @Override
    public void apply(Project project) {
        log.debug("正在启用插件");
        TaskContainer tasks = project.getTasks();
        TaskCollection<JavaCompile> javaCompiles = tasks.withType(JavaCompile.class);
        List<JavaCompile> javaCompileList=new ArrayList<>(javaCompiles);
        for(JavaCompile javaCompile:javaCompileList){
            String genSourcesTaskName = javaCompile.getName() + "GenSources";
            log.debug("注册任务:" + genSourcesTaskName);
            GeneratorTask genSourcesTask = tasks.create(genSourcesTaskName, GeneratorTask.class,javaCompile);
            javaCompile.dependsOn(genSourcesTask);

        }
    }
}
