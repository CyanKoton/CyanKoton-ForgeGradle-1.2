package net.minecraftforge.gradle.obf;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

import java.io.File;

public class ObfuscationTest {
    private static final File EXECUTION_ROOT = new File("build/tmp/testing/obf");
    private static final File TEST_ROOT = new File("src/test/resources/net/minecraftforge/gradle/obf");
    private static final File ACTUAL_CLEAN = new File(TEST_ROOT, "ActualClean.jar");
    private static final File ACTUAL_OBF = new File(TEST_ROOT, "ActualObf.jar");
    private static final File DEP_CLEAN = new File(TEST_ROOT, "DepClean.jar");
    private static final File SRG = new File(TEST_ROOT, "obfuscate.srg");

    private Project makeProject() {
        return ProjectBuilder.builder().withProjectDir(EXECUTION_ROOT).build();
    }

/* // I don't know how to run thistest
    @Test
    public void singleArtifactTest() {
        String expectedHash = Constants.hash(ACTUAL_OBF);
        File output = new File(EXECUTION_ROOT, "output.jar");

        Project proj = makeProject();
        SingleDeobfTask task = BasePlugin.makeTask(proj, "deobfOne", SingleDeobfTask.class);
        task.setInJar(new DelayedFile(ACTUAL_CLEAN));
        task.setSrg(new DelayedFile(SRG));
        task.addClasspath(DEP_CLEAN);
        task.setOutJar(new DelayedFile(output));
        task.setDoesCache(false);

        task.execute();

        String actualHash = Constants.hash(output);
        Assert.assertEquals(expectedHash, actualHash);
    }
// */
}
