package com.mbenabda.maven.plugins.generators.filesRegistry.naming;

import org.apache.commons.lang3.StringUtils;

public class StandardJavaNamingConvention implements JavaNamingConvention {

    public String asMemberIdentifier(String str) {
        return asIdentifier(str);
    }

    public String asSimpleClassName(String str) {
        String identifier = asIdentifier(str);
        String firstChar = "" + identifier.charAt(0);
        return firstChar.toUpperCase() + identifier.substring(1, identifier.length());
    }

    public boolean isSimpleClassName(String simpleClassName) {
        if(StringUtils.isBlank(simpleClassName)) {
            return false;
        }

        if(Character.isJavaIdentifierStart(simpleClassName.charAt(0))) {
            String otherCharacters = simpleClassName.substring(1, simpleClassName.length());
            for(char c : otherCharacters.toCharArray()) {
                if(!Character.isJavaIdentifierPart(c)) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    public boolean isPackageName(String packageName) {
        return true;
    }

    private static String asIdentifier(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isJavaIdentifierStart(str.charAt(0)) || i > 0 && Character.isJavaIdentifierPart(str.charAt(i)))
                sb.append(str.charAt(i));
            else
                sb.append((int)str.charAt(i));
        }
        return sb.toString();
    }
}
