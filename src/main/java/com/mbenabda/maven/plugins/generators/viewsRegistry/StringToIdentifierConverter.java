package com.mbenabda.maven.plugins.generators.viewsRegistry;

public class StringToIdentifierConverter {
    public String asFieldIdentifier(String str) {
        return asIdentifier(str);
    }

    public String asClassIdentifier(String str) {
        String identifier = asIdentifier(str);
        String firstChar = "" + identifier.charAt(0);
        return firstChar.toUpperCase() + identifier.substring(1, identifier.length());
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
