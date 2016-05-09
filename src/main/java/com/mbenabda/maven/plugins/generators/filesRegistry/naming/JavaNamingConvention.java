package com.mbenabda.maven.plugins.generators.filesRegistry.naming;

public interface JavaNamingConvention {
    String asMemberIdentifier(String str);
    String asSimpleClassName(String str);
    boolean isSimpleClassName(String simpleClassName);
    boolean isPackageName(String packageName);
}
