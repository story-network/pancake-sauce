/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

import java.util.Objects;

public class MethodKey {
    
    private String name;

    private String args;

    private String returnType;

    public MethodKey(String name, String args, String returnType) {
        this.name = name;
        this.args = args;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public String getArgs() {
        return args;
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;

        if (obj != null && obj instanceof MethodKey) {
            MethodKey key = (MethodKey) obj;
            
            return name.equals(key.name) && args.equals(key.args) && returnType.equals(key.returnType);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.args, this.returnType);
    }

    @Override
    public String toString() {
        return name + "(" + args + ")" + returnType;
    }

}
