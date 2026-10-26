package com.aiknowledgebase.extractor;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class ExtractorLaunchTest {
    @Test
    void childUsesTheCurrentJavaRuntimeAndRejectsExtraction() throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process probe = new ProcessBuilder(java, "-Xmx256m", "-cp", "target/classes",
                ExtractorMain.class.getName(), "--version").start();
        assertTrue(probe.waitFor(10, TimeUnit.SECONDS));
        assertEquals(0, probe.exitValue());
        assertTrue(new String(probe.getInputStream().readAllBytes()).contains("\"javaFeature\":26"));
        Process unsupported = new ProcessBuilder(java, "-cp", "target/classes",
                ExtractorMain.class.getName(), "example.pdf").start();
        assertTrue(unsupported.waitFor(10, TimeUnit.SECONDS));
        assertEquals(2, unsupported.exitValue());
    }
}
