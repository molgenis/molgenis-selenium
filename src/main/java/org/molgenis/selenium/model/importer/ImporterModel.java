package org.molgenis.selenium.model.importer;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.molgenis.selenium.model.AbstractModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImporterModel extends AbstractModel
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

	@FindBy(xpath = "//input[@name='name']")
	private WebElement entityNameInput;

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

	private final Wait<WebDriver> oneMinuteWait;
	private final Wait<WebDriver> fiveMinuteWait;

	public ImporterModel(WebDriver driver)
	{
		super(driver);
		oneMinuteWait = new WebDriverWait(driver, 60);
		fiveMinuteWait = new WebDriverWait(driver, TimeUnit.MINUTES.toSeconds(5));
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
		LOG.info("importFile {}. Options={} ...", file, options);
		uploadFile(file);
		selectOptions(options);
		selectBasePackage();
		validate();
		waitForResult();
		LOG.info("done");
		return this;
	}

	public ImporterModel importVcf(File file, String entityName)
	{
		LOG.info("importFile {}. entityName={} ...", file, entityName);
		uploadFile(file);
		selectEntityName(entityName);
		selectBasePackage();
		validate();
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
		LOG.info("uploadFile {}...", file);
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
		oneMinuteWait.until(visibilityOf(stepOne));
		return this;
	}

	public ImporterModel selectEntityName(String entityName)
	{
		oneMinuteWait.until(visibilityOf(stepTwo));
		entityNameInput.clear();
		entityNameInput.sendKeys(entityName);
		nextButton.click();
		return this;
	}

	public ImporterModel selectOptions(EntitiesOptions options)
	{
		LOG.info("selectOptions {}...", options);
		oneMinuteWait.until(visibilityOf(stepTwo));
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
		LOG.info("selectBasePackage...");
		oneMinuteWait.until(visibilityOf(stepThree));
		basePackageRadioButton.click();
		nextButton.click();
		return this;
	}

	public ImporterModel validate()
	{
		LOG.info("validate...");
		oneMinuteWait.until(visibilityOf(stepFour));
		nextButton.click();
		return this;
	}

	public ImporterModel waitForResult()
	{
		LOG.info("waitForResult...");
		oneMinuteWait.until(visibilityOf(stepFive));
		fiveMinuteWait.until(this::importFinished);
		return this;
	}

	private boolean importFinished(WebDriver driver)
	{
		return !getMessageHeader().contains("Importing...");
	}
}
