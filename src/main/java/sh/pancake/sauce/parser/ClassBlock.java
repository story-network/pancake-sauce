/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

import java.util.ArrayList;
import java.util.List;

public class ClassBlock {

    private int startLine;

    private List<String> innerLines;

    public ClassBlock(int startLine) {
        this.startLine = startLine;

        this.innerLines = new ArrayList<>();
    }

    public int getStartLine() {
        return startLine;
    }

    public List<String> getInnerLines() {
        return innerLines;
    }
    
}
