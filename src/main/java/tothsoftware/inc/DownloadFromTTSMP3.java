package tothsoftware.inc;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DownloadFromTTSMP3 {

    public static ArrayList<String> russian;
    public static ArrayList<String> hungarian;
    public static ArrayList<String> wordsOut;

    public static ArrayList<Path> sourcePathList;
    public static ArrayList<Path> targetPathList;

    public static String downloadPath;
    public static String inputFilePath;
    public static String outputFilePath;
    public static String outputDir;

    public static String inputFileName;

    public static void main(String[] args) throws InterruptedException, AWTException, UnsupportedFlavorException, IOException {
        //testGoogleSearch();


        sourcePathList = new ArrayList<Path>();
        targetPathList = new ArrayList<Path>();
        russian = new ArrayList<String>();
        hungarian = new ArrayList<String>();
        wordsOut = new ArrayList<String>();

        initUI();

        long startTime = System.nanoTime();

        readInput();
        downloadRussianWords();
        createSummaryFile();
        renameAudioFiles();

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        double billion = 1000000000D;
        double sixty = 60D;
        System.out.println("Running time in minutes: "+(totalTime/billion/sixty));
    }

    public static void chooseInputFilePath() {
        //JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
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
        //JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        JFileChooser jfc = new JFileChooser("c:\\LANGUAGE\\RUSSIAN\\");
        jfc.setDialogTitle("Choose a directory to save your files (the audio files and the smart text file output): ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                System.out.println("You selected the following output directory: " + jfc.getSelectedFile());
                outputDir = jfc.getSelectedFile().toString();
                downloadPath = jfc.getSelectedFile().toString() + "\\";
                outputFilePath = jfc.getSelectedFile().toString() + inputFileName + "_out.txt";
                System.out.println("Your output file will be called: " + outputFilePath);
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

    public static void renameAudioFiles() {
        try {
            for (int i = 0; i < sourcePathList.size(); i++) {
                Files.move(sourcePathList.get(i), targetPathList.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createSummaryFile() {
        try {
            FileOutputStream file;
            if (outputFilePath == null || outputFilePath == "") {
                file = new FileOutputStream("C:/Users/Benedek/Desktop/_test_4_out.txt");
            } else {
                file = new FileOutputStream(outputFilePath);
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, Charset.forName("UTF-8"));
            BufferedWriter bw = new BufferedWriter(outputStreamWriter);

            String downloadsDirPath;
            String home = System.getProperty("user.home");
            downloadsDirPath = home + "/Downloads/";

            if (downloadPath == null || downloadPath == "") {
                downloadPath = "c:\\Users\\Benedek\\Downloads\\";
            }

            String sourceFilePath, targetFilePath, russianWord, sourceFileName, targetFileName;

            for (int i = 0; i < russian.size(); i++) {
                russianWord = russian.get(i);

                sourceFileName = russianWord.replace("/", "_").replace("?", "_");
                if(sourceFileName.length() == 45) {
                    sourceFileName = sourceFileName.substring(0, 44);
                }
                sourceFilePath = downloadsDirPath + sourceFileName + ".mp3";
                targetFileName = russianWord.replace(" ", "-").replace("?", "_").replace("/", "_");
                if(sourceFileName.length() == 45) {
                    targetFileName = targetFileName.substring(0, 44);
                }
                targetFilePath = outputDir + "/" + targetFileName + ".mp3";

                sourcePathList.add(Paths.get(sourceFilePath.replace("\\", "/")));
                targetPathList.add(Paths.get(targetFilePath.replace("\\", "/")));

                wordsOut.add(russianWord + "[sound:" + targetFileName + ".mp3];" + hungarian.get(i) + "\n");
                bw.write(wordsOut.get(i));
            }
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < wordsOut.size(); i++) {
            System.out.println(wordsOut.get(i));
        }
    }

    public static void readInput() {
        try {
            FileInputStream file;
            if (inputFilePath == null || inputFilePath == "") {
                file = new FileInputStream("C:/Users/Benedek/Desktop/_test_4.txt");
            } else {
                file = new FileInputStream(inputFilePath);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(file, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = br.readLine();
            String currentWord;
            String currentDefinition;
            //Path currentPath;
            while (line != null) {
                //System.out.println(line);
                currentWord = line.substring(0, line.indexOf(","));
                currentDefinition = line.substring(line.indexOf(",") + 1, line.length());
                System.out.println(currentDefinition);
                //currentPath = Paths.get(line.substring(line.indexOf("[")+7, line.indexOf("]")));
                byte bytes[] = currentWord.getBytes("UTF-8");
                String value = new String(bytes, "UTF-8");
                System.out.println(value);
                //System.out.println(currentPath.toString());
                if (!russian.contains(currentWord)) {
                    russian.add(currentWord);
                    hungarian.add(currentDefinition);
                }
                //pathList.add(currentPath);
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadRussianWords() throws InterruptedException, AWTException, UnsupportedFlavorException, IOException {
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get("https://ttsmp3.com/login");

        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        username.sendKeys("tothgy74@gmail.com");

        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        password.sendKeys("Q8dNv4pq");

        WebElement submit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='submit']")));
        submit.click();

        //driver.get("https://ttsmp3.com/text-to-speech/Russian/");


        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sprachwahl")));

        Select select = new Select(driver.findElement(By.id("sprachwahl")));
        select.selectByValue("Tatyana");

        Thread.sleep(3000);

        File newFile;
        for (int i = 0; i < russian.size(); i++) {

            WebElement textInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("voicetext")));
            textInputField.clear();
            textInputField.sendKeys(russian.get(i));

            WebElement downLoadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("downloadenbutton")));
            downLoadButton.click();

            Thread.sleep(3000);

            newFile = getLastModified("c:\\Users\\GYULA_TOTH\\Downloads\\");
            newFile.renameTo(new File(outputDir + "\\" + russian.get(i).replace(" ", "-") + ".mp3"));

        }

        Thread.sleep(3000);
        driver.quit();
    }

    public static File getLastModified(String directoryFilePath)
    {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles();
        long lastModifiedTime = Long.MIN_VALUE;
        File chosenFile = null;

        if (files != null) {
            for (File file : files) {
                if (file.lastModified() > lastModifiedTime) {
                    chosenFile = file;
                    lastModifiedTime = file.lastModified();
                }
            }
        }

        return chosenFile;
    }
}