/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sh.pancake.sauce.type.TypeUtil;
import sh.pancake.sauce.util.ConvertUtil;

public class ProguardParser {

    private static final Pattern CLASS_PATTERN = Pattern.compile("(.+) -> (.+):");

    private static final Pattern FIELD_PATTERN = Pattern.compile("(.+) (.+) -> (.+)");
    private static final Pattern METHOD_PATTERN = Pattern
            .compile("(?:\\d+:){0,2}(.*) (.*)\\((.*)\\)(?::\\d+){0,2} -> (.*)");

    private IDupeResolver duplicateResolver;

    public ProguardParser(IDupeResolver duplicateHandler) {
        this.duplicateResolver = duplicateHandler;
    }

    protected ObfucationMap<String, ClassBlock> collectClasses(String obfuscationMap) throws ParserException {
        ObfucationMap<String, ClassBlock> map = new ObfucationMap<>();

        String[] lines = obfuscationMap.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() < 1 || line.startsWith("#"))
                continue;

            Matcher match = CLASS_PATTERN.matcher(line);

            if (match.matches()) {
                ClassBlock block = new ClassBlock(i);

                for (; i + 1 < lines.length; i++) {
                    String innerLine = lines[i + 1];
                    if (innerLine.length() < 1 || innerLine.startsWith("#"))
                        continue;
                    else if (!innerLine.startsWith("    "))
                        break;

                    String memberLine = innerLine.substring(4);

                    block.getInnerLines().add(memberLine);
                }

                map.add(TypeUtil.classNameToPath(match.group(2)), TypeUtil.classNameToPath(match.group(1)), block);
            } else {
                throw new ParserException(i + 1, 0, "Invalid mapping format.");
            }
        }

        return map;
    }

    public ConversionTable parse(String obfuscationMap) throws ParserException {
        ConversionTable table = new ConversionTable();

        ObfucationMap<String, ClassBlock> classPreMap = collectClasses(obfuscationMap);
        for (String name : classPreMap.originalKeys()) {
            // System.out.println("MAPPING " + name);
            ClassBlock block = classPreMap.get(name);

            ClassVal classVal = new ClassVal();

            List<String> innerLines = block.getInnerLines();
            int innerSize = innerLines.size();
            for (int i = 0; i < innerSize; i++) {
                String line = innerLines.get(i);

                Matcher methodMatch = METHOD_PATTERN.matcher(line);
                if (methodMatch.matches()) {
                    String[] args = methodMatch.group(3).split(",");

                    StringBuilder originalBuilder = new StringBuilder();
                    StringBuilder obfBuilder = new StringBuilder();

                    for (String arg : args) {
                        if (arg.isEmpty())
                            continue;

                        String desc = TypeUtil.toASMDesc(arg);

                        originalBuilder.append(desc);

                        obfBuilder.append(ConvertUtil.convertType(classPreMap, desc));
                    }

                    String originalReturnType = TypeUtil.toASMDesc(methodMatch.group(1));

                    MethodKey original = new MethodKey(methodMatch.group(2), originalBuilder.toString(),
                            originalReturnType);

                    MethodKey obfKey = new MethodKey(methodMatch.group(4), obfBuilder.toString(),
                            ConvertUtil.convertType(classPreMap, originalReturnType));

                    MethodVal methodVal = new MethodVal();

                    if (!classVal.getMethodMapping().add(obfKey, original, methodVal)) {
                        String altName = duplicateResolver.resolve(obfKey, original, methodVal);

                        original = new MethodKey(altName, original.getArgs(), original.getReturnType());

                        if (!classVal.getMethodMapping().add(obfKey, original, methodVal)) {
                            throw new ParserException(i + block.getStartLine(), 0,
                                    "Method mapping duplicate and cannot be resolved.");
                        }
                    }
                } else {
                    Matcher fieldMatch = FIELD_PATTERN.matcher(line);

                    if (fieldMatch.matches()) {
                        classVal.getFieldMapping().add(fieldMatch.group(3), fieldMatch.group(2),
                                new FieldVal(TypeUtil.toASMDesc(fieldMatch.group(1))));
                    } else {
                        throw new ParserException(i + block.getStartLine(), 0, "Invalid mapping format.");
                    }
                }
            }

            table.getClassMapping().add(classPreMap.getObfuscated(name), name, classVal);
        }

        return table;
    }

}
