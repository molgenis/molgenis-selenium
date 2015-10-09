package org.molgenis.selenium.model.mappingservice;

import static org.molgenis.selenium.util.MappingServiceUtil.clickButonById;
import static org.molgenis.selenium.util.MappingServiceUtil.clickButtonWithInSpecifiedElementByClassName;
import static org.molgenis.selenium.util.MappingServiceUtil.clickCancelButonInAddNewMappingProjectModal;
import static org.molgenis.selenium.util.MappingServiceUtil.clickGoBackButtonToMappingProjectOverView;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOKButonByXpathExpression;
import static org.molgenis.selenium.util.MappingServiceUtil.clickToOpenOneMappingProject;
import static org.molgenis.selenium.util.MappingServiceUtil.getAnElementByCssSelector;
import static org.molgenis.selenium.util.MappingServiceUtil.openMappingService;
import static org.molgenis.selenium.util.MappingServiceUtil.setValueToTextFieldByName;

import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.selenium.util.Select2Util;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class MappingProjectOverviewModel extends AbstractMappingServiceAppModel
{
	public static final Logger LOG = LoggerFactory.getLogger(MappingProjectOverviewModel.class);

	public MappingProjectOverviewModel(WebDriver driver, MolgenisClient molgenisClient)
	{
		super(driver, molgenisClient);
	}

	public void openOneMappingProject() throws InterruptedException
	{
		clickToOpenOneMappingProject(MAPPING_PROJECT_NAME, driver);
	}

	public void cancelAddMappingProject() throws InterruptedException
	{
		openMappingService(driver);

		int previousNumberOfRows = driver.findElements(By.xpath("//table[contains(@class, 'table')]/tbody/tr")).size();

		clickButonById("submit-new-source-column-btn", driver);

		clickCancelButonInAddNewMappingProjectModal(driver);

		int rowCount = driver.findElements(By.xpath("//table[contains(@class, 'table')]/tbody/tr")).size();

		Assert.assertEquals(rowCount, previousNumberOfRows);
	}

	public void addOneMappingProjectWithoutName() throws InterruptedException
	{
		openMappingService(driver);

		clickButonById("submit-new-source-column-btn", driver);

		clickButonById("submit-new-mapping-project-btn", driver);

		String errorMessage = getAnElementByCssSelector("label", "for", "mapping-project-name", driver).getText();

		Assert.assertEquals(errorMessage, "This field is required.");
	}

	public void addOneMappingProject() throws InterruptedException
	{
		openMappingService(driver);

		clickButonById("submit-new-source-column-btn", driver);

		setValueToTextFieldByName("mapping-project-name", MAPPING_PROJECT_NAME, driver);

		Assert.assertTrue(driver instanceof JavascriptExecutor);

		Select2Util.select("create-new-mapping-project-modal", TARGET_ENTITY_NAME, driver, LOG);

		clickButonById("submit-new-mapping-project-btn", driver);

		clickGoBackButtonToMappingProjectOverView(driver);
	}

	public void removeTestMappingProject() throws InterruptedException
	{
		openMappingService(driver);

		WebElement webElement = driver.findElements(By.xpath("//table[contains(@class, 'table')]/tbody/tr")).stream()
				.filter(this::ifMappingProjectTest).findFirst().orElseGet(null);

		Assert.assertTrue(webElement != null);

		clickButtonWithInSpecifiedElementByClassName(webElement, "btn-danger", driver);

		clickOKButonByXpathExpression(driver);
	}

	private boolean ifMappingProjectTest(WebElement webElement)
	{
		return webElement.findElements(By.tagName("td")).get(1).getText().equals(MAPPING_PROJECT_NAME);
	}
}
