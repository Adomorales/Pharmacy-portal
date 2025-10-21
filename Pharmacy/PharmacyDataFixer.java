import java.util.*;

public class PharmacyDataFixer {
    public static void main(String[] args) {
        String[] products = {
            "('00002030401', 'Lisinopril 10mg', 'Lisinopril', 'Aurobindo', 'Tablet', '10mg', true, false, 0.50, 100, 1)",
            "('00071080401', 'Amoxicillin 500mg', 'Amoxicillin', 'GlaxoSmithKline', 'Capsule', '500mg', true, false, 1.25, 50, 2)",
            "('00045080401', 'Ibuprofen 200mg', 'Ibuprofen', 'Johnson & Johnson', 'Tablet', '200mg', false, false, 0.25, 200, 3)",
            "('00067030401', 'Acetaminophen 500mg', 'Acetaminophen', 'Johnson & Johnson', 'Tablet', '500mg', false, false, 0.15, 300, 4)",
            "('00008070401', 'Omeprazole 20mg', 'Omeprazole', 'AstraZeneca', 'Capsule', '20mg', true, false, 2.00, 75, 5)",
            "('00025030401', 'Simvastatin 20mg', 'Simvastatin', 'Merck', 'Tablet', '20mg', true, false, 0.75, 120, 6)",
            "('00069030401', 'Metformin 500mg', 'Metformin', 'Bristol-Myers Squibb', 'Tablet', '500mg', true, false, 0.40, 150, 7)",
            "('00093040401', 'Hydrochlorothiazide 25mg', 'Hydrochlorothiazide', 'MediRx', 'Tablet', '25mg', true, false, 0.30, 180, 8)",
            "('55501000901', 'Hepatitis A (Adult) 1 mL','Hepatitis A Vaccine (Inactivated)','GSK','Injection','1 mL', true, true, 75.00, 50, 9)",
            "('00054050401', 'Prednisone 10mg', 'Prednisone', 'MediRx', 'Tablet', '10mg', true, false, 0.60, 90, 10)",
            "('00056020401', 'Warfarin 5mg', 'Warfarin Sodium', 'Bristol-Myers Squibb', 'Tablet', '5mg', true, false, 1.50, 60, 11)",
            "('55501000801', 'Hepatitis B (Adult) 1 mL','Hepatitis B Vaccine (Recombinant)','GSK','Injection','1 mL', true, true, 62.00, 85, 12)",
            "('55501000201', 'COVID-19 mRNA (Pfizer) 0.3 mL','SARS-CoV-2 mRNA Vaccine (Pfizer-BioNTech)','Pfizer','Injection','0.3 mL', true, true, 95.00, 80, 13)",
            "('55501000601', 'COVID-19 mRNA (Moderna) 0.3 mL','SARS-CoV-2 mRNA Vaccine (Moderna)','Moderna','Injection','0.3 mL', true, true, 95.00, 80, 14)",
            "('00071000401', 'Clopidogrel 75mg', 'Clopidogrel', 'Sanofi', 'Tablet', '75mg', true, false, 2.25, 45, 15)",
            "('00031010401', 'Sertraline 50mg', 'Sertraline HCl', 'Pfizer', 'Tablet', '50mg', true, false, 1.80, 80, 16)",
            "('55501001201', 'Zoster (Shingrix) 0.5 mL','Recombinant Zoster Vaccine','GSK','Injection (IM, 2-dose)','0.5 mL', true, true, 175.00, 55, 17)",
            "('00078000401', 'Citalopram 20mg', 'Citalopram', 'MediRx', 'Tablet', '20mg', true, false, 0.90, 110, 18)",
            "('00013000401', 'Fluoxetine 20mg', 'Fluoxetine HCl', 'MediRx', 'Capsule', '20mg', true, false, 1.20, 70, 19)",
            "('00035020401', 'Escitalopram 10mg', 'Escitalopram', 'MediRx', 'Tablet', '10mg', true, false, 1.60, 55, 20)",
            "('00068000401', 'Levothyroxine 50mcg', 'Levothyroxine Sodium', 'AbbVie', 'Tablet', '50mcg', true, false, 0.85, 140, 21)",
            "('55501000101', 'Influenza Quad 0.5 mL','Influenza Virus Vaccine, Quadrivalent','Sanofi Pasteur','Injection','0.5 mL', true, true, 25.00, 120, 22)",
            "('00022000401', 'Amlodipine 5mg', 'Amlodipine Besylate', 'Pfizer', 'Tablet', '5mg', true, true, 1.10, 95, 23)",
            "('00017000401', 'Losartan 50mg', 'Losartan Potassium', 'Merck', 'Tablet', '50mg', true, false, 1.35, 85, 24)",
            "('00097000401', 'Gabapentin 300mg', 'Gabapentin', 'Pfizer', 'Capsule', '300mg', true, false, 0.95, 130, 25)",
            "('00004000401', 'Tramadol 50mg', 'Tramadol HCl', 'MediRx', 'Tablet', '50mg', true, false, 0.70, 160, 26)",
            "('55501001101', 'Pneumococcal PCV20 0.5 mL','Pneumococcal 20-valent Conjugate Vaccine','Pfizer','Injection','0.5 mL', true, true, 195.00, 30, 27)",
            "('00085020401', 'Albuterol Inhaler', 'Albuterol', 'GlaxoSmithKline', 'Inhaler', '90mcg', true, false, 45.00, 25, 28)",
            "('00012000401', 'Fluticasone Inhaler', 'Fluticasone', 'GlaxoSmithKline', 'Inhaler', '110mcg', true, false, 120.00, 20, 29)",
            "('00015000401', 'Montelukast 10mg', 'Montelukast', 'Merck', 'Tablet', '10mg', true, false, 8.50, 40, 30)",
            "('00016000401', 'Cetirizine 10mg', 'Cetirizine', 'MediRx', 'Tablet', '10mg', false, false, 0.35, 250, 31)",
            "('00017000402', 'Loratadine 10mg', 'Loratadine', 'MediRx', 'Tablet', '10mg', false, false, 0.45, 220, 32)",
            "('00018000401', 'Fexofenadine 180mg', 'Fexofenadine', 'MediRx', 'Tablet', '180mg', false, false, 0.65, 180, 33)",
            "('00019000401', 'Diphenhydramine 25mg', 'Diphenhydramine', 'MediRx', 'Capsule', '25mg', false, false, 0.20, 300, 34)",
            "('00020000401', 'Ranitidine 150mg', 'Ranitidine', 'MediRx', 'Tablet', '150mg', false, false, 0.55, 190, 35)",
            "('00021000401', 'Pantoprazole 40mg', 'Pantoprazole', 'MediRx', 'Tablet', '40mg', true, false, 1.75, 85, 36)",
            "('00022500401', 'Esomeprazole 40mg', 'Esomeprazole', 'MediRx', 'Capsule', '40mg', true, false, 3.20, 65, 37)",
            "('55501001001', 'HPV 9-valent 0.5 mL','Human Papillomavirus 9-valent Vaccine','Merck','Injection','0.5 mL', true, true, 210.00, 45, 38)",
            "('00029000401', 'Alprazolam 0.5mg', 'Alprazolam', 'MediRx', 'Tablet', '0.5mg', true, false, 0.80, 120, 39)",
            "('00030000401', 'Lorazepam 1mg', 'Lorazepam', 'MediRx', 'Tablet', '1mg', true, false, 0.90, 100, 40)",
            "('00031000401', 'Diazepam 5mg', 'Diazepam', 'MediRx', 'Tablet', '5mg', true, false, 1.05, 90, 41)",
            "('00032000401', 'Clonazepam 1mg', 'Clonazepam', 'MediRx', 'Tablet', '1mg', true, false, 1.15, 80, 42)",
            "('00033000401', 'Temazepam 15mg', 'Temazepam', 'MediRx', 'Capsule', '15mg', true, false, 0.85, 110, 43)",
            "('55501000701', 'Varicella 0.5 mL','Varicella Virus Vaccine Live','Merck','Injection (SubQ)','0.5 mL', true, true, 135.00, 35, 44)",
            "('00034000401', 'Zolpidem 10mg', 'Zolpidem', 'MediRx', 'Tablet', '10mg', true, false, 1.25, 75, 45)",
            "('00035000401', 'Trazodone 50mg', 'Trazodone', 'MediRx', 'Tablet', '50mg', true, false, 0.95, 130, 46)",
            "('00036000401', 'Mirtazapine 15mg', 'Mirtazapine', 'MediRx', 'Tablet', '15mg', true, false, 1.40, 70, 47)",
            "('00037000401', 'Bupropion 150mg', 'Bupropion', 'MediRx', 'Tablet', '150mg', true, false, 1.60, 60, 48)",
            "('00038000401', 'Duloxetine 30mg', 'Duloxetine', 'MediRx', 'Capsule', '30mg', true, false, 2.80, 50, 49)",
            "('55501000301', 'COVID-19 mRNA (Moderna) 0.5 mL','SARS-CoV-2 mRNA Vaccine (Moderna)','Moderna','Injection','0.5 mL', true, true, 105.00, 70, 50)",
            "('00039000401', 'Venlafaxine 75mg', 'Venlafaxine', 'MediRx', 'Capsule', '75mg', true, false, 1.90, 85, 51)",
            "('00040000401', 'Paroxetine 20mg', 'Paroxetine', 'MediRx', 'Tablet', '20mg', true, false, 1.30, 95, 52)",
            "('00041000401', 'Citalopram Solution', 'Citalopram', 'MediRx', 'Solution', '10mg', true, false, 2.10, 45, 53)",
            "('00042000401', 'Quetiapine 25mg', 'Quetiapine', 'MediRx', 'Tablet', '25mg', true, false, 1.70, 80, 54)",
            "('00043000401', 'Risperidone 1mg', 'Risperidone', 'MediRx', 'Tablet', '1mg', true, false, 2.30, 65, 55)",
            "('00044000401', 'Olanzapine 5mg', 'Olanzapine', 'MediRx', 'Tablet', '5mg', true, false, 3.50, 55, 56)",
            "('55501001601', 'MMR 0.5 mL','Measles, Mumps and Rubella Vaccine','Merck','Injection','0.5 mL', true, true, 78.00, 40, 57)",
            "('00045000401', 'Aripiprazole 10mg', 'Aripiprazole', 'MediRx', 'Tablet', '10mg', true, false, 4.20, 40, 58)",
            "('55501000501', 'Td 0.5 mL','Tetanus and Diphtheria Toxoids','Sanofi Pasteur','Injection','0.5 mL', true, true, 28.00, 60, 59)",
            "('55501000401', 'Tdap 0.5 mL','Tetanus, Diphtheria, Acellular Pertussis Vaccine','GSK','Injection','0.5 mL', true, true, 52.00, 90, 60)",
            "('00046000401', 'Lamotrigine 100mg', 'Lamotrigine', 'MediRx', 'Tablet', '100mg', true, false, 1.80, 75, 61)",
            "('00047000401', 'Topiramate 50mg', 'Topiramate', 'MediRx', 'Tablet', '50mg', true, false, 1.45, 90, 62)",
            "('00048000401', 'Levetiracetam 500mg', 'Levetiracetam', 'MediRx', 'Tablet', '500mg', true, false, 2.60, 60, 63)"
        };

        Set<String> usedNdcs = new HashSet<>();
        Random random = new Random();

        for (String product : products) {
            String ndc = product.substring(product.indexOf("'") + 1, product.indexOf("',") + 2).replace("'", "");
            usedNdcs.add(ndc);
        }

        String[] customManufacturers = {
            "PharmaCorp USA", "MediHealth Solutions", "BioTech Pharmaceuticals",
            "GlobalRx Labs", "HealthFirst Manufacturing", "VitalCare Pharma",
            "MediSource Inc", "RxHealth Systems", "PharmaTech Industries",
            "BioMed Solutions", "CarePoint Pharmaceuticals", "MediLab Corporation"
        };

        Set<Integer> vaccineIndices = Set.of(9, 12, 13, 14, 17, 22, 27, 38, 44, 50, 57, 59, 60);

        System.out.println("INSERT INTO product (ndc, name, generic_name, manufacturer, dosage_form, strength, is_rx, is_vaccine, unit_price, stock_qty, medication_id) VALUES");

        for (int i = 0; i < products.length; i++) {
            String product = products[i];

            String ndc;
            do {
                int labelerCode =   random.nextInt(100000);
                int drugCode =      random.nextInt(10000);
                int packageCode =   random.nextInt(100);
                ndc =               String.format("%05d%04d%02d", labelerCode, drugCode, packageCode);
            } while (usedNdcs.contains(ndc));
            usedNdcs.add(ndc);

            int ndcStart =  product.indexOf("'") + 1;
            int ndcEnd =    product.indexOf("',", ndcStart);
            product =       product.substring(0, ndcStart) + ndc + product.substring(ndcEnd);

            if (product.contains("'MediRx'")) {
                String customManufacturer = customManufacturers[random.nextInt(customManufacturers.length)];
                product = product.replace("'MediRx'", "'" + customManufacturer + "'");
            }

            if (vaccineIndices.contains(i + 1)) {
                product = product.replace("false,", "true,");
            }

            System.out.print(product);
            if (i < products.length - 1) {
                System.out.println(",");
            } else {
                System.out.println(";");
            }
        }
    }
}
