package com.dev.codegen.mvn.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;

@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CodegenMojo extends AbstractMojo {

    @Parameter
    private Map<String, String> jdbc;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (getLog().isInfoEnabled()) {
            getLog().info(jdbc.get("url"));
            getLog().info(jdbc.get("driver"));
            getLog().info(jdbc.get("username"));
            getLog().info(jdbc.get("password"));
        }//if

    }
}
