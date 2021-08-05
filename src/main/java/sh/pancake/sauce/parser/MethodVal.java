/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

public class MethodVal {
    
    private String returnType;

    public MethodVal(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }

}
