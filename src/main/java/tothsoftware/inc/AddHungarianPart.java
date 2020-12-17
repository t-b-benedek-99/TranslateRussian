package tothsoftware.inc;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.*;

public class AddHungarianPart {

    public static ArrayList<String> hungarianName = new ArrayList<String>();
    public static ArrayList<String> russianName = new ArrayList<String>();
    public static ArrayList<String> originalParts = new ArrayList<String>();
    public static ArrayList<String> wordsOut = new ArrayList<String>();

    public static String inputFileName;
    public static String inputFilePath;
    public static String outputFilePath;
    public static String outputFilePathHungarianMP3;
    public static String outputFilePathRussianMP3;

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
        JFileChooser jfc = new JFileChooser("c:\\LANGUAGE\\RUSSIAN\\");
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
        JFileChooser jfc = new JFileChooser("c:\\LANGUAGE\\RUSSIAN\\");
        jfc.setDialogTitle("Choose a directory to save your files (the audio files and the smart text file output): ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                System.out.println("You selected the following output directory: " + jfc.getSelectedFile());
                outputFilePath = jfc.getSelectedFile().toString() + inputFileName + "_out.txt";
                outputFilePathHungarianMP3 = jfc.getSelectedFile().toString() + inputFileName + "_hungarian.txt";
                outputFilePathRussianMP3 = jfc.getSelectedFile().toString() + inputFileName + "_russian.txt";
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
            String currentWord;
            while (line != null) {
                line = line.trim();
                originalParts.add(line);
                currentWord = line.substring(line.indexOf(";")+1);
                if (currentWord.contains(" ")) {
                    currentWord = currentWord.replace(" ", "-");
                    hungarianName.add(currentWord);

                } else {
                    hungarianName.add(currentWord);
                }
                russianName.add(line.substring(line.indexOf("sound:") + 6, line.indexOf(".mp3")));
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
        for (int i = 0; i < hungarianName.size(); i++) {
            wordsOut.add(originalParts.get(i) + "[sound:" + hungarianName.get(i) + ".mp3]");
        }
        writeToFile(outputFilePath, wordsOut);
        writeToFile(outputFilePathRussianMP3, russianName);
        writeToFile(outputFilePathHungarianMP3, hungarianName);
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