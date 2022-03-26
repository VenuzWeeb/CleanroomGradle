package com.cleanroommc.gradle.tests;

import com.cleanroommc.gradle.CleanroomLogger;
import com.cleanroommc.gradle.extensions.MinecraftExtension;
import com.cleanroommc.gradle.tasks.download.DownloadManifestTask;
import com.cleanroommc.gradle.tasks.download.DownloadVersionTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.File;
import java.io.IOException;

import static com.cleanroommc.gradle.Constants.DOWNLOAD_MANIFEST;
import static com.cleanroommc.gradle.Constants.DOWNLOAD_VERSION;

@TestMethodOrder(OrderAnnotation.class)
public class ProjectTaskTests {

    static Project project;

    @BeforeAll
    public static void setupProject() {
        File projectDir = new File(".", "test/project/");
        File homeDir = new File(".", "test/gradle_home/");
        project = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .withGradleUserHomeDir(homeDir)
                .build();
        // Load
        project.getPluginManager().apply("com.cleanroommc.gradle");
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        testInfo.getTestMethod().ifPresent(m -> CleanroomLogger.log("TEST PHASE >> {}", m.getName()));
        MinecraftExtension.get(project).setVersion("1.12.2");
    }

    @Test
    @Order(1)
    public void testDefaults() throws IOException {
        // Assert default maven repos
        Assertions.assertEquals(1, project.getRepositories().stream().filter(ar -> ar.getName().equals("Minecraft")).count());
        Assertions.assertEquals(1, project.getRepositories().stream().filter(ar -> ar.getName().equals("CleanroomMC")).count());
    }

    @Test
    @Order(2)
    public void testMetaDownloads() throws IOException {
        Task task = project.getTasks().getByPath(DOWNLOAD_MANIFEST);
        Assertions.assertTrue(task instanceof DownloadManifestTask);
        DownloadManifestTask dlMeta = (DownloadManifestTask) task;
        dlMeta.downloadManifest();

        task = project.getTasks().getByPath(DOWNLOAD_VERSION);
        Assertions.assertTrue(task instanceof DownloadVersionTask);
        DownloadVersionTask dlVersion = (DownloadVersionTask) task;
        dlVersion.downloadVersion();
    }

}
