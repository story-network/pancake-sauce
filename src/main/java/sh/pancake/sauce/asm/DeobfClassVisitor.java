/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import sh.pancake.sauce.parser.ClassVal;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.MethodKey;
import sh.pancake.sauce.util.ConvertUtil;
import sh.pancake.sauce.util.ASMUtil;

public class DeobfClassVisitor extends ClassVisitor {

    private ConversionTable table;

    private ClassVal currentClass;

    public DeobfClassVisitor(ClassVisitor visitor, int api, ConversionTable table) {
        super(api, visitor);

        this.table = table;
        this.currentClass = null;
    }

    protected void transformClasses(String[] classes) {
        if (classes == null)
            return;

        for (int i = 0; i < classes.length; i++) {
            String deobfName = table.getClassMapping().getDeobfuscated(classes[i]);
            if (deobfName == null)
                continue;

            classes[i] = deobfName;
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        transformClasses(interfaces);

        String deobfSuperName = table.getClassMapping().getDeobfuscated(superName);
        if (deobfSuperName != null)
            superName = deobfSuperName;

        String deobfName = table.getClassMapping().getDeobfuscated(name);
        if (deobfName != null) {
            this.currentClass = table.getClassMapping().get(deobfName);

            // System.out.println("Transforming " + name + " -> " + deobfName);

            name = deobfName;
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        transformClasses(exceptions);

        Type methodDesc = Type.getType(descriptor);
        if (currentClass != null) {

            MethodKey key = currentClass.getMethodMapping().getDeobfuscated(ASMUtil.getMethodKey(name, methodDesc));
            if (key != null) {
                name = key.getName();
                descriptor = key.toDescriptor();
            } else {
                descriptor = ASMUtil.remapMethodDesc(table, methodDesc);
            }
        } else {
            descriptor = ASMUtil.remapMethodDesc(table, methodDesc);
        }

        // System.out.println("METHOD " + name + " " + descriptor);

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        descriptor = ConvertUtil.toDeobfuscatedType(table.getClassMapping(), descriptor);

        if (currentClass != null) {
            String deobfName = currentClass.getFieldMapping().getDeobfuscated(name);
            if (deobfName != null) {
                name = deobfName;
            }
        }

        // System.out.println("FIELD " + name + " " + descriptor);

        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        this.currentClass = null;

        super.visitEnd();
    }

}
