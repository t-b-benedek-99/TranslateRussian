package tothsoftware.inc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

public class ResolveTranslation {

    public static ArrayList<String> words = new ArrayList<String>();;
    public static ArrayList<String> wordsExcluded = new ArrayList<String>();;
    public static ArrayList<String> hungarianMeaning = new ArrayList<String>();;
    public static ArrayList<String> wordsDetails = new ArrayList<String>();
    public static ArrayList<String> wordsWithIssues = new ArrayList<String>();
    public static ArrayList<String> wordsOut = new ArrayList<String>();
    public static JCheckBox split = new JCheckBox("Split at \"-\"?");
    public static JDialog dialog = new JDialog();
    public static JButton button = new JButton("OK");

    public static String inputFileName;
    public static String inputFilePath;
    public static String outputFilePath;
    public static String outputFilePathIssues;
    public static String outputFilePathExcluded;

    public static void main(String[] args) throws InterruptedException {

        initUI();

        long startTime = System.nanoTime();

        readInput();
        getTranslation();
        assembleOutput();

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

        dialog.add(split);
        dialog.add(button);
        dialog.setSize(480, 150);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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
                outputFilePathIssues = jfc.getSelectedFile().toString() + inputFileName + "_issues.txt";
                outputFilePathExcluded = jfc.getSelectedFile().toString() + inputFileName + "_excluded.txt";
                System.out.println("Your output file will be called: " + outputFilePath);
                System.out.println("Your output file for problematic hungarianName: " + outputFilePathIssues);
                System.out.println("Your output file for excluded hungarianName: " + outputFilePathIssues);
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
                currentWord = line.substring(0, line.indexOf("["));
                currentWord.trim();
                if (!currentWord.contains(" ")) {
                    if(split.isSelected()) {
                        if (currentWord.contains("-")) {
                            String firstPart = currentWord.substring(0, currentWord.indexOf("-"));
                            String secondPart = currentWord.substring(currentWord.indexOf("-") + 1, currentWord.length());
                            words.add(firstPart);
                            words.add(secondPart);
                        } else {
                            words.add(currentWord);
                        }
                    }
                    else {
                        words.add(currentWord);
                    }
                } else {
                    //wordsExcluded.add(currentWord);
                    wordsExcluded.add(line);
                }

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

    public static void getTranslation() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.get("http://www.orosz-szotar.hu");
        WebDriverWait wait = new WebDriverWait(driver, 60);

        for (int i = 0; i < words.size(); i++) {
            try {

                System.out.print(words.get(i) + ": ");

                try {
                    WebElement strictnessButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='strictness_match_full']")));
                    strictnessButton.click();
                } catch (Exception r) {
                    Thread.sleep(5000);
                }

                WebElement textInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='word']")));
                textInputField.sendKeys(words.get(i));

                WebElement submitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"searchbutton\"]")));
                submitButton.click();

                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[@class='result']/a")),
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='noresult']/tbody/tr/td/b[contains(text(), 'Nincs ilyen kifejezés a szótárban, vagy ki van szűrve, mert vulgáris')]")))
                );

                if (driver.getPageSource().contains("Nincs ilyen kifejezés a szótárban, vagy ki van szűrve, mert vulgáris")) {
                    System.out.println(words.get(i) + ": Nincs ilyen kifejezés a szótárban, vagy ki van szűrve, mert vulgáris");
                    wordsWithIssues.add(words.get(i));
                    words.remove(i);
                    i--;
                } else {
                    String hungarianMeanings = getTheHungarianMeanings(driver);
                    String wordDetails = getTheWordDetails(driver);
                    System.out.println(words.get(i) + wordDetails + ";" + hungarianMeanings);
                    hungarianMeaning.add(hungarianMeanings);
                    wordsDetails.add(wordDetails);
                }

                WebElement reloadButton = driver.findElement(By.xpath("//*[@id=\"page\"]/div[1]/div[1]/a"));
                reloadButton.click();

            } catch (NoSuchElementException e) {
                System.out.println("No results: " + words.get(i));
                wordsWithIssues.add(words.get(i));
                words.remove(i);
                i--;
            }
        }
        driver.quit();
    }

    public static String getTheHungarianMeanings(WebDriver driver) {
        String resultText = "";
        int x = 1;
        while (x < 11) {
            if (x == 1) {
                WebElement result = driver.findElement(By.xpath("//tr[@class='rowcolor1'][" + x + "]/td[@class='result'][2]/a[1]"));
                resultText = result.getText();
            } else {
                List<WebElement> xpath = driver.findElements(By.xpath("//tr[@class='rowcolor1'][" + x + "]/td[@class='result']"));
                int xpathCount = xpath.size();

                if (xpathCount == 1) {
                    WebElement result = driver.findElement(By.xpath("//tr[@class='rowcolor1'][" + x + "]/td[@class='result']/a[1]"));
                    resultText = resultText + ", " + result.getText();
                }
            }
            x++;
        }
        return resultText;
    }

    public static String getTheWordDetails(WebDriver driver) {
        List<WebElement> xpathBefejezett = driver.findElements(By.xpath("//tr[@class='rowcolor1'][\"+x+\"]/td[@class='result'][1]/b[@title='befejezett']"));
        int xpathBefejezettCount = xpathBefejezett.size();

        List<WebElement> xpathFolyamatos = driver.findElements(By.xpath("//tr[@class='rowcolor1'][\"+x+\"]/td[@class='result'][1]/b[@title='folyamatos']"));
        int xpathFolyamatosCount = xpathFolyamatos.size();

        List<WebElement> xpathFonev = driver.findElements(By.xpath("//tr[@class='rowcolor1'][\"+x+\"]/td[@class='result'][1]/i[contains(text(), 'Főnév')]"));
        int xpathFonevCount = xpathFonev.size();

        List<WebElement> xpathNonem = driver.findElements(By.xpath("//tr[@class='rowcolor1'][\"+x+\"]/td[@class='result'][1]/img[@title='nőnem']"));
        int xpathNonemCount = xpathNonem.size();

        List<WebElement> xpathHimnem = driver.findElements(By.xpath("//tr[@class='rowcolor1'][\"+x+\"]/td[@class='result'][1]/img[@title='hímnem']"));
        int xpathHimnemCount = xpathHimnem.size();

        List<WebElement> xpathMelleknev = driver.findElements(By.xpath("//tr[@class='rowcolor1'][\"+x+\"]/td[@class='result'][1]/i[contains(text(), 'Melléknév')]"));
        int xpathMelleknevCount = xpathMelleknev.size();

        List<WebElement> xpathHatarozoszo = driver.findElements(By.xpath("//tr[@class='rowcolor1'][\"+x+\"]/td[@class='result'][1]/i[contains(text(), 'Határozó')]"));
        int xpathHatarozoszoCount = xpathHatarozoszo.size();

        String details = "";
        if (xpathBefejezettCount > 0) {
            details = "(ige: b)";
        }
        if (xpathFolyamatosCount > 0) {
            details = "(ige: f)";
        }
        if (xpathFonevCount > 0 && xpathNonemCount > 0) {
            details = "(fn: n)";
        }
        if (xpathFonevCount > 0 && xpathHimnemCount > 0) {
            details = "(fn: h)";
        }
        if (xpathMelleknevCount > 0) {
            details = "(mn)";
        }
        if (xpathHatarozoszoCount > 0) {
            details = "(hat)";
        }
        return details;
    }

    public static void assembleOutput() {
        for (int i = 0; i < words.size(); i++) {
            wordsOut.add(words.get(i) + "[sound:" + words.get(i).trim() + ".mp3];" + hungarianMeaning.get(i).trim() + " " + wordsDetails.get(i));
        }
        writeToFile(outputFilePath, wordsOut);
        writeToFile(outputFilePathIssues, wordsWithIssues);
        writeToFile(outputFilePathExcluded, wordsExcluded);
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