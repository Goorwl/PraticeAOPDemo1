# 手把手实现自己的HUGO

[![](https://img.shields.io/badge/blog-%E6%A9%99%E5%AD%90-blue.svg)](https://xiaozhuanlan.com/goorwl?rel=goorwl) [![](https://img.shields.io/badge/E--mail-goorwl%40163.com-pink.svg)](https://mailto:goorwl@163.com)

## 文档说明

关于Android开发，很少用到AOP编程思想，因为一般都是使用OOP模块化解析子模块。但是扩展我们的编程思想对于解决某些问题还是大有裨益。关于本篇文档的基础概念，建议参考：[深入理解Android之AOP](https://blog.csdn.net/innost/article/details/49387395)

以下内容默认您已简单了解以上文章所列举概念，不必掌握，了解即可。

## 实现过程

### 准备工作

在任意一个Android 项目内的根目录下的`build.gradle`添加如下内容：

	buildscript {
		...
	    dependencies {
	        ...
	        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'		// 添加沪江ASPECT插件依赖
	    }
	}


哪个module有使用到注解就在哪个module的`build.gradle`文件内添加如下内容：
	
	apply plugin: 'com.android.application'
	apply plugin: 'android-aspectjx'		// 依赖aspect插件

简单理解就是编写注解逻辑的module可以不用添加，但是使用注解的module需要添加。举个栗子：如果这个注解是个Android library，那么这个library的module就不需要添加，但是使用注解的app的module就需要添加以上信息。

在编写注解逻辑的module的`build.gradle`文件内添加如下内容：

	dependencies {
		...
	    implementation 'org.aspectj:aspectjrt:1.8.14'
	}

### 代码实现

在实际过程，我们一般都是会进行分模块开发，因此建议新建一个Android Library进行逻辑实现。

在 Library 中新建一个 Java 类，内容如下：

	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})		// 当前注解使用范围:普通函数、构造函数
	@Retention(RetentionPolicy.CLASS)							// 当前注解保留到class文件但不进入JVM环境
	public @interface Debuglog {
	}

新建一个 Java 类，使用 @Aspect 进行注解。

在类中声明两个 POINTCUT ：

	
	// 注意更新自己的Debuglog的全类名
    private static final String POINTCUT_METHOD   = "execution(@com.goorwl.hugo.DebugLog * *(..))";
    private static final String POINTCUT_CONSCTUT = "execution(@com.goorwl.hugo.DebugLog *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void method() {
    }

    @Pointcut(POINTCUT_CONSCTUT)
    public void constructor() {
    }

至于`POINTCUT`匹配规则，见上文。

新建函数：

    @Around("method() || constructor()")				// 使用上面定义的两个POINTCUT @Around 表示注解函数的运行交由当前函数处理
    public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable{		//  返回值为Object但是Aspect会进行转换为原函数的返回类型
        enterMethod(joinPoint);							// 自建函数，实现原函数运行前的一些操作
        long   startNanos   = System.nanoTime();
        Object result       = joinPoint.proceed();		// 表示执行原函数，注释掉表示原函数内容不执行
        long   stopNanos    = System.nanoTime();
        long   lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
        exitMetho(joinPoint, result, lengthMillis);		// 自建函数，实现原函数运行后的一些操作
        return result;
    }

`enterMethod(joinPoint)`函数部分主要解析如下:

    CodeSignature codeSignature  = (CodeSignature) joinPoint.getSignature();		// 获取注解函数的签名
    Class<?>      cls            = codeSignature.getDeclaringType();				// 获取注解函数所在的类名
    String        methodName     = codeSignature.getName();							// 获取注解函数的函数名
    String[]      parameterNames = codeSignature.getParameterNames();				// 获取注解函数的参数类型
    Object[]      args           = joinPoint.getArgs();								// 获取注解函数的参数值

把需要的数据信息进行整合编辑即可得到我们需要的内容。

### 使用方式

如果注解内容和使用注解不在一个module，需要依赖注解module。

	dependencies {
		...
	    implementation project(path: ':hugo')
	}

新建一个函数：

    @Debuglog
    private int getSum(int a, int b) {
        return a + b;
    }

### 运行结果

![result](https://i.imgur.com/ExiW7W4.png)

### 排错

如果在编译过程出现如下内容：

![exception](https://i.imgur.com/b3Y5Rgc.png)

表明在些POINTCUT的时候参数格式配置异常。

## 小作业

通过以上介绍，我们可以通过Aspect拦截函数，进行一些简单的操作，那么扩展一下，如何通过Aspect实现一个拦截1s重复点击的注解呢？

## 小结

以上是一个简单的AOP demo。但是只是进行了简单的日志输出，并没有出现数据交互，即便这样，已经可以解决好多问题了，发散思维，你会发现Aspect的用处。

关于和Activity数据交互，请期待下一篇文章。


[博客链接](https://xiaozhuanlan.com/topic/8023679145)