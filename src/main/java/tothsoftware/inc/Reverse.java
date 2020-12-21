package tothsoftware.inc;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.*;

public class Reverse {

    public static ArrayList<String> hungarianPart = new ArrayList<String>();
    public static ArrayList<String> russianPart = new ArrayList<String>();
    public static ArrayList<String> originalParts = new ArrayList<String>();
    public static ArrayList<String> wordsOut = new ArrayList<String>();

    public static String inputFileName;
    public static String inputFilePath;
    public static String outputFilePath;

    public static void main(String[] args) throws InterruptedException {

        initUI();

        long startTime = System.nanoTime();

        readInput();
        assembleOutput();

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        double billion = 1000000000D;
        double sixty = 60D;
        System.out.println("Running time in minutes: "+(totalTime/billion/sixty));

    }

    public static void chooseInputFilePath() {
        JFileChooser jfc = new JFileChooser("C:\\Users\\GYULA_TOTH\\Google Drive\\LANGUAGE\\RUSSIAN");
        jfc.setDialogTitle("Choose your file input text file: ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isFile() && jfc.getSelectedFile().getName().endsWith(".txt")) {
                System.out.println("You selected the following input file: " + jfc.getSelectedFile());
                inputFilePath = jfc.getSelectedFile().toString();
                inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf("\\"), inputFilePath.length() - 4);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "The choosen file is not a text file, it has to be type of: .txt\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "You haven't choosen a file\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void chooseOutputFilePath() {
        JFileChooser jfc = new JFileChooser("C:\\Users\\GYULA_TOTH\\Google Drive\\LANGUAGE\\RUSSIAN");
        jfc.setDialogTitle("Choose a directory to save your files (the audio files and the smart text file output): ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                System.out.println("You selected the following output directory: " + jfc.getSelectedFile());
                String newFileName = inputFileName.substring(0, inputFileName.length()-2);
                outputFilePath = jfc.getSelectedFile().toString() + newFileName + "hu.txt";
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "You have to choose a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "You haven't choosen a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void initUI() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }

        chooseInputFilePath();
        chooseOutputFilePath();
    }

    public static void readInput() {
        try {
            FileInputStream file;
            if (inputFilePath == null || inputFilePath == "") {
                throw new Exception();
            } else {
                file = new FileInputStream(inputFilePath);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(file, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                russianPart.add(line.substring(0, line.indexOf(";")));
                hungarianPart.add(line.substring(line.indexOf(";")+1));
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(new JFrame(), "Oops: Some error occured, no input file is specified in: readInput() method...\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);

            e.printStackTrace();

            System.exit(0);
        }
    }

    public static void assembleOutput() {
        for (int i = 0; i < russianPart.size(); i++) {
            wordsOut.add(hungarianPart.get(i) + ";" + russianPart.get(i));
        }
        writeToFile(outputFilePath, wordsOut);
    }

    public static void writeToFile(String filePath, ArrayList<String> collection) {
        try {
            FileOutputStream file;
            file = new FileOutputStream(filePath);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, Charset.forName("UTF-8"));
            BufferedWriter bw = new BufferedWriter(outputStreamWriter);
            for (int i = 0; i < collection.size(); i++) {
                bw.write(collection.get(i) + "\n");
            }
            bw.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


}