package cn.zhg.simplelog.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * 代码生成器
 */
public class SimpleLogGenerator {
    private final JavaParser javaParser;
    /**
     * 当前文件名位置
     */
    private String visitFileName;
    /**
     * 当前类名位置
     */
    private String visitClassName;
    /**
     * 当前方法名位置
     */
    private String visitMethodName;
    /**
     * 当前是否在访问lambda，方法前缀加入lambda$
     */
    private boolean visitLambda;
    /**
     * lambda方法计数器,方法后缀$0
     */
    private int visitLambdaCount;
    /**
     * 匿名类计数器,类名后缀$1
     */
    private int visitAnonymousClassCount;
    public SimpleLogGenerator() {
        javaParser = new JavaParser();
    }

    /**
     * 解析Java文件
     *
     * @param javaFile
     * @return
     */
    CompilationUnit parseJavaFile(File javaFile) throws Exception {
        ParseResult<CompilationUnit> res = javaParser.parse(javaFile);
        if (!res.isSuccessful()) {
            StringJoiner sb = new StringJoiner("\n");
            List<Problem> problems = res.getProblems();
            for (int i = 0, size = problems.size(); i < size; i++) {
                sb.add("问题" + (i + 1) + ":" + problems.get(i).getMessage());
            }
            throw new RuntimeException(sb.toString());
        }
        Optional<CompilationUnit> ret = res.getResult();
        if (!ret.isPresent()) {
            throw new NullPointerException("解析Java源码结果为null");
        }
        return ret.get();
    }

    /**
     * 输入一个Java文件
     *
     * @param javaFile 必须是java文件
     */
    public String genFile(File javaFile) throws Exception {
        CompilationUnit javaFileObjec = parseJavaFile(javaFile);
        visitFileName = javaFile.getName();
        println("正在进入文件[" + visitFileName + "]");
        if(hasImported(javaFileObjec)){
            javaFileObjec.getTypes().forEach(this::visitType);
        } 
        return javaFileObjec.toString();
    }

    /**
     * 遍历类
     *
     * @param item
     */
    private void visitType(TypeDeclaration<?> item) {
        visitClassName = item.getNameAsString();
        item.getFullyQualifiedName().ifPresent(name -> {
            visitClassName = name;
        });
        println("正在进入类[" + visitClassName + "]");
        item.getMembers().forEach(this::visitBodyDeclaration);
    }

    /**
     * 遍历子类
     *
     * @param item
     */
    private void visitSubType(TypeDeclaration<?> item) {
        String _visitClassName = visitClassName;
        visitClassName = visitClassName + "$" + item.getNameAsString();
        println("正在进入子类[" + visitClassName + "]");
        item.getMembers().forEach(this::visitBodyDeclaration);
        visitClassName = _visitClassName;//结束访问
    }

    /**
     * 访问方法本地类
     *
     * @param item
     */
    private void visitLocalType(LocalClassDeclarationStmt item) {
        visitSubType(item.getClassDeclaration());
    }

    /**
     * 访问创建对象
     * @param item
     */
    private void visitObjectCreation(ObjectCreationExpr item) {
        Optional<NodeList<BodyDeclaration<?>>> abody = item.getAnonymousClassBody();
        if(!abody.isPresent()){
            item.getChildNodes().forEach(this::visitNode);
            return;
        }
        println("正在访问匿名类["+item+"]");
        NodeList<BodyDeclaration<?>> body = abody.get();
        String _visitClassName = visitClassName;
        visitClassName=visitClassName+"$"+visitAnonymousClassCount;
        body.forEach(this::visitAnonymousBodyDeclaration);
        visitClassName = _visitClassName;//结束访问
        visitAnonymousClassCount++;
    }
    /**
     * 访问构造方法、方法、静态块
     *
     * @param item
     */
    private void visitBodyDeclaration(BodyDeclaration item) {
        if (item instanceof InitializerDeclaration) {
            visitInitializer((InitializerDeclaration) item);
            return;
        }
        if (item instanceof MethodDeclaration) {
            visitMethod((MethodDeclaration) item);
            return;
        }
        if (item instanceof ConstructorDeclaration) {
            visitConstructor((ConstructorDeclaration) item);
            return;
        }
        if (item instanceof ClassOrInterfaceDeclaration) {
            visitSubType((ClassOrInterfaceDeclaration) item);
            return;
        }
    }
    /**
     * 访问匿名类中的构造方法、方法、静态块
     *
     * @param item
     */
    private void visitAnonymousBodyDeclaration(BodyDeclaration item) {
        if (item instanceof InitializerDeclaration) {
            visitInitializer((InitializerDeclaration) item);
            return;
        }
        if (item instanceof MethodDeclaration) {
            visitAnonymousMethod((MethodDeclaration) item);
            return;
        }
        if (item instanceof ConstructorDeclaration) {
            visitConstructor((ConstructorDeclaration) item);
            return;
        }
        if (item instanceof ClassOrInterfaceDeclaration) {
            visitSubType((ClassOrInterfaceDeclaration) item);
            return;
        }
    }
    /**
     * 静态区块
     *
     * @param item
     */
    private void visitInitializer(InitializerDeclaration item) {
        visitMethodName = "<cinit>";
        println("正在进入静态区块[" + visitMethodName + "]");
        BlockStmt body = item.getBody();
        body.findAll(MethodCallExpr.class).forEach(this::visitMethodCallExpr);
    }

