/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce;

import org.junit.jupiter.api.Test;

import sh.pancake.sauce.parser.ClassVal;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.FieldVal;
import sh.pancake.sauce.parser.MethodKey;
import sh.pancake.sauce.parser.MethodVal;
import sh.pancake.sauce.parser.ProguardParser;

public class ParserTest {
    
    @Test
    public void testParser() throws Exception {
        String mapping = "";

        ProguardParser parser = ProguardParser.getInstance();

        ConversionTable table = parser.parse(mapping);

        for (String original : table.getClassMapping().originalKeys()) {
            ClassVal classVal = table.getClassMapping().get(original);
            String obf = table.getClassMapping().getObfuscated(original);

            System.out.println("CLASS: " + obf + " -> " + original);

            for (String originalField : classVal.getFieldMapping().originalKeys()) {
                FieldVal fieldVal = classVal.getFieldMapping().get(originalField);
                String fieldObf = classVal.getFieldMapping().getObfuscated(originalField);

                System.out.println("    FIELD: " + fieldVal.getType() + " " + fieldObf + " -> " + originalField);
            }

            for (MethodKey originalMethod : classVal.getMethodMapping().originalKeys()) {
                MethodVal methodVal = classVal.getMethodMapping().get(originalMethod);
                MethodKey fieldObf = classVal.getMethodMapping().getObfuscated(originalMethod);

                System.out.println("    METHOD: " + methodVal.getReturnType() + " " + fieldObf + " -> " + originalMethod);
            }
        }
    }

}
