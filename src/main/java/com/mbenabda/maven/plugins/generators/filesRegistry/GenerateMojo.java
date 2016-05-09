package com.mbenabda.maven.plugins.generators.filesRegistry;


import com.mbenabda.maven.plugins.generators.filesRegistry.filters.FilenameHasSuffixPredicate;
import com.mbenabda.maven.plugins.generators.filesRegistry.generators.RegistryClassCodeGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.tools.JavaFileObject;
import java.io.File;
import java.nio.file.Path;

import static com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext.iWantToGenerateARegistryClass;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources")
    private File outputDirectory;

    @Parameter(required = true)
    private String relativeFilesLocation;

    @Parameter(required = true)
    private String filenameSuffix;

    @Parameter(defaultValue = "com.mbenabda.filesRegistry")
    private String registryPackageName;

    @Parameter(defaultValue = "FilesRegistry")
    private String registryClassName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Path filesLocation = project.getBasedir().toPath().resolve(this.relativeFilesLocation);

        if(filesLocation.toFile().exists()) {
            getLog().info(String.format(
                    "Accessing %s to generate the registry class %s at %s",
                    filesLocation,
                    registryPackageName + "." + registryClassName,
                    outputDirectory
            ));

            createMissingDirectoriesTo(outputDirectory);

            try {
                FileUtils.writeStringToFile(
                        javaFileAt(outputDirectory.toPath(), registryClassName),

                        new RegistryClassCodeGenerator().generateCode(
                                iWantToGenerateARegistryClass()
                                        .called(registryClassName)
                                        .inPackage(registryPackageName)
                                        .fromTheFilesUnder(filesLocation)
                                        .thatMatch(new FilenameHasSuffixPredicate(filenameSuffix))
                                        .please()
                        )
                );

                project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
            } catch(Exception e) {
                throw new MojoExecutionException("", e);
            }
        } else {
            throw new MojoFailureException("Path " + filesLocation + " does not exist.");
        }
    }

    private void createMissingDirectoriesTo(File target) {
        if(!target.exists()) {
            target.mkdirs();
        }
    }

    private File javaFileAt(Path directory, String className) {
        return directory
                .resolve(className + JavaFileObject.Kind.SOURCE.extension)
                .toFile();
    }
}
