package com.pharmacy.util;

import java.util.Random;

public class NdcGenerator {

    private static final Random RANDOM = new Random();
    private static final String NDC_FORMAT = "%05d%04d%02d";

    public static String generateNdc() {
        int labelerCode = RANDOM.nextInt(100000);
        int drugCode = RANDOM.nextInt(10000);
        int packageCode = RANDOM.nextInt(100);
        return String.format(NDC_FORMAT, labelerCode, drugCode, packageCode);
    }

    public static String generateUniqueNdc(java.util.Set<String> existingNdcs) {
        String ndc;
        int attempts = 0;
        final int MAX_ATTEMPTS = 1000;
        do {
            ndc = generateNdc();
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                throw new RuntimeException("Unable to generate unique NDC after " + MAX_ATTEMPTS + " attempts");
            }
        } while (existingNdcs.contains(ndc));
        return ndc;
    }
}
