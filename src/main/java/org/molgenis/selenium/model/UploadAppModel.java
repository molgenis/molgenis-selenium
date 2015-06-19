package org.molgenis.selenium.model;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.molgenis.selenium.util.MenuUtil;
import org.molgenis.selenium.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.testng.Assert;

public class UploadAppModel
{
	public static String MENUITEM = "Upload";
	private WebDriver driver;
	
	public static enum EntitiesOptions
	{
		// Add entities
		// Importer adds new entities or fails if entity exists
		ADD, 
		
		// Add entities / update existing
		// Importer adds new entities or updates existing entities
		ADD_UPDATE,
		
		//Update entities
		//Importer updates existing entities or fails if entity does not exist
		UPDATE;
	}

	public UploadAppModel(WebDriver driver)
	{
		this.driver = driver;
	}

	public void uploadXlsxEmxAllDatatypes(EntitiesOptions entitiesOption, Logger logger)
	{
		try
		{
			uploadFile("org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx", entitiesOption,
					"org_molgenis_test");

			// Success message header
			Assert.assertEquals(this.isImportedSuccess(), true);

			// Success message body
			Assert.assertEquals(this.getValidationError(logger),
					"Imported 5 org_molgenis_test_TypeTestRef entities.<br>Imported 38 org_molgenis_test_TypeTest entities.<br>");
		}
		catch (IOException | InterruptedException e)
		{
			logger.error("Importing org/molgenis/selenium/emx/xlsx/emx_all_datatypes.xlsx with option: "
					+ entitiesOption
					+ " failed");
		}
	}

	public void uploadCsvZipEmxAllDatatypes(EntitiesOptions entitiesOption, Logger logger)
	{
		String filePath = "org/molgenis/selenium/emx/csv.zip/emx_all_datatypes_csv.zip";
		try
		{
			uploadFile(filePath, entitiesOption, "org_molgenis_test");

			// Success message header
			Assert.assertEquals(this.isImportedSuccess(), true);

			// Success message body
			Assert.assertEquals(this.getValidationError(logger),
					"Imported 5 org_molgenis_test_TypeTestRefCSV entities.<br>Imported 38 org_molgenis_test_TypeTestCSV entities.<br>");
		}
		catch (IOException | InterruptedException e)
		{
			logger.error(filePath + " with option: " + entitiesOption + " failed");
		}
	}

	public void uploadFile(String filePath, EntitiesOptions entitiesOption,
			String addToPackage) throws IOException, InterruptedException
	{
		// Step 1: UploadFile
		this.uploadFile(filePath);
		this.next();

		// Step 2: Options
		this.setEntityOptions(entitiesOption);
		this.next();

		// Step 3: packages
		this.addToPackage(addToPackage);
		this.next();

		// Step 4: validation
		this.next();

		// Step 5
		this.finish();
	}

	public void open() throws InterruptedException
	{
		MenuUtil.openPageByClickOnMenuItem("Upload", driver);
	}
	
	public void uploadFile(String relativePath) throws InterruptedException
	{
		By selectAFile = By.name("upload");
		SeleniumUtils.waitForElement(selectAFile, driver);

		// http://stackoverflow.com/questions/5610256/file-upload-using-selenium-webdriver-and-java
		File file;
		try
		{
			file = new File(getClass().getClassLoader().getResource(relativePath).toURI());
		}
		catch (Exception ex)
		{
			file = new File("test-classes/" + relativePath);
		}
		assertTrue(file.exists());

		SeleniumUtils.waitForElement(By.cssSelector("ol.bwizard-steps li:nth-child(1).active"), driver);

		driver.findElement(selectAFile).sendKeys(file.getAbsolutePath());
	}

	/**
	 * Importer adds new entities or fails if entity exists
	 * 
	 * @throws InterruptedException
	 */
	public void setEntityOptions(EntitiesOptions entitiesOptions) throws InterruptedException
	{
		switch(entitiesOptions){
			case ADD:
				By add = By.xpath("//input[@value='add']");
				SeleniumUtils.waitForElement(add, driver);
				driver.findElement(add).click();
				break;
			case ADD_UPDATE:
				By addUpdate = By.xpath("//input[@value='add_update']");
				SeleniumUtils.waitForElement(addUpdate, driver);
				driver.findElement(addUpdate).click();
				break;
			case UPDATE:
				By update = By.xpath("//input[@value='update']");
				SeleniumUtils.waitForElement(update, driver);
				driver.findElement(update).click();
				break;
			default:
				break;
		}
	}

	public void next() throws InterruptedException
	{
		By next = By.partialLinkText("Next");
		driver.findElement(next).click();

		// To accommodate for Ajax stuff
		Thread.sleep(2000);
	}

	public void finish() throws InterruptedException
	{
		By finish = By.cssSelector("li.next:not(.disabled)");
		SeleniumUtils.waitForElement(finish, driver);
		
		By click = By.cssSelector("li.next [href=\\/menu\\/main\\/importwizard\\/restart]");
		SeleniumUtils.waitForElement(click, driver);
		driver.findElement(click).click();
	}

	public void addToPackage(String packageName)
	{
		driver.findElement(By.xpath("//input[@value='" + packageName + "']")).click();
	}

	public boolean isImportedSuccess() throws InterruptedException
	{
		By heading = By.cssSelector("#message-panel .panel-heading");
		SeleniumUtils.waitForElement(heading, driver);
		String innerHTML = driver.findElement(heading).getAttribute("innerHTML");
		return "Import success".equals(innerHTML);
	}

	public String getValidationError(Logger logger) throws InterruptedException
	{
		By messages = By.id("message");
		SeleniumUtils.waitForElement(messages, driver);
		String innerHTML = driver.findElement(messages).getAttribute("innerHTML");
		logger.info("Messages: " + innerHTML);
		return innerHTML;
	}
}
