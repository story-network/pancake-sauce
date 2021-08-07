/*
 * Created on Sat Aug 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

public interface IMappingParser {
    
    ConversionTable parse(String mapping) throws ParserException;

}
