package com.mbenabda.maven.plugins.generators.filesRegistry;

import com.google.common.base.Predicate;
import org.apache.commons.lang3.Validate;

import java.nio.file.Path;

public class RegistryGenerationContext {
    private static final JavaNamingConvention NAMING_CONVENTION = new JavaNamingConvention();
    private final Path filesRootDirectory;
    private final Predicate<Path> includedFilesSpecification;
    private final String registrySimpleClassName;
    private final String registryPackageName;

    private RegistryGenerationContext(String registryPackageName, String registrySimpleClassName, Path filesRootDirectory, Predicate<Path> includedFilesSpecification) {
        validatePackageName(registryPackageName);
        validateClassName(registrySimpleClassName);
        Validate.notNull(filesRootDirectory);
        Validate.notNull(includedFilesSpecification);

        this.registryPackageName = registryPackageName;
        this.registrySimpleClassName = registrySimpleClassName;
        this.filesRootDirectory = filesRootDirectory;
        this.includedFilesSpecification = includedFilesSpecification;
    }

    public String getRegistryPackageName() { return registryPackageName; }
    public String getRegistrySimpleClassName() { return registrySimpleClassName; }
    public Path getFilesRootDirectory() { return filesRootDirectory; }
    public Predicate<Path> getIncludedFilesSpecification() { return includedFilesSpecification; }

    private void validatePackageName(String packageName) {
        Validate.notBlank(packageName);
        Validate.isTrue(NAMING_CONVENTION.isPackageName(packageName));
    }

    private void validateClassName(String registrySimpleClassName) {
        Validate.notBlank(registrySimpleClassName);
        Validate.isTrue(NAMING_CONVENTION.isSimpleClassName(registrySimpleClassName));
    }



    public static Builder iWantToGenerateARegistryClass() {
        return new Builder();
    }

    public JavaNamingConvention getJavaNamingConvention() {
        return NAMING_CONVENTION;
    }

    public static class Builder {
        private static final Predicate<Path> TRUE_PREDICATE = new Predicate<Path>() {
            @Override public boolean apply(Path input) { return true; }
        };

        private Path filesRootDirectory;
        private Predicate<Path> includedFilesSpecification = TRUE_PREDICATE;
        private String registryPackageName;
        private String registrySimpleClassName;

        public Builder called(String registrySimpleClassName) {
            this.registrySimpleClassName = registrySimpleClassName;
            return this;
        }

        public Builder inPackage(String registryPackageName) {
            this.registryPackageName = registryPackageName;
            return this;
        }

        public Builder fromTheFilesUnder(Path filesRootDirectory) {
            this.filesRootDirectory = filesRootDirectory;
            return this;
        }

        public Builder thatMatch(Predicate<Path> includedFilesSpecification) {
            this.includedFilesSpecification = includedFilesSpecification;
            return this;
        }

        public RegistryGenerationContext please() {
            return new RegistryGenerationContext(
                    registryPackageName,
                    registrySimpleClassName,
                    filesRootDirectory,
                    includedFilesSpecification
            );
        }
    }
}
