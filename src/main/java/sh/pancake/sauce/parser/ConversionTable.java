/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

public class ConversionTable {

    private ObfucationMap<String, ClassVal> classMapping;

    public ConversionTable() {
        this.classMapping = new ObfucationMap<>();
    }

    public ConversionTable(ConversionTable other) {
        this.classMapping = new ObfucationMap<>();

        classMapping.getInnerMap().add(other.classMapping);
    }

    public ObfucationMap<String, ClassVal> getClassMapping() {
        return classMapping;
    }

}
