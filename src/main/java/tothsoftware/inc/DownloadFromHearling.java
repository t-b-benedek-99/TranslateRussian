package tothsoftware.inc;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DownloadFromHearling {

    public static ArrayList<String> russian;
    public static ArrayList<String> hungarian;
    //public static ArrayList<String> wordsOut;

    public static ArrayList<Path> sourcePathList;
    public static ArrayList<Path> targetPathList;

    public static String downloadPath;
    public static String inputFilePath;
    public static String outputFilePath;
    public static String outputDir;

    public static String inputFileName;

    public static JCheckBox russianCheckBox = new JCheckBox("Russian");
    public static JCheckBox hungarianCheckBox = new JCheckBox("Hungarian");
    public static JDialog dialog = new JDialog();
    public static JButton button = new JButton("OK");

    public static void main(String[] args) throws InterruptedException, AWTException, UnsupportedFlavorException, IOException {
        //testGoogleSearch();

        sourcePathList = new ArrayList<Path>();
        targetPathList = new ArrayList<Path>();
        russian = new ArrayList<String>();
        hungarian = new ArrayList<String>();
        //wordsOut = new ArrayList<String>();

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

    public static void selectOptions() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }

        dialog.setLayout(new FlowLayout());

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.dispose();
            }
        });

        dialog.add(russianCheckBox);
        dialog.add(hungarianCheckBox);
        dialog.add(button);
        dialog.setSize(480, 150);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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

        selectOptions();
        chooseInputFilePath();
        chooseOutputFilePath();
    }

    public static void renameAudioFiles() {
        try {
            for (int i = 0; i < sourcePathList.size(); i++) {
                System.out.println("From: " + sourcePathList.get(i));
                System.out.println("To: " + targetPathList.get(i));
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
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, Charset.forName("UTF-8"));
            //BufferedWriter bw = new BufferedWriter(outputStreamWriter);

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
                //sourceFilePath = downloadsDirPath + sourceFileName + ".mp3";
                sourceFilePath = downloadsDirPath + sourceFileName + ".mp3";

                //targetFileName = russianWord.replace(" ", "-").replace("?", "_").replace("/", "_");
                //if(sourceFileName.length() == 45) {
                //    targetFileName = targetFileName.substring(0, 44);
                //}
                //targetFilePath = outputDir + "/" + targetFileName + ".mp3";
                targetFileName = sourceFileName;
                targetFilePath = outputDir + "/" + sourceFileName + ".mp3";

                sourcePathList.add(Paths.get(sourceFilePath.replace("\\", "/")));
                targetPathList.add(Paths.get(targetFilePath.replace("\\", "/")));

                //wordsOut.add(russianWord + "[sound:" + targetFileName + ".mp3];" + hungarian.get(i) + "\n");
                //bw.write(wordsOut.get(i));
            }
            //bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //for (int i = 0; i < wordsOut.size(); i++) {
        //    System.out.println(wordsOut.get(i));
        //}
    }

    /*public static void readInput() {
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
                currentWord = line.substring(0, line.indexOf("["));
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
    }*/


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
            while (line != null) {
                byte bytes[] = line.getBytes("UTF-8");
                String value = new String(bytes, "UTF-8");
                if (!russian.contains(value)) {
                    russian.add(value);
                }
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
        driver.get("https://hearling.com/clips");
        Thread.sleep(4000);
        //WebElement emailField = driver.findElement(By.name("email"));
        // toth.balint.benedek@gmail.com
        WebElement username = driver.findElement(By.name("email"));
        //((JavascriptExecutor)driver).executeAsyncScript("arguments[0].value='toth.balint.benedek@gmail.com'",username);
        String usernameOfMineReal = "tothgy74@gmail.com";
        StringSelection stringSelection = new StringSelection(usernameOfMineReal);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        String originalClipboardContent = "";
        StringSelection stringSelectionOriginal = new StringSelection(originalClipboardContent);

        try {
            originalClipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);
            stringSelectionOriginal = new StringSelection(originalClipboardContent);
        } catch (Exception e) {
        }

        clipboard.setContents(stringSelection, stringSelection);
        username.click();

        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        Thread.sleep(1000);
        clipboard.setContents(stringSelectionOriginal, stringSelectionOriginal);

        WebDriverWait wait = new WebDriverWait(driver, 60);

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        passwordField.sendKeys("Zvs5BYdbqF4pGg3");

        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button--stretch")));
        loginButton.click();

        Thread.sleep(5000);

        for (int i = 0; i < russian.size(); i++) {

            WebElement newClipButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button")));
            newClipButton.click();

            if (i == 0) {

                if(russianCheckBox.isSelected()) {
                    WebElement russianButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/main/section/div/ul/li[25]")));
                    russianButton.click();
                    WebElement theCorrectVoice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/section/div[3]/ul/li[8]")));
                    theCorrectVoice.click();
                }
                else if (hungarianCheckBox.isSelected()) {
                    WebElement russianButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/main/section/div/ul/li[15]")));
                    russianButton.click();
                    WebElement theCorrectVoice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/section/div[3]/ul/li[2]")));
                    theCorrectVoice.click();
                }

            }

            WebElement nextButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/section/div[4]/button")));
            nextButton.click();

            WebElement textInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/div/div[2]/div/div[2]/textarea")));
            textInputField.sendKeys(russian.get(i));

            WebElement generateVoiceButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/div/div[1]/section/div[3]/button")));
            generateVoiceButton.click();

            WebElement downloadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/main/section/div[1]/ul/li/div/div[3]/a")));
            Thread.sleep(1000);
            downloadButton.click();

            WebElement mainPageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/header/div/a")));
            Thread.sleep(1000);
            mainPageButton.click();

            Thread.sleep(2000);
        }

        Thread.sleep(15000);
        driver.quit();
    }
}