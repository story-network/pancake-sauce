/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.type;

public class TypeUtil {

    public static String toASMDesc(String javaType) {
        if (javaType.isEmpty()) return "";

        if (javaType.endsWith("[]")) {
            return "[" + toASMDesc(javaType.substring(0, javaType.length() - 2));
        }

        switch (javaType) {
            case "void":
                return "V";
            case "boolean":
                return "Z";
            case "char":
                return "C";
            case "byte":
                return "B";
            case "short":
                return "S";
            case "int":
                return "I";
            case "float":
                return "F";
            case "long":
                return "J";
            case "double":
                return "D";

            default:
                return "L" + TypeUtil.classNameToPath(javaType) + ";";
        }
    }

    public static String classNameToPath(String fullClassName) {
        return fullClassName.replaceAll("\\.", "/");
    }

    public static String classPathToFullName(String classPath) {
        return classPath.replaceAll("/", ".");
    }

}
