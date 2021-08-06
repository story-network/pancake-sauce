/*
 * Created on Fri Aug 06 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.type;

public class ASMType {

    public static final ASMType VOID = new ASMType("V");

    public static final ASMType BOOLEAN = new ASMType("Z");

    public static final ASMType CHAR = new ASMType("C");
    public static final ASMType BYTE = new ASMType("B");

    public static final ASMType SHORT = new ASMType("S");

    public static final ASMType INT = new ASMType("I");
    public static final ASMType FLOAT = new ASMType("F");

    public static final ASMType LONG = new ASMType("J");
    public static final ASMType DOUBLE = new ASMType("D");

    public static final ASMType ARRAY = new ASMType("[");

    private String keyword;
    
    private ASMType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public StringBuilder build(StringBuilder builder) {
        return builder.append(keyword);
    }

}
