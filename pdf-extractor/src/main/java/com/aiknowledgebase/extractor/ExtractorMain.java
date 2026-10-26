package com.aiknowledgebase.extractor;

/** T1 packaging probe. PDF extraction is introduced by T6. */
public final class ExtractorMain {
    public static void main(String[] args) {
        if (args.length != 1 || !args[0].equals("--version")) {
            System.err.println("Usage: java -jar pdf-extractor.jar --version; extraction is not implemented yet");
            System.exit(2);
        }
        System.out.println("{\"contractVersion\":1,\"javaFeature\":" + Runtime.version().feature()
                + ",\"extractionAvailable\":false}");
    }
}
