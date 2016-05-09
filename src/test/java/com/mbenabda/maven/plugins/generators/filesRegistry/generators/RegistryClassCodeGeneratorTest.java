package com.mbenabda.maven.plugins.generators.filesRegistry.generators;

import com.mbenabda.maven.plugins.generators.filesRegistry.filters.FilenameHasSuffixPredicate;
import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext.iWantToGenerateARegistryClass;
import static org.junit.Assert.assertEquals;

public class RegistryClassCodeGeneratorTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private static final String CLASS_NAME = "RegistryClass";
    private static final String CLASS_PACKAGE = "com.mbenabda.tests.fileRegistry";

    @Test(expected = FileNotFoundException.class)
    public void should_not_generate_the_registry_class_when_source_directory_does_not_exist() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .fromTheFilesUnder(nonExistingDirectory())
            .inPackage(CLASS_PACKAGE)
            .please();

        assertEquals(
            emptyClass(CLASS_PACKAGE, CLASS_NAME),
            testableGenerator().generateCode(context)
        );
    }

    @Test
    public void should_generate_an_empty_class_for_an_empty_directory() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .fromTheFilesUnder(emptyDirectory())
            .inPackage(CLASS_PACKAGE)
            .please();

        assertEquals(
            emptyClass(CLASS_PACKAGE, CLASS_NAME),
            testableGenerator().generateCode(context)
        );
    }

    @Test
    public void should_not_generate_a_nested_class_for_a_directory_without_accepted_files() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .inPackage(CLASS_PACKAGE)
            .fromTheFilesUnder(aDirectoryWithFiles("images", new String[]{ "anImage.png" }))
            .thatMatch(new FilenameHasSuffixPredicate(".txt"))
            .please();

        assertEquals(
            emptyClass(CLASS_PACKAGE, CLASS_NAME),
            testableGenerator().generateCode(context)
        );
    }

    @Test
    public void should_generate_fields_for_accepted_files() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .inPackage(CLASS_PACKAGE)
            .fromTheFilesUnder(aDirectoryWithFiles("directory", new String[]{"aTextFile.txt", "anImage.jpg", "anotherTextFile.txt"}))
            .thatMatch(new FilenameHasSuffixPredicate(".txt"))
            .please();

        assertEquals(
            StringUtils.join(new String[]{
                "package " + CLASS_PACKAGE + ";",
                "",
                "import java.lang.String;",
                "",
                "public final class " + CLASS_NAME + " {",
                "  public static final String aTextFile = \"aTextFile\";",
                "",
                "  public static final String anotherTextFile = \"anotherTextFile\";",
                "}",
                ""
            }, "\n"),
            testableGenerator().generateCode(context)
        );
    }

    private Path aDirectoryWithFiles(String directoryName, String[] fileNames) throws IOException {
        File directory = tmp.newFolder(directoryName);
        for (String fileName : fileNames) {
            File aFile = FileUtils.getFile(directory, fileName);
            aFile.createNewFile();
        }
        return directory.toPath();
    }


    private String emptyClass(String classPackage, String className) {
        return StringUtils.join(new String[]{
            "package " + classPackage + ";",
            "",
            "public final class " + className + " {",
            "}",
            ""
        }, "\n");
    }

    private Path nonExistingDirectory() throws IOException, URISyntaxException {
        return Paths
            .get(tmp.getRoot().toURI())
            .resolve("aDirThatDoesNotExist_" + System.currentTimeMillis());
    }

    private Path emptyDirectory() {
        return tmp.getRoot().toPath();
    }

    private RegistryClassCodeGenerator testableGenerator() {
        return new RegistryClassCodeGenerator();
    }
}