package com.aiknowledgebase.extractor;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class ExtractorPackageIT {
    @Test
    void packagedChildUsesJava26AndRejectsUnsupportedExtraction() throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process probe = new ProcessBuilder(java, "-Xmx256m", "-jar", "target/pdf-extractor-0.1.0-SNAPSHOT.jar", "--version").start();
        assertTrue(probe.waitFor(10, TimeUnit.SECONDS));
        assertEquals(0, probe.exitValue());
        assertTrue(new String(probe.getInputStream().readAllBytes()).contains("\"javaFeature\":26"));
        Process unsupported = new ProcessBuilder(java, "-Xmx256m", "-jar", "target/pdf-extractor-0.1.0-SNAPSHOT.jar", "example.pdf").start();
        assertTrue(unsupported.waitFor(10, TimeUnit.SECONDS));
        assertEquals(2, unsupported.exitValue());
    }
}
