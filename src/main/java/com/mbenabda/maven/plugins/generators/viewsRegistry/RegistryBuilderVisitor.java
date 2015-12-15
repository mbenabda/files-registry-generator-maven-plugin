package com.mbenabda.maven.plugins.generators.viewsRegistry;

import com.google.common.base.Predicate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * usage:
 RegistryBuilderVisitor registry = new RegistryBuilderVisitor(
    registryPackageName,
    registryClassName,
    includes,
    getLog()
 );

 Files.walkFileTree(pathToFiles, registry);

 getLog().info("Generated java code : ");
 getLog().info(registry.getGeneratedJavaCode());
 */
public class RegistryBuilderVisitor implements FileVisitor<Path> {
    private final String packageName;
    private final Log logger;
    private final TypeSpec rootClass;
    private final Predicate<Path> includes;
    public TypeSpec parentClass;
    public TypeSpec currentClass;

    RegistryBuilderVisitor(String packageName, String registryClassName, Predicate<Path> includes, Log logger) {
        this.packageName = packageName;
        this.includes = includes;
        this.logger = logger;

        rootClass = TypeSpec.classBuilder(registryClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .build();

        parentClass = rootClass;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        logger.info("generating class " + asClassName(dir) + " for " + dir + ", parentClass = " + parentClass.name + " currentClass = " + asInnerClassName(parentClass, dir).simpleName());

        currentClass = TypeSpec.classBuilder(asInnerClassName(parentClass, dir).simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .build();


        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(includes.apply(file)) {
            logger.info("generating field " + asFieldName(file) + " of class + " + currentClass.name + " for " + file);

            FieldSpec field = FieldSpec
                    .builder(String.class, asFieldName(file))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .build();

            currentClass = currentClass.toBuilder()
                    .addField(field)
                    .build();
        } else {
            logger.info("skipping " + file);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        parentClass = parentClass.toBuilder().addType(currentClass).build();
        parentClass = currentClass;
        return FileVisitResult.CONTINUE;
    }


    private String asClassName(Path dir) {
        String name = FileUtils.basename(dir.toFile().toString());
        String firstLetter = "" + name.charAt(0);
        return firstLetter.toUpperCase() + name.substring(1, name.length());
    }

    private String asFieldName(Path file) {
        return FileUtils.basename(FileUtils.removeExtension(file.toFile().toString())).toLowerCase();
    }

    private ClassName asInnerClassName(TypeSpec parentClass, Path dir) {
        if(parentClass == rootClass) {
            return ClassName.get(packageName, asClassName(dir));
        }
        return ClassName.get(packageName, parentClass.name, asClassName(dir));
    }

    public String getGeneratedJavaCode() {
        return JavaFile.builder(packageName, rootClass)
                .build()
                .toString();
    }
}