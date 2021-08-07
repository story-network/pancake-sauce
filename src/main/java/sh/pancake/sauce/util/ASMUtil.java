/*
 * Created on Sat Aug 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.util;

import org.objectweb.asm.Type;

import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.MethodKey;

public class ASMUtil {

    public static MethodKey getMethodKey(String name, Type methodDesc) {
        Type[] paramTypes = methodDesc.getArgumentTypes();
        Type returnType = methodDesc.getReturnType();

        StringBuilder argBuilder = new StringBuilder();
        for (Type paramType : paramTypes) {
            argBuilder.append(paramType.getDescriptor());
        }

        return new MethodKey(name, argBuilder.toString(), returnType.getDescriptor());
    }

    public static String remapMethodDesc(ConversionTable table, Type methodDesc) {
        Type returnType = Type
                .getType(ConvertUtil.toDeobfuscatedType(table.getClassMapping(), methodDesc.getReturnType().getDescriptor()));
        Type[] argTypes = methodDesc.getArgumentTypes();
        for (int i = 0; i < argTypes.length; i++) {
            String rawArgDesc = argTypes[i].getDescriptor();
            String converted = ConvertUtil.toDeobfuscatedType(table.getClassMapping(), rawArgDesc);

            if (!converted.equals(rawArgDesc)) {
                argTypes[i] = Type.getType(converted);
            }
        }

        return Type.getMethodDescriptor(returnType, argTypes);
    }

}
