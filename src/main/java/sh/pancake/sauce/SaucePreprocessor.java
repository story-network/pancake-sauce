package sh.pancake.sauce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import sh.pancake.sauce.asm.TablePreprocessor;
import sh.pancake.sauce.parser.ConversionTable;

/**
 * Mapping preprocessor.
 * 
 * Preprocessor must be run before remapping jar file. It is required to analysis inheritance tree and interfaces.
 */
public class SaucePreprocessor {

    private ZipInputStream input;
    private Function<ZipEntry, Boolean> filter;

    public SaucePreprocessor(ZipInputStream input) {
        this(input, null);
    }

    public SaucePreprocessor(ZipInputStream input, Function<ZipEntry, Boolean> filter) {
        this.input = input;
        this.filter = filter;
    }

    /**
     * Preprocess given ConversionTable
     *
     * @param table Table to be preprocessed
     * @throws IOException
     */
    public void process(ConversionTable table) throws IOException {
        List<String> entries = new ArrayList<>();

        for (ZipEntry entry = input.getNextEntry(); entry != null; entry = input.getNextEntry()) {
            if (entry.getName().endsWith(".class") && !entries.contains(entry.getName()) && (filter == null || filter.apply(entry))) {
                ClassReader reader = new ClassReader(input);

                reader.accept(new TablePreprocessor(Opcodes.ASM7, table), 0);

                entries.add(entry.getName());

                input.closeEntry();
            }
        }
    }
}
