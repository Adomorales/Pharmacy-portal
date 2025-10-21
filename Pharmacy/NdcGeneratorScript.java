import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NdcGeneratorScript {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        Set<String> usedNdcs = new HashSet<>();
        String[] originalNdcs = {
            "00002030401", "00071080401", "00045080401", "00067030401", "00008070401",
            "00025030401", "00069030401", "00093040401", "00054050401", "00056020401",
            "00071000401", "00031010401", "00078000401", "00013000401", "00035000401",
            "00068000401", "00022000401", "00017000401", "00097000401", "00004000401",
            "00085020401", "00012000401", "00015000401", "00016000401", "00017000402",
            "00018000401", "00019000401", "00020000401", "00021000401", "00022000401",
            "00029000401", "00030000401", "00031000401", "00032000401", "00033000401",
            "00034000401", "00035000401", "00036000401", "00037000401", "00038000401",
            "00039000401", "00040000401", "00041000401", "00042000401", "00043000401",
            "00044000401", "00045000401", "00046000401", "00047000401", "00048000401",
            "55501000901", "55501000801", "55501000201", "55501000601", "55501001201",
            "55501000101", "55501001101", "55501001001", "55501000701", "55501000301",
            "55501001601", "55501000501", "55501000401"
        };

        for (String ndc : originalNdcs) {
            usedNdcs.add(ndc);
        }

        String[] uniqueNdcs = new String[63];
        for (int i = 0; i < 63; i++) {
            String ndc;
            do {
                ndc = generateNdc();
            } while (usedNdcs.contains(ndc));
            usedNdcs.add(ndc);
            uniqueNdcs[i] = ndc;
        }

        System.out.println("-- Update product NDCs to be unique");
        for (int i = 0; i < 63; i++) {
            System.out.println("UPDATE pharmacy.product SET ndc = '" + uniqueNdcs[i] + "' WHERE product_id = " + (i + 1) + ";");
        }
    }

    private static String generateNdc() {
        int labelerCode = RANDOM.nextInt(100000);
        int drugCode = RANDOM.nextInt(10000);
        int packageCode = RANDOM.nextInt(100);
        return String.format("%05d%04d%02d", labelerCode, drugCode, packageCode);
    }
}
