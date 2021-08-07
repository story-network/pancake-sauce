package sh.pancake.sauce.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;

import sh.pancake.sauce.parser.ClassVal;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.MethodKey;
import sh.pancake.sauce.util.ASMUtil;

public class TableRemapper extends Remapper {

    private ConversionTable table;

    public TableRemapper(ConversionTable table) {
        this.table = table;
    }

    @Override
    public String map(String internalName) {
        String deobfName = table.getClassMapping().getDeobfuscated(internalName);
        if (deobfName != null) {
            return deobfName;
        }

        return internalName;
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        String deobfOwner = table.getClassMapping().getDeobfuscated(owner);
        if (deobfOwner != null) {
            ClassVal val = table.getClassMapping().get(deobfOwner);

            String deobfName = val.getFieldMapping().getDeobfuscated(name);
            if (deobfName != null) {
                return deobfName;
            }
        }

        return super.mapFieldName(owner, name, descriptor);
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        String deobfOwner = table.getClassMapping().getDeobfuscated(owner);
        if (deobfOwner != null) {
            ClassVal val = table.getClassMapping().get(deobfOwner);

            Type methodDesc = Type.getType(descriptor);

            MethodKey key = ASMUtil.getMethodKey(name, methodDesc);
            MethodKey deobfKey = val.getMethodMapping().getDeobfuscated(key);

            if (deobfKey != null) {
                return deobfKey.getName();
            }
        }

        return super.mapMethodName(owner, name, descriptor);
    }

    @Override
    public String mapInvokeDynamicMethodName(String name, String descriptor) {
        /*Type methodDesc = Type.getMethodType(descriptor);

        if (currentClass != null) {
            MethodKey key = ASMUtil.getMethodKey(name, methodDesc);

            MethodKey deobfKey = currentClass.getMethodMapping().getDeobfuscated(key);
            if (deobfKey != null) {
                return name;
            }
        }*/

        return super.mapInvokeDynamicMethodName(name, descriptor);
    }

}
