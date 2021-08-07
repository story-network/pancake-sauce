package sh.pancake.sauce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import sh.pancake.sauce.asm.TablePreprocessor;
import sh.pancake.sauce.parser.ConversionTable;

public class SaucePreprocessor {

    public SaucePreprocessor() {
        
    }

    public void process(ZipInputStream input, ConversionTable table) throws IOException {
        List<String> entries = new ArrayList<>();

        for (ZipEntry entry = input.getNextEntry(); entry != null; entry = input.getNextEntry()) {
            if (entry.getName().endsWith(".class") && !entries.contains(entry.getName())) {
                ClassReader reader = new ClassReader(input.readAllBytes());

                reader.accept(new TablePreprocessor(Opcodes.ASM7, table), 0);

                entries.add(entry.getName());
            }
        }
    }

}
