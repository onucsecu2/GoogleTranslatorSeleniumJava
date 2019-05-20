import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;




public class TRANS {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("hey");
		// TODO Auto-generated method stub
		
		
		List<String> records = new ArrayList<>();
		List<String> translated = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("/home/onu/Downloads/testing.csv"))) {
		    String line=new String();
		    int rows =0;
		    while ((line = br.readLine()) != null) {
		    	rows++;
		    	if(rows==1)
		    			continue;
		    	int cnt=0;
		    	for(int i=0;i<line.length();i++) {
		    		if(line.charAt(i)==',') {
		    			cnt++;
		    		}
		    		if(cnt==2) {
		    			records.add(line.substring(i+1,line.length()-1));
		    			break;
		    		}
		    	}
		        
		    }
		}
		
		/*for (String string : records) {
			System.out.println(string);
		} */
		
		
		
		System.setProperty("webdriver.gecko.driver", "/home/onu/geckodriver");
		WebDriver driver =new FirefoxDriver() ;
		
        String baseUrl = "https://translate.google.com/#view=home&op=translate&sl=en&tl=bn";					
        driver.get(baseUrl);
		WebElement giventext =driver.findElement(By.xpath("//*[@id=\"source\"]"));
		for (String string : records) {
			//System.out.println(string);
		 
			giventext.sendKeys(string);
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("cant wait haga dhorce :3");
			}
			WebElement outtext =driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[3]/div[1]/div[2]/div/span[1]/span"));
			String ans=outtext.getText();
			//WebDriverWait wait = new WebDriverWait(driver, 10);
			//wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(giventext, ans)));
			System.out.println(ans);
			translated.add(ans);
			giventext.clear();
			//outtext.clear();
			
		}
		
		System.out.println("Finish");
		//driver.close();
		for(String string:translated) {
			System.out.println(string);
		}
		
	}

}
