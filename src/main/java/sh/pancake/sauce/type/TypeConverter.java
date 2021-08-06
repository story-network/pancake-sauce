/*
 * Created on Fri Aug 06 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.type;

import java.util.ArrayList;
import java.util.List;

public class TypeConverter {

    private List<ASMType> typeList;
    private String objectClass;

    public TypeConverter(List<ASMType> typeList) {
        this(typeList, "");
    }

    public TypeConverter(List<ASMType> typeList, String objectClass) {
        this.typeList = typeList;
        this.objectClass = objectClass;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public List<ASMType> getTypeList() {
        return typeList;
    }

    public String toDescriptor() {
        StringBuilder builder = new StringBuilder();
        for (ASMType type : typeList) {
            builder = type.build(builder);
        }

        if (!objectClass.isEmpty()) {
            builder.append("L");
            builder.append(objectClass);
            builder.append(";");
        }

        return builder.toString();
    }

    public static TypeConverter fromTypeDescriptor(String desc) throws IllegalArgumentException {
        List<ASMType> typeList = new ArrayList<>();

        int len = desc.length();
        for (int i = 0; i < len; i++) {
            char c = desc.charAt(i);
            
            switch (c) {
                case 'V':
                    typeList.add(ASMType.VOID); continue;
                case 'Z':
                    typeList.add(ASMType.BOOLEAN); continue;
                case 'C':
                    typeList.add(ASMType.CHAR); continue;
                case 'B':
                    typeList.add(ASMType.BYTE); continue;
                case 'S':
                    typeList.add(ASMType.SHORT); continue;
                case 'I':
                    typeList.add(ASMType.INT); continue;
                case 'F':
                    typeList.add(ASMType.FLOAT); continue;
                case 'J':
                    typeList.add(ASMType.LONG); continue;
                case 'D':
                    typeList.add(ASMType.DOUBLE); continue;

                case '[':
                    typeList.add(ASMType.ARRAY); continue;

                default: break;
            }

            String sub = desc.substring(i);
            if (sub.startsWith("L") && sub.endsWith(";")) {
                return new TypeConverter(typeList, sub.substring(1, sub.length() - 1));
            }

            throw new IllegalArgumentException("Invalid type descriptor");
        }

        return new TypeConverter(typeList);
    }
    
}
