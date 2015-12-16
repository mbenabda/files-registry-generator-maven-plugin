package com.mbenabda.maven.plugins.generators.viewsRegistry;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources")
    private File outputDirectory;

    @Parameter(required = true)
    private String pathToFiles;

    @Parameter(required = true)
    private String filesSuffix;

    @Parameter(defaultValue = "com.mbenabda.filesRegistry")
    private String registryPackageName;

    @Parameter(defaultValue = "FilesRegistry")
    private String registryClassName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Path pathToFiles = Paths.get(this.pathToFiles);

        if(pathToFiles.toFile().exists()) {
            getLog().info(String.format(
                    "The plugin will access the %s files in %s to generate the registry class %s at %s",
                    filesSuffix,
                    pathToFiles,
                    registryPackageName + "." + registryClassName,
                    outputDirectory()
            ));

            try {
                String registryClassCode = new RegistryClassCodeGenerator()
                        .generateClassCode(
                                pathToFiles,
                                new FileHasIncludedSuffixPredicate(filesSuffix),
                                registryPackageName,
                                registryClassName
                        );

                writeJavaCode(registryClassCode, outputDirectory());

                project.addCompileSourceRoot(outputDirectory().toAbsolutePath().toString());
            } catch(Exception e) {
                throw new MojoExecutionException("", e);
            }
        } else {
            throw new MojoFailureException("Path " + pathToFiles + " does not exist.");
        }
    }

    private void writeJavaCode(String javaCode, Path outputDirectory) throws IOException {
        getLog().info(javaCode);

        File javaFile = outputDirectory
                .resolve(registryClassName + JavaFileObject.Kind.SOURCE.extension)
                .toFile();

        FileUtils.writeStringToFile(javaFile, javaCode);
    }

    private Path outputDirectory() {
        Path targetRegistryClassDirectory = this.outputDirectory
                .toPath()
                .resolve(packageNameAsPath(registryPackageName));

        if(!targetRegistryClassDirectory.toFile().exists()) {
            targetRegistryClassDirectory.toFile().mkdirs();
        }
        return targetRegistryClassDirectory;
    }

    private Path packageNameAsPath(String packageName) {
        String asPath = packageName.replaceAll("[.]", File.separator);
        return Paths.get(asPath);
    }

}
