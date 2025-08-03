package com.winequality.main;

import com.winequality.model.Wine;
import com.winequality.utils.CSVLoader;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.lazy.IBk;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // To read the data from the CSV file
            List<Wine> wines = CSVLoader.loadWines("src/main/resources/WineQT.csv");
            System.out.println("Loaded " + wines.size() + " wine records");
            // To show the cleaned data
            System.out.println("\nData Cleaning Report:");
            System.out.println("Total records checked: " + (wines.size() + CSVLoader.getBadRecordsCount()));
            System.out.println("Valid records kept: " + wines.size());
            System.out.println("Invalid records removed: " + CSVLoader.getBadRecordsCount());
            // To create a WEKA dataset
            Instances dataset = createWekaDataset(wines);
            // To train the models (RandomForest, k-NN and Neural Network)
            evaluateClassifiers(dataset);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Instances createWekaDataset(List<Wine> wines) {
        // Define the numeric attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("fixed_acidity"));
        attributes.add(new Attribute("volatile_acidity"));
        attributes.add(new Attribute("citric_acid"));
        attributes.add(new Attribute("residual_sugar"));
        attributes.add(new Attribute("chlorides"));
        attributes.add(new Attribute("free_sulfur_dioxide"));
        attributes.add(new Attribute("total_sulfur_dioxide"));
        attributes.add(new Attribute("density"));
        attributes.add(new Attribute("pH"));
        attributes.add(new Attribute("sulphates"));
        attributes.add(new Attribute("alcohol"));
        attributes.add(new Attribute("quality"));

        // Create the dataset
        Instances dataset = new Instances("WineQuality", attributes, wines.size());
        dataset.setClassIndex(dataset.numAttributes() - 1);  // Set the target

        // We add the data
        for (Wine wine : wines) {
            DenseInstance instance = new DenseInstance(12);
            instance.setValue(0, wine.getFixedAcidity());
            instance.setValue(1, wine.getVolatileAcidity());
            instance.setValue(2, wine.getCitricAcid());
            instance.setValue(3, wine.getResidualSugar());
            instance.setValue(4, wine.getChlorides());
            instance.setValue(5, wine.getFreeSulfurDioxide());
            instance.setValue(6, wine.getTotalSulfurDioxide());
            instance.setValue(7, wine.getDensity());
            instance.setValue(8, wine.getPH());
            instance.setValue(9, wine.getSulphates());
            instance.setValue(10, wine.getAlcohol());
            instance.setValue(11, wine.getQuality());
            dataset.add(instance);
        }
        return dataset;
    }

    private static void evaluateClassifiers(Instances dataset) throws Exception {
        Classifier[] classifiers = {
                new RandomForest(),
                new IBk(5),         // k-NN method
                new MultilayerPerceptron()  // Neural Network method
        };
        // For printing
        for (Classifier classifier : classifiers) {
                System.out.println("\nEvaluating " + classifier.getClass().getSimpleName() + ": ");

            Evaluation eval = new Evaluation(dataset);
            eval.crossValidateModel(classifier, dataset, 10, new Random(1));  // 10-fold CV

            // Print the results up to 3 decimal places so it is easier to understand
            System.out.println(String.format("Mean Absolute Error (MAE): %.3f", eval.meanAbsoluteError()));
            System.out.println(String.format("Root Mean Squared Error (RMSE): %.3f", eval.rootMeanSquaredError()));
            System.out.println(String.format("Correlation Coefficient (R): %.3f", eval.correlationCoefficient()));
        }
    }
}