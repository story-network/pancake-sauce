/*
 * Created on Fri Aug 06 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.util;

import sh.pancake.sauce.parser.ObfucationMap;
import sh.pancake.sauce.type.TypeConverter;

public class ConvertUtil {
    
    public static String toDeobfuscatedType(ObfucationMap<String, ? extends Object> mapping, String typeDesc) {
        TypeConverter converter = TypeConverter.fromTypeDescriptor(typeDesc);
        if (converter.getObjectClass().isEmpty())
            return typeDesc;

        String deobfName = mapping.getDeobfuscated(converter.getObjectClass());
        if (deobfName == null)
            return typeDesc;

        converter.setObjectClass(deobfName);

        return converter.toDescriptor();
    }

    public static String toObfuscatedType(ObfucationMap<String, ? extends Object> mapping, String typeDesc) {
        TypeConverter converter = TypeConverter.fromTypeDescriptor(typeDesc);
        if (converter.getObjectClass().isEmpty())
            return typeDesc;

        String deobfName = mapping.getObfuscated(converter.getObjectClass());
        if (deobfName == null)
            return typeDesc;

        converter.setObjectClass(deobfName);

        return converter.toDescriptor();
    }

}
