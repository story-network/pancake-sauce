/*
 * Created on Sat Aug 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.asm;

import org.objectweb.asm.ClassVisitor;

import sh.pancake.sauce.parser.ClassVal;
import sh.pancake.sauce.parser.ConversionTable;

public class TablePreprocessor extends ClassVisitor {

    private ConversionTable table;

    public TablePreprocessor(int api, ConversionTable table) {
        super(api);

        this.table = table;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String deobfName = table.getClassMapping().getDeobfuscated(name);
        if (deobfName != null) {
            ClassVal val = table.getClassMapping().get(deobfName);

            for (String iface : interfaces) {
                String deobfIfaceName = table.getClassMapping().getDeobfuscated(iface);
                if (deobfIfaceName != null) {
                    ClassVal ifaceVal = table.getClassMapping().get(deobfIfaceName);

                    val.getFieldMapping().getInnerMap().add(ifaceVal.getFieldMapping());
                    val.getMethodMapping().getInnerMap().add(ifaceVal.getMethodMapping());
                }
            }

            String deobfSuperName = table.getClassMapping().getDeobfuscated(superName);
            if (deobfSuperName != null) {
                ClassVal superVal = table.getClassMapping().get(deobfSuperName);

                val.getFieldMapping().getInnerMap().add(superVal.getFieldMapping());
                val.getMethodMapping().getInnerMap().add(superVal.getMethodMapping());
            }
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    public ConversionTable getTable() {
        return table;
    }

}
