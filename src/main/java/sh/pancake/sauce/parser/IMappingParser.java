/*
 * Created on Sat Aug 07 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

/**
 * Mapping parser
 */
public interface IMappingParser {
    
    /**
     * Parser raw mapping into ConversionTable
     *
     * @param mapping Raw mapping to parse
     * @return ConversionTable with mapping
     * @throws ParserException
     */
    ConversionTable parse(String mapping) throws ParserException;

}
