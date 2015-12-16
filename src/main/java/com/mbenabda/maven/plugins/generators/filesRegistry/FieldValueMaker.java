package com.mbenabda.maven.plugins.generators.filesRegistry;

import java.nio.file.Path;

public interface FieldValueMaker {
    String asFieldValue(Path file);
}
