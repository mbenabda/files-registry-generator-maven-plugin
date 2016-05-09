package com.mbenabda.maven.plugins.generators.filesRegistry.naming;

import com.mbenabda.maven.plugins.generators.filesRegistry.naming.JavaNamingConvention;

public class NoopJavaNamingConvention implements JavaNamingConvention {
    public String asMemberIdentifier(String str) {
        return str;
    }

    public String asSimpleClassName(String str) {
        return str;
    }

    public boolean isSimpleClassName(String simpleClassName) {
        return true;
    }

    public boolean isPackageName(String packageName) {
        return true;
    }
}
