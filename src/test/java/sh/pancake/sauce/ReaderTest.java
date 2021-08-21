/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.IDupeResolver;
import sh.pancake.sauce.parser.ProguardParser;

public class ReaderTest {

    // @Test
    public void testClass() throws Exception {
        try (FileInputStream input = new FileInputStream(new File("server.txt"))) {
            String mapping = new String(input.readAllBytes());
            ProguardParser parser = new ProguardParser(IDupeResolver.SUFFIX_TAG_RESOLVER);

            ConversionTable table = parser.parse(mapping);

            FileInputStream fileStream = new FileInputStream(new File("server.jar"));
            byte[] data = fileStream.readAllBytes();
            fileStream.close();

            try(ZipInputStream serverInput = new ZipInputStream(new ByteArrayInputStream(data))) {
                SaucePreprocessor preprocessor = new SaucePreprocessor(serverInput);

                preprocessor.process(table);
            }

            try (
                ZipInputStream serverInput = new ZipInputStream(new ByteArrayInputStream(data));
                ZipOutputStream serverOutput = new ZipOutputStream(new FileOutputStream(new File("server-mapped.jar")))
            ) {
                PancakeSauce sauce = new PancakeSauce(serverInput, table);

                sauce.remapJar(serverOutput, (entry) -> {
                    System.out.println("REMAPPING " + entry.getFromName() + " -> " + entry.getToName());
                });
            }
        }
    }

}
