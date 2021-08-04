package petstore.automation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Automation Tests for Azure Pet Store
 */
public class AzurePetStoreAutomationTests {

	private HtmlUnitDriver unitDriver = new HtmlUnitDriver();

	private String URL = "https://azurepetstore.com/";

	private String DOG_BREEDS = "Afador,American Bulldog,Australian Retriever,Australian Shepherd,Basset Hound,Beagle,Border Terrier,Boston Terrier,Bulldog,Bullmastiff,Chihuahua,Cocker Spaniel,German Sheperd,Labrador Retriever,Pomeranian,Pug,Rottweiler,Shetland Sheepdog,Shih Tzu,Toy Fox Terrier";

	@Test
	// Test the Azure Pet Store App Title
	public void testAzurePetStoreTitle() {
		this.unitDriver.get(this.URL);
		System.out.println("Title of the page is -> " + this.unitDriver.getTitle());
		assertEquals("Azure Pet Store", this.unitDriver.getTitle());
	}

	@Test
	// Test the Azure Pet Store App Dog Breed Page and Downstream Azure Pet Store
	// Service / Dog Breed API
	public void testAzurePetStoreDogBreedCount() {
		this.unitDriver.get(this.URL + "dogbreeds?category=Dog");
		WebElement element = this.unitDriver.findElement(By.className("table"));
		String dogBreedsFound = new String(element.getText()).trim().replaceAll("\n", ",");
		System.out.println("Dog Breeds found in the page -> " + dogBreedsFound);
		assertEquals(this.DOG_BREEDS, dogBreedsFound);
	}
}
