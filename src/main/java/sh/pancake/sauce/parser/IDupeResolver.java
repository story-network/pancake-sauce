/*
 * Created on Fri Aug 06 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

@FunctionalInterface
public interface IDupeResolver {

    public static final IDupeResolver SUFFIX_TAG_RESOLVER = (MethodKey obfKey, MethodKey originalKey,
            MethodVal methodVal) -> {
                return originalKey.getName() + "_" + obfKey.getName();
            };

    String resolve(MethodKey obfKey, MethodKey originalKey, MethodVal methodVal);

}