    /**
     * 遍历构造方法
     *
     * @param item
     */
    private void visitConstructor(ConstructorDeclaration item) {
        visitMethodName = "<init>";
        println("正在进入构造方法[" + visitMethodName + "]");
        BlockStmt body = item.getBody();
        body.findAll(MethodCallExpr.class).forEach(this::visitMethodCallExpr);
    }

    /**
     * 遍历方法
     *
     * @param item
     */
    private void visitMethod(MethodDeclaration item) {
        visitMethodName = item.getNameAsString();
        visitLambdaCount=0;
        visitAnonymousClassCount=1;
        Optional<BlockStmt> bodyOp = item.getBody();
        if (!bodyOp.isPresent()) {
            return;
        }
        println("正在进入方法[" + visitMethodName + "]");
        visitNode(bodyOp.get());
    }
    /**
     * 遍历方法(包括匿名类方法)
     *
     * @param item
     */
    private void visitAnonymousMethod(MethodDeclaration item) {
        visitMethodName = item.getNameAsString();
        visitLambdaCount=0;
        Optional<BlockStmt> bodyOp = item.getBody();
        if (!bodyOp.isPresent()) {
            return;
        }
        println("正在进入方法[" + visitMethodName + "]");
        visitNode(bodyOp.get());
    }
    /**
     * 遍历方法里面的通用节点
     *
     * @param item
     */
    private void visitNode(Node item) {
        if(item instanceof Comment){
            return;
        }
        if (item instanceof MethodCallExpr) {
            visitMethodCallExpr((MethodCallExpr) item);
            return;
        }
        if (item instanceof LocalClassDeclarationStmt) {
            visitLocalType((LocalClassDeclarationStmt) item);
            return;
        }
        if (item instanceof LambdaExpr) {
            visitLambda((LambdaExpr) item);
            return;
        }
        if(item instanceof ObjectCreationExpr){
            visitObjectCreation((ObjectCreationExpr) item);
            return;
        }
        println("item="+item+",class="+item.getClass());
        List<Node> nodes = item.getChildNodes();
        if (nodes.isEmpty()) {
            return;
        }
        nodes.forEach(this::visitNode);
    }



    /**
     * 访问lambda语句
     *
     * @param item
     */
    private void visitLambda(LambdaExpr item) {
        visitLambda = true;
        println("正在进入lambada[" + visitClassName + "]");
        item.getChildNodes().forEach(this::visitNode);
        visitLambda = false;//结束访问
    }

    /**
     * 遍历执行语句
     *
     * @param item
     */
    private void visitMethodCallExpr(MethodCallExpr item) {
        Optional<Expression> callScope = item.getScope();
        if (!callScope.isPresent()) {
            return;
        }
        String call = callScope.get().toString();
        if (!"log".equals(call)) {
            item.getChildNodes().forEach(this::visitNode);
            return;
        }
        String methodName = item.getNameAsString();
        if (!isLogMethod(methodName)) {
            item.getChildNodes().forEach(this::visitNode);
            return;
        }
        if (visitFileName == null || visitClassName == null || visitMethodName == null) {
            //no happend
            return;
        }
        int lineNumber = item.getBegin().get().line;
        println("修改前,item=" + item + ",call=" + call + ",methodName=" + methodName + ",lineNumber=" + lineNumber);

        item.setName(new SimpleName(methodName + "p"));
        NodeList<Expression> args = item.getArguments();
        args.addFirst(new IntegerLiteralExpr(lineNumber));
        if (visitLambda) {
            args.addFirst(new StringLiteralExpr("lambda$" + visitMethodName+"$"+visitLambdaCount));
            visitLambdaCount++;
        } else {
            args.addFirst(new StringLiteralExpr(visitMethodName));
        }

        args.addFirst(new StringLiteralExpr(visitClassName));
        args.addFirst(new StringLiteralExpr(visitFileName));
        println("修改后,item=" + item);
    }

    /**
     * 是否导入了包cn.zhg.simplelog.LoggerUtil
     *
     * @param javaFileObjec
     */
    boolean hasImported(CompilationUnit javaFileObjec) {
        NodeList<ImportDeclaration> list = javaFileObjec.getImports();
        for (ImportDeclaration item : list) {
            if (item.getNameAsString().equals("cn.zhg.simplelog.LoggerUtil")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为调试方法,即方法名为:trace,debug,info,warn,error
     *
     * @param methodName 方法名
     * @return
     */
    boolean isLogMethod(String methodName) {
        return "trace".equals(methodName) || "debug".equals(methodName) || "info".equals(methodName) || "warn".equals(methodName) || "error".equals(methodName);
    }

    /**
     * 内部测试
     *
     * @param msg
     */
    private void println(String msg) {
//        System.out.println(msg);
    }
}
