package com.mbenabda.maven.plugins.generators.filesRegistry.fieldValue;

import java.nio.file.Path;

public interface FieldValueMaker<FIELD_VALUE_TYPE> {
    FIELD_VALUE_TYPE asFieldValue(Path file);
}
