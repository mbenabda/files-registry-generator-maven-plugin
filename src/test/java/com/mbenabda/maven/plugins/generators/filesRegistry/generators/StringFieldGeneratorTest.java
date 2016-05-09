package com.mbenabda.maven.plugins.generators.filesRegistry.generators;

import com.mbenabda.maven.plugins.generators.filesRegistry.fieldValue.FieldValueMaker;
import com.mbenabda.maven.plugins.generators.filesRegistry.naming.NoopJavaNamingConvention;
import com.squareup.javapoet.FieldSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class StringFieldGeneratorTest {

    @Test
    public void should_create_field() {
        Path source = Paths.get("aFileName");

        assertEquals(
            FieldSpec
                .builder(String.class, source.toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", "value")
                .build(),
            testableGenerator().createField(source)
        );
    }

    private StringFieldGenerator testableGenerator() {
        return new StringFieldGenerator(
            new NoopJavaNamingConvention(),
            new FieldValueMaker<String>() {
                public String asFieldValue(Path path) {
                    return "value";
                }
            }
        );
    }

}