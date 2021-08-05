/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

public class ParserException extends Exception {

    private int errorLine;
    private int errorPos;

    public ParserException(int errorLine, int errorPos) {
        super("Parse error at line: " + errorLine + " pos: " + errorPos);

        this.errorLine = errorLine;
        this.errorPos = errorPos;
    }

    public int getErrorLine() {
        return errorLine;
    }

    public int getErrorPos() {
        return errorPos;
    }
    
}
