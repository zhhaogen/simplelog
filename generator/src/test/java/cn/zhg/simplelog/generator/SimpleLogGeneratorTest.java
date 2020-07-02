package cn.zhg.simplelog.generator;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class SimpleLogGeneratorTest {

    SimpleLogGenerator generator = new SimpleLogGenerator();
    /**
     * 返回demo项目的Java源文件
     *
     * @param className 类名
     * @return
     */
    File getDemoJavaFile(String className) {
        try {
            File javaFile = new File(".");
            List<String> dirs = new ArrayList<>();
            dirs.addAll(Arrays.asList("demo", "src", "main", "java"));
            String[] classPaths = className.split("\\.");
            for (int i = 0; i < classPaths.length - 1; i++) {
                dirs.add(classPaths[i]);
            }
            dirs.add(classPaths[classPaths.length - 1] + ".java");
            javaFile = new File(javaFile.getCanonicalFile().getParentFile(), String.join(File.separator, dirs));
            return javaFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Test
    public void testGenFile() throws Exception {
        File javaFile = getDemoJavaFile("cn.zhg.simplelog.demo.Demo3");
        System.out.println("javaFile=" + javaFile.getCanonicalFile() + ",exists=" + javaFile.exists());

        String source = generator.genFile(javaFile);
        System.out.println("生成源码:");
        System.out.println(source);
    }
    @Test
    public void testGenFile2() throws Exception {
        File javaFile = getDemoJavaFile("cn.zhg.simplelog.demo.Demo2");
        System.out.println("javaFile=" + javaFile.getCanonicalFile() + ",exists=" + javaFile.exists());

        String source = generator.genFile(javaFile);
        System.out.println("生成源码:");
        System.out.println(source);
    }
    @Test
    public void testGenFile1() throws Exception {
        File javaFile = getDemoJavaFile("cn.zhg.simplelog.demo.Demo1");
        System.out.println("javaFile=" + javaFile.getCanonicalFile() + ",exists=" + javaFile.exists());

        String source = generator.genFile(javaFile);
        System.out.println("生成源码:");
        System.out.println(source);
    }
    @Test
    public void test() throws Exception {
        File javaFile = getDemoJavaFile("cn.zhg.simplelog.demo.MainApplication");
        CompilationUnit javaFileObjec = generator.parseJavaFile(javaFile);
        boolean res = generator.hasImported(javaFileObjec);
        System.out.println("res=" + res);
    }
    @Test
    public void testHasImported() throws Exception {
        File javaFile = getDemoJavaFile("cn.zhg.simplelog.demo.MainApplication");
        CompilationUnit javaFileObjec = generator.parseJavaFile(javaFile);
        boolean res = generator.hasImported(javaFileObjec);
        System.out.println("res=" + res);
    }
}