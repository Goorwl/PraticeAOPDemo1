package com.goorwl.hugo;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

import java.util.concurrent.TimeUnit;

@Aspect
public class Hugo {

    private static boolean enable = true;

    public static boolean isEnable() {
        return enable;
    }

    public static void setEnable(boolean enable) {
        Hugo.enable = enable;
    }

    private static final String POINTCUT_METHOD   = "execution(@com.goorwl.hugo.DebugLog * *(..))";
    private static final String POINTCUT_CONSCTUT = "execution(@com.goorwl.hugo.DebugLog *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void method() {
    }

    @Pointcut(POINTCUT_CONSCTUT)
    public void constructor() {
    }

    @Around("method() || constructor()")
    public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        enterMethod(joinPoint);
        long   startNanos   = System.nanoTime();
        Object result       = joinPoint.proceed();
        long   stopNanos    = System.nanoTime();
        long   lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
        exitMetho(joinPoint, result, lengthMillis);
        return result;
    }

    private void exitMetho(ProceedingJoinPoint joinPoint, Object result, long lengthMillis) {
        if (!enable) {
            return;
        }
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        Class<?>      cls           = codeSignature.getDeclaringType();
        String        methodName    = codeSignature.getName();
        StringBuilder builder       = new StringBuilder();
        builder.append("<---")
                .append(methodName)
                .append(" : ")
                .append("TIME = [")
                .append(lengthMillis)
                .append(" ms], ")
                .append("RESULT = [")
                .append(result == null ? "null" : result.toString())
                .append("]");
        printLog(cls, builder.toString());
    }

    private void enterMethod(ProceedingJoinPoint joinPoint) {
        if (!enable) {
            return;
        }
        CodeSignature codeSignature  = (CodeSignature) joinPoint.getSignature();
        Class<?>      cls            = codeSignature.getDeclaringType();
        String        methodName     = codeSignature.getName();
        String[]      parameterNames = codeSignature.getParameterNames();
        Object[]      args           = joinPoint.getArgs();

        StringBuffer builder = new StringBuffer();
        builder.append("--->")
                .append(methodName)
                .append(" : ")
                .append("ARGS = [");

        for (int i = 0; i < args.length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i])
                    .append("=")
                    .append(args[i].toString());
        }
        builder.append("]");
        printLog(cls, builder.toString());
    }

    private void printLog(Class<?> cls, String res) {
        Log.e(asTag(cls), res);
    }

    private static String asTag(Class<?> cls) {
        if (cls.isAnonymousClass()) {
            return asTag(cls.getEnclosingClass());
        }
        return cls.getSimpleName();
    }
}
