package cn.zhg.simplelog.plugin;

import cn.zhg.simplelog.generator.SimpleLogGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;

import javax.inject.Inject;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


public class GeneratorTask extends DefaultTask implements LoggerUtil {
    private JavaCompile javaCompile;

    @Inject
    public GeneratorTask(JavaCompile javaCompile) {
        super();
        this.setGroup("simplelog");
        this.javaCompile = javaCompile;
        this.setDescription("在" + javaCompile.getName() + "之前生成源码");
    }

    @TaskAction
    public void run() {
       log.debug("执行任务:" + getName()+",defaultCharset="+ Charset.defaultCharset());
        Project project = getProject();
        File genRootDir = new File(project.getBuildDir(), "simplelog" + File.separator + javaCompile.getName());
        if (!genRootDir.exists()) {
            genRootDir.mkdirs();
        }
        SimpleLogGenerator generator = new SimpleLogGenerator();
        List<File> newSourceFiles = new ArrayList<>();
        FileTree sources = javaCompile.getSource();
//       log.debug("源码文件个数:" + sources.getFiles().size());

        String _charset = javaCompile.getOptions().getEncoding();
        if(_charset==null){
            _charset=Charset.defaultCharset().name();
        }
        final String charset=_charset;
        sources.visit(new FileVisitor() {
            public void visitDir(FileVisitDetails dirDetails) {
                File genDir = dirDetails.getRelativePath().getFile(genRootDir);
//               log.debug("genDir=" + genDir);
                genDir.mkdir();
            }

            public void visitFile(FileVisitDetails fileDetails) {
                File genFile = fileDetails.getRelativePath().getFile(genRootDir);
               log.debug("genFile=" + genFile);
                if (fileDetails.getName().endsWith(".java")) {//java文件
                    try(PrintWriter os=new PrintWriter(genFile,charset)) {
                        String genSource=generator.genFile(fileDetails.getFile());
                        os.write(genSource);
                        os.flush();
                        newSourceFiles.add(genFile);
                    } catch (Exception e) {
                        newSourceFiles.add(fileDetails.getFile());
                    }
                }else{
                    newSourceFiles.add(fileDetails.getFile());
                }
            }
        });

        //修改为新的源码路径
        log.debug("修改源码:"+newSourceFiles);
        javaCompile.setSource(newSourceFiles);
    }
}
