package com.dev.codegen.mvn.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CodegenMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}
