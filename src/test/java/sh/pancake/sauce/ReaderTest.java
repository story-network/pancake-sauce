/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import sh.pancake.sauce.asm.DeobfClassVisitor;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.IDupeResolver;
import sh.pancake.sauce.parser.ProguardParser;

public class ReaderTest {

    @Test
    public void testClass() throws Exception {
        try (FileInputStream input = new FileInputStream(new File("server.txt"))) {
            String mapping = new String(input.readAllBytes());
            ProguardParser parser = new ProguardParser(IDupeResolver.SUFFIX_TAG_RESOLVER);

            ConversionTable table = parser.parse(mapping);

            ZipFile zip = new ZipFile(new File("server.jar"));
            for (Enumeration<? extends ZipEntry> list = zip.entries(); list.hasMoreElements();) {
                ZipEntry entry = list.nextElement();
                if (!entry.getName().endsWith(".class"))
                    continue;

                System.out.println("Converting " + entry.getName());

                byte[] clazz = zip.getInputStream(entry).readAllBytes();
                ClassReader reader = new ClassReader(clazz);
                ClassWriter writer = new ClassWriter(0);
                ClassVisitor visitor = new DeobfClassVisitor(writer, Opcodes.ASM7, table);
                reader.accept(visitor, 0);
            }
        }
    }

}
