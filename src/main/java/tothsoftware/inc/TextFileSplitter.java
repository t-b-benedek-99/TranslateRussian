package tothsoftware.inc;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileSplitter {

    public static String inputFilePath;
    public static String outputDir;
    public static String inputFileName;

    private final static String NEWLINE = System.getProperty("line.separator");

    public static void main(String[] args) {

        int lines = 100;

        try {
            chooseInputFilePath();
            chooseOutputFilePath();
            readFileData(inputFilePath, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void chooseInputFilePath() {
        //JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        JFileChooser jfc = new JFileChooser("c:\\LANGUAGE\\RUSSIAN\\5000\\voices\\");
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
        //JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        JFileChooser jfc = new JFileChooser("c:\\LANGUAGE\\RUSSIAN\\5000\\");
        jfc.setDialogTitle("Choose a directory to save your files (the audio files and the smart text file output): ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                System.out.println("You selected the following output directory: " + jfc.getSelectedFile());
                outputDir = jfc.getSelectedFile().toString();
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "You have to choose a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "You haven't choosen a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void readFileData(String filename, int lines) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    filename));
            StringBuffer stringBuffer = new StringBuffer();

            String line;
            int i = 0;
            int counter = 1;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append(NEWLINE);
                i++;
                if (i >= lines) {
                    createFile(stringBuffer, outputDir + inputFileName+ "_" + counter + ".txt");
                    stringBuffer = new StringBuffer();
                    i = 0;
                    counter++;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new IOException("read file error " + filename);
        }
    }

    private static void createFile(StringBuffer stringBuffer, String filename) {
        File file = new File(filename);
        FileWriter output = null;
        try {
            output = new FileWriter(file);
            output.write(stringBuffer.toString());
            System.out.println("file " + filename + " written");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                output.close();
            } catch (IOException e) {
            }
        }
    }

}