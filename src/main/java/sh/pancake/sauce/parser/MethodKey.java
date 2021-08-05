/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

import java.util.ArrayList;
import java.util.Objects;

public class MethodKey {
    
    private String name;

    private ArrayList<String> params;

    public MethodKey(String name, ArrayList<String> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;

        if (obj != null && obj instanceof MethodKey) {
            MethodKey key = (MethodKey) obj;
            
            return this.name.equals(key.name) && this.params.equals(params);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.params);
    }

    @Override
    public String toString() {
        return name + "(" + String.join(",", this.params.toArray(new String[0])) + ")";
    }

}
