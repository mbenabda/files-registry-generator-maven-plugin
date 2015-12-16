import com.mbenabda.maven.plugins.generators.filesRegistry.FileHasIncludedSuffixPredicate;
import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.mbenabda.maven.plugins.generators.filesRegistry.generators.javapoet.RegistryClassCodeGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext.iWantToGenerateARegistryClass;
import static org.junit.Assert.assertEquals;

public class RegistryClassCodeGeneratorTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void should_ok() throws Exception {
        tmp.newFile("one.html");
        tmp.newFile("two.html");
        tmp.newFile("some.xml");
        tmp.newFile("three.html");

        RegistryGenerationContext generationContext = generationContext();

        assertEquals(
                join("",
                        "package com.pkg.test;",
                        "import java.lang.String;",
                        "public final class Registry {",
                            "public static final String two = \"two\";",
                            "public static final String three = \"three\";",
                            "public static final String one = \"one\";",
                        "}"
                ),
                inline(new RegistryClassCodeGenerator().generateCode(generationContext))
        );
    }

    @Test
    public void should_ok_nested() throws Exception {
        createFileInFolder(
                tmp.newFolder("subDir"),
                "aFileInSubDir.html"
        );

        assertEquals(
                join("",
                        "package com.pkg.test;",
                        "import java.lang.String;",
                        "public final class Registry {",
                            "public static final class SubDir {",
                                "public static final String aFileInSubDir = \"subDir/aFileInSubDir\";",
                            "}",
                        "}"
                ),
                inline(new RegistryClassCodeGenerator().generateCode(generationContext()))
        );
    }

    private RegistryGenerationContext generationContext() {
        return iWantToGenerateARegistryClass()
                .called("Registry")
                .inPackage("com.pkg.test")
                .fromTheFilesUnder(tmp.getRoot().toPath())
                .thatMatch(new FileHasIncludedSuffixPredicate(".html"))
                .please();
    }

    private File createFileInFolder(File dir, String fileName) throws IOException {
        File file = dir.toPath().resolve(fileName).toFile();
        new FileWriter(file).write("");
        return file;
    }

    private String join(String separator, String... words) {
        int i = 0;
        StringBuffer acc = new StringBuffer();
        for(String word : words) {
            acc.append(word);

            if(i < words.length) {
                acc.append(separator);
            }
            i++;
        }
        return acc.toString();
    }

    private String inline(String javaCode) {
        return javaCode.replaceAll("\n", "").replaceAll("\\s{2,}", "");
    }

}
