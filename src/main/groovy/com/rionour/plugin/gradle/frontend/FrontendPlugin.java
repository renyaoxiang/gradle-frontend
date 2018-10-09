package com.rionour.plugin.gradle.frontend;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.PathSensitivity;

import java.io.File;

public class FrontendPlugin implements Plugin<Project> {

    public static final String GroupName = "frontend";
    Project project = null;

    @Override
    public void apply(Project project) {
        this.project = project;
        project.getExtensions().add(GroupName, new FrontendExtension());

        project.afterEvaluate(it0 -> {
            project.getTasks().create("frontendInit", Exec.class, (task) -> {
                task.setGroup(GroupName);
                task.setWorkingDir(new File("."));
                task.setCommandLine("sh", "-c", "npx create-react-app " + appName() + " --scripts-version=react-scripts-ts");
            });
            project.getTasks().create("frontendInstall", Exec.class, (task) -> {
                task.setGroup(GroupName);
                task.setWorkingDir(new File(appName()));
                task.getInputs().file(appNamePrefix("/package.json")).withPathSensitivity(PathSensitivity.RELATIVE);
                task.setCommandLine("sh", "-c", "npm install");
            });
            project.getTasks().create("frontendBuild", Exec.class, (task) -> {
                task.setGroup(GroupName);
                task.setWorkingDir(new File(appName()));
                task.getInputs().file(appNamePrefix("/package-lock.json")).withPathSensitivity(PathSensitivity.RELATIVE);
                task.getInputs().dir(appNamePrefix("/src")).withPathSensitivity(PathSensitivity.RELATIVE);
                task.getOutputs().dirs("build", appNamePrefix("build"));
                task.getOutputs().cacheIf(it1 -> true);
                task.setCommandLine("sh", "-c", "npm run build");
            });
            project.getTasks().create("frontendProcessResources", Copy.class, (task) -> {
                task.setGroup(GroupName);
                task.getRootSpec().from(appNamePrefix("/build/"));
                task.getRootSpec().into(project.getBuildDir() + "/resources/main/public");
            });
            project.getTasks().findByName("processResources").dependsOn("frontendProcessResources");
        });
    }

    public String appNamePrefix(String path) {
        return appName() + path;
    }

    public String appName() {
        FrontendExtension extension = (FrontendExtension) project.getExtensions().getByName(GroupName);
        return extension.appName;
    }
}
