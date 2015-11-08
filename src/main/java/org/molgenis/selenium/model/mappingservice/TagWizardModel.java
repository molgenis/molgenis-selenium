package org.molgenis.selenium.model.mappingservice;

import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.molgenis.selenium.model.MenuModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class TagWizardModel extends MenuModel
{
	private static final Logger LOG = LoggerFactory.getLogger(TagWizardModel.class);

	private WebDriverWait tenSecondWait;

	private Select2Model tagSelectionModel;
	private Select2Model ontologySelectionModel;
	private Select2Model entitySelectionModel;

	@FindBy(css = ".alert-success button.close")
	private WebElement closeSuccessAlert;

	@FindBy(id = "automatic-tag-btn")
	private WebElement automatedTaggingButton;

	@FindBy(id = "clear-all-tags-btn")
	private WebElement clearTagsButton;

	@FindBy(xpath = "//div[@class='bootbox-body' and text() = 'Are you sure you want to remove all tags?']/../../div[@class='modal-footer']/button[text()='OK']")
	private WebElement okButton;

	@FindBy(xpath = "//table[@id='tag-mapping-table']/tbody/tr")
	List<WebElement> attributeTableRows;

	@FindBy(id = "spinner")
	WebElement spinner;

	public TagWizardModel(WebDriver driver)
	{
		super(driver);
		tenSecondWait = new WebDriverWait(driver, 10);
		tagSelectionModel = new Select2Model(driver, "tag-dropdown", true);
		ontologySelectionModel = new Select2Model(driver, "ontology-select", true);
		entitySelectionModel = new Select2Model(driver, "select-target", false);
	}

	public TagWizardModel selectEntity(String name)
	{
		entitySelectionModel.select(name);
		Predicate<WebDriver> blah = d -> name.equals(getSelectedEntity());
		tenSecondWait.until(blah);
		return this;
	}

	public String getSelectedEntity()
	{
		return entitySelectionModel.getSelectedLabels().get(0);
	}

	public List<List<String>> getAttributeTags()
	{
		return attributeTableRows.stream().map(elt -> elt.findElements(By.cssSelector("td")))
				.map(tds -> Arrays.asList(tds.get(0).getText(), tds.get(1).getText())).collect(Collectors.toList());
	}

	public TagWizardModel clearTags()
	{
		clearTagsButton.click();
		okButton.click();
		tenSecondWait.until(visibilityOf(closeSuccessAlert));
		closeSuccessAlert.click();
		return this;
	}

	public TagWizardModel doAutomatedTagging()
	{
		automatedTaggingButton.click();
		tenSecondWait.until(visibilityOf(closeSuccessAlert));
		closeSuccessAlert.click();
		return this;
	}

	public TagWizardModel selectOntologies(String... ontologies)
	{
		ontologySelectionModel.clearSelection();
		ontologySelectionModel.select(ontologies);
		return this;
	}

}
