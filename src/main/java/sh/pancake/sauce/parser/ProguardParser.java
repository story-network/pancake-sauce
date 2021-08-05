/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public class ProguardParser {

    private static ProguardParser instance;

    private static final Pattern CLASS_PATTERN = Pattern.compile("(.+) -> (.+):");

    private static final Pattern FIELD_PATTERN = Pattern.compile("(.+) (.+) -> (.+)");
    private static final Pattern METHOD_PATTERN = Pattern
            .compile("(?:\\d+:){0,2}(.*) (.*)\\((.*)\\)(?::\\d+){0,2} -> (.*)");

    public static ProguardParser getInstance() {
        if (instance == null)
            instance = new ProguardParser();

        return instance;
    }

    private ProguardParser() {

    }

    protected ObfucationMap<String, ClassBlock> collectClasses(String obfuscationMap) throws ParserException {
        ObfucationMap<String, ClassBlock> map = new ObfucationMap<>();

        String[] lines = obfuscationMap.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() < 1 || line.startsWith("#")) continue;

            Matcher match = CLASS_PATTERN.matcher(line);

            if (match.matches()) {
                ClassBlock block = new ClassBlock(i);

                for (; i + 1 < lines.length; i++) {
                    String innerLine = lines[i + 1];
                    if (innerLine.length() < 1 || innerLine.startsWith("#")) continue;
                    else if (!innerLine.startsWith("    ")) break;

                    String memberLine = innerLine.substring(4);

                    block.getInnerLines().add(memberLine);
                }

                map.add(match.group(2), match.group(1), block);
            } else {
                throw new ParserException(i + 1, 0);
            }
        }

        return map;
    }

    public ConversionTable parse(String obfuscationMap) throws ParserException {
        ConversionTable table = new ConversionTable();

        ObfucationMap<String, ClassBlock> classPreMap = collectClasses(obfuscationMap);
        for (String name : classPreMap.originalKeys()) {
            ClassBlock block = classPreMap.get(name);

            ClassVal classVal = new ClassVal();

            List<String> innerLines = block.getInnerLines();
            int innerSize = innerLines.size();
            for (int i = 0; i < innerSize; i++) {
                String line = innerLines.get(i);

                Matcher methodMatch = METHOD_PATTERN.matcher(line);
                if (methodMatch.matches()) {
                    String[] params = methodMatch.group(3).split(",");

                    String[] obfParams = new String[params.length];
                    for (int a = 0; a < obfParams.length; a++) {
                        String param = params[a];

                        String obfuscated = classPreMap.getObfuscated(param);

                        if (obfuscated != null) {
                            obfParams[a] = obfuscated;
                        } else {
                            obfParams[a] = param;
                        }
                    }
                    
                    MethodKey original = new MethodKey(methodMatch.group(2), Lists.newArrayList(params));
                    MethodKey obfKey = new MethodKey(methodMatch.group(4), Lists.newArrayList(obfParams));

                    classVal.getMethodMapping().add(obfKey, original, new MethodVal(methodMatch.group(1)));
                } else {
                    Matcher fieldMatch = FIELD_PATTERN.matcher(line);

                    if (fieldMatch.matches()) {
                        classVal.getFieldMapping().add(fieldMatch.group(3), fieldMatch.group(2),
                                new FieldVal(fieldMatch.group(1)));
                    } else {
                        throw new ParserException(i + block.getStartLine(), 0);
                    }
                }
            }

            table.getClassMapping().add(classPreMap.getObfuscated(name), name, classVal);
        }

        return table;
    }

}
