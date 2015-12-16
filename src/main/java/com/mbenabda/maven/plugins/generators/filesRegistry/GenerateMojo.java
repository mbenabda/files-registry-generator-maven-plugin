package com.mbenabda.maven.plugins.generators.filesRegistry;


import com.mbenabda.maven.plugins.generators.filesRegistry.generators.javapoet.RegistryClassCodeGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.tools.JavaFileObject;
import java.io.File;
import java.nio.file.Path;

import static com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext.iWantToGenerateARegistryClass;

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
        final Path pathToFiles = project.getBasedir().toPath().resolve(this.pathToFiles);

        if(pathToFiles.toFile().exists()) {
            getLog().info(String.format(
                    "Accessing %s to generate the registry class %s at %s",
                    pathToFiles,
                    registryPackageName + "." + registryClassName,
                    outputDirectory
            ));

            if(!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            try {
                FileUtils.writeStringToFile(
                        javaFileAt(outputDirectory.toPath(), registryClassName),

                        new RegistryClassCodeGenerator().generateCode(
                                iWantToGenerateARegistryClass()
                                        .called(registryClassName)
                                        .inPackage(registryPackageName)
                                        .fromTheFilesUnder(pathToFiles)
                                        .thatMatch(new FileHasIncludedSuffixPredicate(filesSuffix))
                                        .please()
                        )
                );

                project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
            } catch(Exception e) {
                throw new MojoExecutionException("", e);
            }
        } else {
            throw new MojoFailureException("Path " + pathToFiles + " does not exist.");
        }
    }

    private File javaFileAt(Path directory, String className) {
        return directory
                .resolve(className + JavaFileObject.Kind.SOURCE.extension)
                .toFile();
    }
}
