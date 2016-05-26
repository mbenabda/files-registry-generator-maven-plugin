package com.mbenabda.maven.plugins.generators.filesRegistry.generators;

import com.google.common.base.Predicates;
import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.mbenabda.maven.plugins.generators.filesRegistry.filters.FilenameHasSuffixPredicate;
import org.apache.commons.lang3.NotImplementedException;
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
import static org.apache.commons.io.FileUtils.*;
import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertEquals;

public class RegistryClassCodeGeneratorTest {
    
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");;
    private static final String INDENT = "  ";
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private static final String CLASS_NAME = "RegistryClass";
    private static final String CLASS_PACKAGE = "com.mbenabda.tests.fileRegistry";

    @Test
    public void should_generate_an_empty_class_for_an_empty_directory() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .fromTheFilesUnder(emptyDirectory())
            .inPackage(CLASS_PACKAGE)
            .please();

        assertEquals(
            aClass(CLASS_PACKAGE, CLASS_NAME),
            testableGenerator().generateCode(context)
        );
    }

    @Test(expected = FileNotFoundException.class)
    public void should_not_generate_the_registry_class_when_source_directory_does_not_exist() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .fromTheFilesUnder(nonExistingDirectory())
            .inPackage(CLASS_PACKAGE)
            .please();

        assertEquals(
            aClass(CLASS_PACKAGE, CLASS_NAME),
            testableGenerator().generateCode(context)
        );
    }

    @Test
    public void should_generate_fields_for_accepted_files() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .inPackage(CLASS_PACKAGE)
            .fromTheFilesUnder(aDirectoryWithFiles("directory", new String[]{"aTextFile.txt", "anImage.jpg", "anotherTextFile.txt"}).toPath())
            .thatMatch(new FilenameHasSuffixPredicate(".txt"))
            .please();

        assertEquals(
            aClass(
                CLASS_PACKAGE, CLASS_NAME,
                aField("aTextFile"),
                aField("anotherTextFile")
            ),

            testableGenerator().generateCode(context)
        );
    }

    private String aField(String name) {
        return aField(name, name);
    }
    private String aField(String name, String value) {
        return String.format("public static final String %s = \"%s\";", name, value);
    }

    @Test
    public void should_not_generate_a_nested_class_for_a_subdirectory_without_accepted_files() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .inPackage(CLASS_PACKAGE)
            .fromTheFilesUnder(aDirectoryWithFiles("images", new String[]{ "anImage.png" }).toPath())
            .thatMatch(new FilenameHasSuffixPredicate(".txt"))
            .please();

        assertEquals(
            aClass(CLASS_PACKAGE, CLASS_NAME),
            testableGenerator().generateCode(context)
        );
    }

    @Test
    public void should_generate_a_nested_class_for_a_subdirectory_with_accepted_files() throws Exception {
        File root = aDirectoryWithFiles(
            "images",
            aDirectoryWithFiles(
                "subDir",
                new String[]{"anImage.png"}
            )
        );

        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .inPackage(CLASS_PACKAGE)
            .fromTheFilesUnder(root.toPath())
            .thatMatch(new FilenameHasSuffixPredicate(".png"))
            .please();

        assertEquals(
            aClass(
                CLASS_PACKAGE,
                CLASS_NAME,
                aNestedClass("SubDir", aField("anImage", "subDir/anImage"))
            ),
            testableGenerator().generateCode(context)
        );
    }

    @Test
    @Ignore
    public void what_happens_on_conflict() throws Exception {
        RegistryGenerationContext context = iWantToGenerateARegistryClass()
            .called(CLASS_NAME)
            .inPackage(CLASS_PACKAGE)
            .fromTheFilesUnder(aDirectoryWithFiles("directory", new String[]{"anImage.jpg", "anImage.png"}).toPath())
            .thatMatch(Predicates.or(
                new FilenameHasSuffixPredicate(".jpg"),
                new FilenameHasSuffixPredicate(".png")
            ))
            .please();

        throw new NotImplementedException("conflict resolution");
    }

    @Test
    @Ignore
    public void what_happens_on_symbolic_links() throws Exception {
        throw new NotImplementedException("symbolic links");
    }

    private File aDirectoryWithFiles(String directoryName, String... fileNames) throws IOException {
        File directory = tmp.newFolder(directoryName);
        for (String fileName : fileNames) {
            File aFile = getFile(directory, fileName);
            aFile.createNewFile();
        }
        return directory;
    }

    private File aDirectoryWithFiles(String directoryName, File... files) throws IOException {
        File directory = tmp.newFolder(directoryName);
        for (File file : files) {
            if(file.isDirectory()) {
                copyDirectoryToDirectory(file, directory);
            } else {
                copyFileToDirectory(file, directory);
            }
        }
        return directory;
    }

    private String aClass(String classPackage, String className, String... memberDeclarations) {
        boolean classHasMembers = memberDeclarations.length > 0;

        String pkg = line("package " + classPackage + ";") + emptyLine();

        String imports = classHasMembers
            ? line("import java.lang.String;") + emptyLine()
            : "";

        String classDeclaration = line("public final class " + className + " {");

        String members = classHasMembers
            ? join(memberDeclarations, emptyLine() + LINE_SEPARATOR) + LINE_SEPARATOR
            : "";

        String endOfClass = line("}");

        return pkg
            + imports
            + classDeclaration
            + indent(members)
            + endOfClass;
    }

    private String aNestedClass(String className, String... memberDeclarations) {
        String classDeclaration = line("public static final class " + className + " {");
        String members = memberDeclarations.length > 0
            ? join(memberDeclarations, emptyLine() + LINE_SEPARATOR) + LINE_SEPARATOR
            : "";
        String endOfClass = "}";
        return classDeclaration
            + indent(members)
            + endOfClass;
    }

    private String emptyLine() {
        return line("");
    }

    private String line(String text) {
        return text + LINE_SEPARATOR;
    }

    private String indent(String text) {
        return text.replaceAll("(?m)^(.)", INDENT + "$1");
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