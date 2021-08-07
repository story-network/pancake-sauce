/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import sh.pancake.sauce.asm.PancakeClassRemapper;
import sh.pancake.sauce.parser.ConversionTable;

/**
 * Main remapper class
 */
public class PancakeSauce {

    private ZipInputStream stream;
    private ConversionTable table;

    public PancakeSauce(ZipInputStream stream, ConversionTable table) {
        this.stream = stream;
        this.table = table;
    }

    /**
     * Remap class files in archive stream and emit to @param output
     *
     * @param output Output stream
     * @throws IOException
     */
    public void remapJar(ZipOutputStream output) throws IOException {
        remapJar(output, null);
    }

    /**
     * Remap class files in archive stream and emit to @param output
     *
     * @param output Output stream
     * @param progressCb Progress callback
     * @throws IOException
     */
    public void remapJar(ZipOutputStream output, Consumer<RemapInfo> progressCb) throws IOException {
        List<String> entries = new ArrayList<>();

        for (ZipEntry entry = stream.getNextEntry(); entry != null; entry = stream.getNextEntry()) {
            if (entries.contains(entry.getName())) continue;

            if (entry.getName().endsWith(".class")) {
                byte[] data = stream.readAllBytes();

                RemapInfo info = this.remapClass(data);

                if (progressCb != null) progressCb.accept(info);

                output.putNextEntry(new ZipEntry(info.getToName()));
                
                output.write(info.getData());
                entries.add(info.getToName());
            } else {
                output.putNextEntry(entry);

                stream.transferTo(output);
                entries.add(entry.getName());
            }

            stream.closeEntry();
            output.closeEntry();
        }
    }

    private RemapInfo remapClass(byte[] clazz) {
        ClassReader reader = new ClassReader(clazz);
        ClassWriter writer = new ClassWriter(0);
        PancakeClassRemapper remapper = new PancakeClassRemapper(writer, table);
        // ClassVisitor visitor = new DeobfClassVisitor(writer, Opcodes.ASM7, table);

        reader.accept(remapper, 0);

        byte[] written = writer.toByteArray();

        ClassReader newReader = new ClassReader(written);

        return new RemapInfo(written, reader.getClassName() + ".class", newReader.getClassName() + ".class");
    }

}
