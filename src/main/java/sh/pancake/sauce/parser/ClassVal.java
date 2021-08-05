/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

public class ClassVal {
    
    private ObfucationMap<String, FieldVal> fieldMapping;
    private ObfucationMap<MethodKey, MethodVal> methodMapping;

    public ClassVal() {
        this.fieldMapping = new ObfucationMap<>();
        this.methodMapping = new ObfucationMap<>();
    }

    public ObfucationMap<String, FieldVal> getFieldMapping() {
        return fieldMapping;
    }

    public ObfucationMap<MethodKey, MethodVal> getMethodMapping() {
        return methodMapping;
    }

}
