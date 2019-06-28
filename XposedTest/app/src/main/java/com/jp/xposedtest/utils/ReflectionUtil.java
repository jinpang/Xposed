package com.jp.xposedtest.utils;

import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XposedBridge;

public class ReflectionUtil {

    public static void printConstructors(Class c1, boolean lineBreak) {
        Constructor[] constructors = c1.getDeclaredConstructors();
        StringBuffer stringBuffer = new StringBuffer(" ");
        for (Constructor c : constructors) {
            String name = c.getName();
            String modifiers = Modifier.toString(c.getModifiers());
            if (modifiers.length() > 0) {
                stringBuffer.append(modifiers + " ");
            }
            stringBuffer.append(name + "(");
            Class[] paramTypes = c.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(paramTypes[i].getName());
            }
            if (lineBreak){
                stringBuffer.append(");\n");
            }else {
                stringBuffer.append(");");
            }
        }
        XposedBridge.log(stringBuffer.toString());
    }

    public static void printMethods(Class c1, boolean lineBreak) {
        Method[] methods = c1.getDeclaredMethods();
        StringBuffer stringBuffer = new StringBuffer(" ");
        for (Method m : methods) {
            Class retType = m.getReturnType();
            String name = m.getName();
            String modifiers = Modifier.toString(m.getModifiers());
            if (modifiers.length() > 0) {
                stringBuffer.append(modifiers + " ");
            }
            stringBuffer.append(retType.getName() + " " + name + "(");
            Class[] paraTypes = m.getParameterTypes();
            for (int i = 0; i < paraTypes.length; i++) {
                if (i > 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(paraTypes[i].getName());
            }
            if (lineBreak) {
                stringBuffer.append(");\n");
            }else {
                stringBuffer.append(");");
            }
        }
        XposedBridge.log(stringBuffer.toString());
    }

    public static void printFields(Class c1, boolean lineBreak) {
        Field[] fields = c1.getDeclaredFields();
        StringBuffer stringBuffer = new StringBuffer(" ");
        for (Field f : fields) {
            Class type = f.getType();
            String name = f.getName();
            String modifiers = Modifier.toString(f.getModifiers());
            if (modifiers.length() > 0) {
                if (lineBreak) {
                    stringBuffer.append(type.getName() + " " + name + ";\n");
                }else {
                    stringBuffer.append(type.getName() + " " + name + ";");
                }
            }
        }
        XposedBridge.log(stringBuffer.toString());
    }

    public static void printParentViewGroup(ViewGroup viewGroup, int parentLevel){
        if (viewGroup != null){
            String headLine = getParentViewGroup(viewGroup, parentLevel);
            XposedBridge.log("printParentViewGroup:" + headLine);
            XposedBridge.log(headLine);
        }
    }

    public static String getParentViewGroup(ViewGroup viewGroup, int parentLevel){
        XposedBridge.log("getParentViewGroup parentLevel:" + parentLevel);
        if (viewGroup != null){
            if (parentLevel > 0 && viewGroup.getParent() != null && viewGroup.getParent() instanceof ViewGroup){
                return getParentViewGroup((ViewGroup)viewGroup.getParent(), parentLevel);
            }else {
                XposedBridge.log("getParentViewGroup :" + viewGroup.getClass().getName());
                return viewGroup.getClass().getName();
            }
        }
        return null;
    }
}
