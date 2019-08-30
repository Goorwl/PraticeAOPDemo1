package com.goorwl.hugo;

import android.util.ArrayMap;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

@Aspect
public class MyClick {

    private static final String POINTCUT_METHOD = "execution(@com.goorwl.hugo.DoubleClick * *(..))";

    // 临时存储点击时间
    private static ArrayMap<String, Long> tempArray = new ArrayMap<>();

    @Pointcut(POINTCUT_METHOD)
    public void method() {
    }

    @Around("method()")
    public Object AntiDoubleClick(ProceedingJoinPoint joinPoint) throws Throwable {
        CodeSignature signature         = (CodeSignature) joinPoint.getSignature();
        String        declaringTypeName = signature.getDeclaringTypeName();
        long          nanoTime          = System.currentTimeMillis();

        Log.e("TAG====", "AntiDoubleClick0: " + declaringTypeName);

        Long aLong = tempArray.get(declaringTypeName);
        if (aLong != null) {
            if (nanoTime - aLong < 1000) {
                return null;
            }
        }
        tempArray.put(declaringTypeName, nanoTime);
        return joinPoint.proceed();
    }
}
