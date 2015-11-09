package org.molgenis.selenium.model.importer;

import static org.molgenis.selenium.util.SeleniumUtils.waitFor;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.molgenis.selenium.model.MenuModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImporterModel extends MenuModel
{
	private static final Logger LOG = LoggerFactory.getLogger(ImporterModel.class);

	public static enum EntitiesOptions
	{
		// Add entities
		// Importer adds new entities or fails if entity exists
		ADD,

		// Add entities / update existing
		// Importer adds new entities or updates existing entities
		ADD_UPDATE,

		// Update entities
		// Importer updates existing entities or fails if entity does not exist
		UPDATE;
	}

	@FindBy(id = "wizard-next-button")
	private WebElement nextButton;

	@FindBy(id = "wizard-finish-button")
	private WebElement finishButton;

	@FindBy(name = "upload")
	private WebElement upload;

	@FindBy(css = "ol.bwizard-steps li:nth-child(1)")
	private WebElement fileNameSelection;

	@FindBy(css = "#message-panel .panel-heading")
	private WebElement messagePanel;

	@FindBy(xpath = "//input[@value='add']")
	private WebElement addRadioButton;

	@FindBy(xpath = "//input[@value='add_update']")
	private WebElement addUpdateRadioButton;

	@FindBy(xpath = "//input[@value='update']")
	private WebElement updateRadioButton;

	@FindBy(xpath = "//input[@value='base']")
	private WebElement basePackageRadioButton;

	@FindBy(id = "message")
	private WebElement message;

	@FindBy(css = "ol.bwizard-steps li:nth-child(1).active")
	private WebElement stepOne;

	@FindBy(css = "ol.bwizard-steps li:nth-child(2).active")
	private WebElement stepTwo;

	@FindBy(css = "ol.bwizard-steps li:nth-child(3).active")
	private WebElement stepThree;

	@FindBy(css = "ol.bwizard-steps li:nth-child(4).active")
	private WebElement stepFour;

	@FindBy(css = "ol.bwizard-steps li:nth-child(5).active")
	private WebElement stepFive;

	public ImporterModel(WebDriver driver)
	{
		super(driver);
	}

	public static File getFile(String relativePath)
	{
		// http://stackoverflow.com/questions/5610256/file-upload-using-selenium-webdriver-and-java
		File file;
		try
		{
			file = new File(ImporterModel.class.getClassLoader().getResource(relativePath).toURI());
		}
		catch (Exception ex)
		{
			file = new File("test-classes/" + relativePath);
		}
		return file;
	}

	public ImporterModel importFile(File file, EntitiesOptions options)
	{
		LOG.info("uploadFile...");
		uploadFile(file);
		LOG.info("selectOptions...");
		selectOptions(options);
		LOG.info("select base package...");
		selectBasePackage();
		LOG.info("validate...");
		validate();
		LOG.info("waitForResult...");
		waitForResult();
		LOG.info("done");
		return this;
	}

	public String getMessageHeader()
	{
		return messagePanel.getText();
	}

	public String getMessage()
	{
		return message.getText();
	}

	public ImporterModel uploadFile(File file)
	{
		assertTrue(file.exists());
		stepOne.click();
		upload.sendKeys(file.getAbsolutePath());
		nextButton.click();
		return this;
	}

	public ImporterModel finish()
	{
		LOG.info("finish()");
		finishButton.click();
		stepOne.click();
		return this;
	}

	public ImporterModel selectOptions(EntitiesOptions options)
	{
		stepTwo.click();
		switch (options)
		{
			case ADD:
				addRadioButton.click();
				break;
			case ADD_UPDATE:
				addUpdateRadioButton.click();
				break;
			case UPDATE:
				updateRadioButton.click();
				break;
		}
		nextButton.click();
		return this;
	}

	public ImporterModel selectBasePackage()
	{
		stepThree.click();
		basePackageRadioButton.click();
		nextButton.click();
		return this;
	}

	public ImporterModel validate()
	{
		stepFour.click();
		nextButton.click();
		return this;
	}

	public ImporterModel waitForResult()
	{
		LOG.info("waitForResult...");
		stepFive.click();
		waitFor(this::importFinished, 300);
		return this;
	}

	private boolean importFinished()
	{
		return !getMessageHeader().contains("Importing...");
	}
}
