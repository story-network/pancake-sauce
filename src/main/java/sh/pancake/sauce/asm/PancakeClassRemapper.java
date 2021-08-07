/*
 * Created on Sat Aug 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.ClassRemapper;

import sh.pancake.sauce.parser.ConversionTable;

public class PancakeClassRemapper extends ClassRemapper {

    public PancakeClassRemapper(ClassVisitor classVisitor, ConversionTable table) {
        super(classVisitor, new TableRemapper(new ConversionTable(table)));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);

        return new MethodVarFixer(api, visitor);
    }
    
}
