package com.winequality.utils;

import com.winequality.model.Wine;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CSVLoader {
    private static int badRecords = 0;
    public static int getBadRecordsCount() {
        return badRecords;
    }
    public static List<Wine> loadWines(String filePath) throws IOException {
        List<Wine> wines = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {
            for (CSVRecord record : csvParser) {
                try {
                    boolean hasEmptyValue = false;
                    for (String value : record) {
                        if (value == null || value.trim().isEmpty()) {
                            hasEmptyValue = true;
                            break;
                        }
                    }
                    if (hasEmptyValue) {
                        badRecords++;
                        continue;
                    }
                    double fixedAcidity = Double.parseDouble(record.get("fixed acidity"));
                    if (fixedAcidity < 4 || fixedAcidity > 16) {
                        badRecords++;
                        continue;
                    }
                    double volatileAcidity = Double.parseDouble(record.get("volatile acidity"));
                    if (volatileAcidity < 0.1 || volatileAcidity > 2.0) {
                        badRecords++;
                        continue;
                    }
                    int quality = Integer.parseInt(record.get("quality"));
                    if (quality < 1 || quality > 10) { // Quality should be 1-10
                        badRecords++;
                        continue;
                    }
                    Wine wine = new Wine(
                            Double.parseDouble(record.get("fixed acidity")),
                            Double.parseDouble(record.get("volatile acidity")),
                            Double.parseDouble(record.get("citric acid")),
                            Double.parseDouble(record.get("residual sugar")),
                            Double.parseDouble(record.get("chlorides")),
                            Double.parseDouble(record.get("free sulfur dioxide")),
                            Double.parseDouble(record.get("total sulfur dioxide")),
                            Double.parseDouble(record.get("density")),
                            Double.parseDouble(record.get("pH")),
                            Double.parseDouble(record.get("sulphates")),
                            Double.parseDouble(record.get("alcohol")),
                            Integer.parseInt(record.get("quality"))
                    );
                    wines.add(wine);
                } catch (NumberFormatException e) {
                    badRecords++;
                }
            }
            return wines;
        }
    }
}