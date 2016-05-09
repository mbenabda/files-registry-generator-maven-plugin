package com.mbenabda.maven.plugins.generators.filesRegistry;

import com.google.common.base.Predicate;
import com.mbenabda.maven.plugins.generators.filesRegistry.naming.JavaNamingConvention;
import com.mbenabda.maven.plugins.generators.filesRegistry.naming.StandardJavaNamingConvention;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.nio.file.Path;

public class RegistryGenerationContext {
    private static final JavaNamingConvention NAMING_CONVENTION = new StandardJavaNamingConvention();
    private final Path filesLocation;
    private final Predicate<Path> includedFilesSpecification;
    private final String registrySimpleClassName;
    private final String registryPackageName;

    public static Builder iWantToGenerateARegistryClass() {
        return new Builder();
    }

    private RegistryGenerationContext(String registryPackageName, String registrySimpleClassName, Path filesLocation, Predicate<Path> includedFilesSpecification) {
        validatePackageName(registryPackageName);
        validateClassName(registrySimpleClassName);
        Validate.notNull(filesLocation);
        Validate.notNull(includedFilesSpecification);

        this.registryPackageName = registryPackageName;
        this.registrySimpleClassName = registrySimpleClassName;
        this.filesLocation = filesLocation;
        this.includedFilesSpecification = includedFilesSpecification;
    }

    public String getRegistryPackageName() {
        return registryPackageName;
    }

    public String getRegistrySimpleClassName() {
        return registrySimpleClassName;
    }

    public Path getFilesLocation() {
        return filesLocation;
    }

    private void validatePackageName(String packageName) {
        Validate.notBlank(packageName);
        Validate.isTrue(NAMING_CONVENTION.isPackageName(packageName));
    }

    private void validateClassName(String registrySimpleClassName) {
        Validate.notBlank(registrySimpleClassName);
        Validate.isTrue(NAMING_CONVENTION.isSimpleClassName(registrySimpleClassName));
    }

    public JavaNamingConvention getJavaNamingConvention() {
        return NAMING_CONVENTION;
    }

    public boolean isRootDirectory(Path directory) {
        return directory.equals(getFilesLocation());
    }

    public boolean accepts(File file) {
        return file.exists()
            && !file.isHidden()
            && includedFilesSpecification.apply(file.toPath());
    }

    public static class Builder {
        private static final Predicate<Path> TRUE_PREDICATE = new Predicate<Path>() {
            @Override
            public boolean apply(Path input) {
                return true;
            }
        };

        private Path filesLocation;
        private Predicate<Path> includedFilesSpecification = TRUE_PREDICATE;
        private String registryPackageName = "com.mbenabda.filesRegistry";
        private String registrySimpleClassName = "FilesRegistry";

        public Builder called(String registrySimpleClassName) {
            this.registrySimpleClassName = registrySimpleClassName;
            return this;
        }

        public Builder inPackage(String registryPackageName) {
            this.registryPackageName = registryPackageName;
            return this;
        }

        public Builder fromTheFilesUnder(Path filesLocation) {
            this.filesLocation = filesLocation;
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
                filesLocation,
                includedFilesSpecification
            );
        }
    }
}
