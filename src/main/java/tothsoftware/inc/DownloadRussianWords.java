package tothsoftware.inc;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DownloadRussianWords {

    public static ArrayList<String> russianWords;
    public static ArrayList<String> englishWords;
    public static String outputFilePath;
    public static String memrisePage;
    public static JDialog dialog = new JDialog();
    public static JLabel myLabel = new JLabel("Please choose the Memrise page:");
    public static JTextField textField = new JTextField("", 20);
    public static JButton button = new JButton("OK");
    public static JFileChooser jfc = new JFileChooser();

    public static void main(String[] args) throws InterruptedException {

        russianWords = new ArrayList<String>();
        englishWords = new ArrayList<String>();

        chooseMemrisePage();
        chooseOutputFilePath();
        long startTime = System.nanoTime();
        downloadWords();

        for (int i = 0; russianWords.size() > i; i++) {
            System.out.println(russianWords.get(i) + "" + englishWords.get(i));
        }

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        double billion = 1000000000D;
        double sixty = 60D;
        System.out.println("Running time in minutes: "+(totalTime/billion/sixty));

    }

    public static void chooseMemrisePage() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }

        dialog.setLayout(new FlowLayout());
        textField.setColumns(37);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(dialog,"You entered text:\n" + textField.getText());
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                String content = textField.getText();
                if (!content.equals("")) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
        });

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                memrisePage = textField.getText();
                dialog.dispose();
            }
        });

        dialog.add(myLabel);
        dialog.add(textField);
        dialog.add(button);

        dialog.setSize(480, 150);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void chooseOutputFilePath() {
        JFileChooser jfc = new JFileChooser("c:\\LANGUAGE\\RUSSIAN\\5000\\");
        jfc.setDialogTitle("Choose a directory to save your files (the audio files and the smart text file output): ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                System.out.println("You selected the following output directory: " + jfc.getSelectedFile());
                outputFilePath = jfc.getSelectedFile().toString() + "\\words.txt";
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "You have to choose a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "You haven't choosen a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void downloadWords() throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(memrisePage);
        WebDriverWait wait = new WebDriverWait(driver, 60);

        int pageCount = 1;
        while(true) {
            processPage(driver, wait);
            System.out.print("Page " + pageCount + ". has been processed. ");
            System.out.println("Number of Russian words: " + russianWords.size() + ".");
            List<WebElement> nextButton = driver.findElements(By.xpath("//a[@class='level-nav level-nav-next']"));
            int finish = nextButton.size();
            if(finish > 0) {
                driver.findElement(By.xpath("//a[@class='level-nav level-nav-next']")).click();
            }
            else {
                break;
            }
            pageCount++;
        }

        writeToFile(outputFilePath, russianWords, englishWords);

        driver.quit();
    }

    public static void processPage(WebDriver driver, WebDriverWait wait) throws InterruptedException {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='thing text-text']")));

        for (int i = 1; i < driver.findElements(By.xpath("//div[@class='thing text-text']")).size() + 1; i++) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='thing text-text']["+i+"]/div[3]/div[@class='text']")));
            WebElement result = driver.findElement(By.xpath("//div[@class='thing text-text']["+i+"]/div[3]/div[@class='text']"));
            russianWords.add(result.getText());

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='thing text-text']["+i+"]/div[4]/div[@class='text']")));
            result = driver.findElement(By.xpath("//div[@class='thing text-text']["+i+"]/div[4]/div[@class='text']"));
            englishWords.add(result.getText());
        }
    }

    public static void writeToFile(String filePath, ArrayList<String> russianWords, ArrayList<String> englishWords) {
        try {
            FileOutputStream file;
            file = new FileOutputStream(filePath);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, Charset.forName("UTF-8"));
            BufferedWriter bw = new BufferedWriter(outputStreamWriter);
            for (int i = 0; i < russianWords.size(); i++) {
                bw.write(russianWords.get(i) + "," + englishWords.get(i) + "\n");
            }
            bw.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
