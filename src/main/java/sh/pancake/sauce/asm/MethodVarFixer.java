/*
 * Created on Sat Aug 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.asm;

import com.google.common.base.CharMatcher;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class MethodVarFixer extends MethodVisitor {

    private int variableCounter;

    public MethodVarFixer(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);

        variableCounter = 0;
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end,
            int index) {
        if (!CharMatcher.ascii().matchesAllOf(name)) {
            name = "var" + variableCounter;
        }

        variableCounter++;

        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

}
