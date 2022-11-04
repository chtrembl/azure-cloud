package petstore.automation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Automation Tests for Azure Pet Store
 */
public class AzurePetStoreAutomationTests {

	private static String URL = "https://azurepetstore.com/";

	@Test
	public void testAzurePetStoreShoppingCartCount() {
		System.setProperty("webdriver.chrome.driver", "/Users/christremblay/Development/automation/chromedriver/107/chromedriver");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--window-size=1400,800");
		WebDriver driver = new ChromeDriver(options);
		driver.get(URL);
		//check title
		assertEquals("Azure Pet Store",driver.getTitle());
		//dismiss cookie tracking notification
		driver.findElement(By.cssSelector(".cc-dismiss")).click();
		//click dog breeds
		driver.findElement(By.linkText("Shop by breeds")).click();
		//verify number of dog breeds
		assertEquals(20, driver.findElements(By.xpath("//button[contains(.,\'Shop for\')]")).size());
		//click on a dog breed
		driver.findElement(By.xpath("//button[contains(.,\'Shop for Afador\')]")).click();
		//click on a shop for toys button
		driver.findElement(By.xpath("//table[@class='table']//a[1]/button[@class='btn btn-outline-primary']")).click();
		//click add to cart
		driver.findElement(By.xpath("//table[@class='table']//tr[1]//button[@class='btn btn-outline-primary']"))
				.click();
		//wait for page to reload
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// verify cart has a 1 toy
		assertEquals(driver.findElement(By.cssSelector(".cartcount > div")).getText(), "1");
	}

}
