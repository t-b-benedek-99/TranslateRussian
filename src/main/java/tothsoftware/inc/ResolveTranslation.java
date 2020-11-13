package tothsoftware.inc;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ResolveTranslation {
	
	public static ArrayList<String> words;
	public static ArrayList<String> wordsDefinitions;
	public static ArrayList<String> wordsOut;
	
	public static String inputFilePath;
	public static String outputFilePath;

	public static String inputFileName;
	
	public static void main(String []args) throws InterruptedException {
		
		words = new ArrayList<String>();
		wordsDefinitions = new ArrayList<String>();
		wordsOut = new ArrayList<String>();
		
		initUI();
		readInput();
		getTranslation();
		assembleOutput();
	}
	
	public static void chooseInputFilePath() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose your file input text file: ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isFile() && jfc.getSelectedFile().getName().endsWith(".txt")) {
                System.out.println("You selected the following input file: " + jfc.getSelectedFile());
                inputFilePath = jfc.getSelectedFile().toString();
                inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf("\\"), inputFilePath.length()-4);
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
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a directory to save your files (the audio files and the smart text file output): ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                System.out.println("You selected the following output directory: " + jfc.getSelectedFile());
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
		
		chooseInputFilePath();
		
		chooseOutputFilePath();
	}
	
	public static void readInput() {
		try {
		FileInputStream file;
		if(inputFilePath == null || inputFilePath == "") {
			throw new NoInputFilePathDeclaredException();
		} else {
			file = new FileInputStream(inputFilePath);
		}
		InputStreamReader inputStreamReader = new InputStreamReader(file, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(inputStreamReader);
		String line = br.readLine();
		String currentWord;
		while(line != null) {
			currentWord = line.substring(0, line.indexOf(","));
			byte bytes[] = currentWord.getBytes("UTF-8"); 
			String value = new String(bytes, "UTF-8"); 
			System.out.println(value);
			words.add(currentWord);
			line = br.readLine();
		}
		br.close();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (NoInputFilePathDeclaredException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Oops: Some error occured, no input file is specified in: readInput() method...\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
        	System.exit(0);
		}
	}
	

	public static void getTranslation() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
		 
		 WebDriver driver = new ChromeDriver();
		 driver.get("http://www.orosz-szotar.hu");
		 		 
		 for(int i = 0; i < words.size(); i++) {
			try {
				WebElement textInputField = driver.findElement(By.xpath("//*[@id=\"word\"]"));
				textInputField.sendKeys(words.get(i));
				Thread.sleep(4000);
				WebElement submitButton = driver.findElement(By.xpath("//*[@id=\"searchbutton\"]"));
				submitButton.click();
				Thread.sleep(12000);
				WebElement result = driver.findElement(By.xpath("//td[@class='result'][2]/a[1]"));
				String resultText = result.getText();
				System.out.println(resultText);
				wordsDefinitions.add(resultText);
				WebElement reloadButton = driver.findElement(By.xpath("//*[@id=\"page\"]/div[1]/div[1]/a"));
				reloadButton.click();
				Thread.sleep(8000);
			 } catch(NoSuchElementException e) {								
				System.out.println(e.getClass() + e.getMessage());
				WebElement reloadButton = driver.findElement(By.xpath("//*[@id='page']/div[1]/div[1]/a"));
				try {											
					WebElement result = driver.findElement(By.xpath("//td[@class='result'][2]/a[1]"));
					String resultText = result.getText();
					wordsDefinitions.add(resultText);
					String currentRecommendedWord = "";
					currentRecommendedWord = driver.findElement(By.xpath("//*[@id=\"result\"]/table/tbody/tr[2]/td[1]/a[1]")).getText();
					wordsOut.add(i, currentRecommendedWord);
					
				} catch(NoSuchElementException ex) {
					System.out.println(ex.getClass() + ex.getMessage());
					System.out.println("a words(i), amit kiszedünk: " + words.get(i));
					//words.remove(i);					
				}
				reloadButton.click();
				Thread.sleep(8000);
			 }
		 }		 
		 driver.quit();
	}
	
	
	/*
	public static void getTranslation() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
		 
		 WebDriver driver = new ChromeDriver();
		 driver.get("http://www.orosz-szotar.hu");
		 		 
		 for(int i = 0; i < words.size(); i++) {
			try {
				WebElement textInputField = driver.findElement(By.xpath("//*[@id=\"word\"]"));
				textInputField.sendKeys(words.get(i));
				Thread.sleep(4000);
				WebElement submitButton = driver.findElement(By.xpath("//*[@id=\"searchbutton\"]"));
				submitButton.click();
				Thread.sleep(8000);
				//WebElement result = driver.findElement(By.xpath("/html/body/div[2]/div[4]/div[3]/table/tbody/tr[2]/td[2]/a[1]"));
				//WebElement result = driver.findElement(By.xpath("//*[@id=\"result\"]/table/tbody/tr[2]/td[2]/a[1]"));
				WebElement result = driver.findElement(By.xpath("//td[@class=\"result\"][2]/a[1]"));
				String resultText = result.getText();
				System.out.println(resultText);
				wordsDefinitions.add(resultText);
				WebElement reloadButton = driver.findElement(By.xpath("//*[@id=\"page\"]/div[1]/div[1]/a"));
				reloadButton.click();
				Thread.sleep(8000);
			 } catch(NoSuchElementException e) {								
				System.out.println(e.getClass() + e.getMessage());
				WebElement reloadButton = driver.findElement(By.xpath("//*[@id=\"page\"]/div[1]/div[1]/a"));
				try {				
					WebElement result = driver.findElement(By.xpath("//*[@id=\"result\"]/table/tbody/tr[2]/td[2]/a[1]"));
					String resultText = result.getText();
					wordsDefinitions.add(resultText);
					String currentRecommendedWord = "";
					currentRecommendedWord = driver.findElement(By.xpath("//*[@id=\"result\"]/table/tbody/tr[2]/td[1]/a[1]")).getText();
					words.add(i, currentRecommendedWord);
					
				} catch(NoSuchElementException ex) {
					System.out.println(e.getClass() + e.getMessage());
					words.remove(i);					
				}
				reloadButton.click();
				Thread.sleep(8000);
				continue;
			 }
		 }		 
		 driver.quit();
	}
	*/
	
	public static void assembleOutput() {
		for(int i = 0; i < wordsDefinitions.size(); i++) {
			wordsOut.add(words.get(i) + "," + wordsDefinitions.get(i));
		}
		
		System.out.println("wordsOut");
		for(String line : wordsOut) {
			System.out.println(line);
		}
		
		System.out.println("words");
		for(String line : words) {
			System.out.println(line);
		}
		
		System.out.println("wordsDefinitions");
		for(String line : wordsDefinitions) {
			System.out.println(line);
		}
	}
	
	
}