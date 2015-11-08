package org.molgenis.selenium.model.mappingservice;

import java.util.List;

import org.molgenis.selenium.model.MenuModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MappingProjectsModel extends MenuModel
{
	@FindBy(name = "mapping-project-name")
	private WebElement mappingProjectTextFieldName;

	@FindBy(id = "create-new-mapping-project-modal")
	private WebElement createNewMappingProjectModal;

	@FindBy(id = "add-mapping-project-btn")
	private WebElement addMappingProjectButton;

	@FindBy(id = "submit-new-mapping-project-btn")
	private WebElement submitNewMappingProjectButton;

	@FindBy(css = "#mapping-projects-tbl tbody tr")
	private List<WebElement> mappingProjectTableRows;

	@FindBy(css = "div.modal-footer button:contains('OK')")
	private WebElement okButton;

	private Select2Model targetEntitySelect;

	public MappingProjectsModel(WebDriver driver)
	{
		super(driver);
		targetEntitySelect = new Select2Model(driver, "target-entity-select", false);
	}

	public MappingProjectDetailsModel addNewMappingProject(String name, String targetEntity)
	{
		addMappingProjectButton.click();
		mappingProjectTextFieldName.sendKeys(name);
		targetEntitySelect.select(targetEntity);
		submitNewMappingProjectButton.click();
		return PageFactory.initElements(driver, MappingProjectDetailsModel.class);
	}

	public List<List<String>> getMappingProjectsTable()
	{
		return getTableData(mappingProjectTableRows);
	}

	public MappingProjectsModel copyMappingProject(String projectName)
	{
		WebElement toMappingProjectDetailsLink = driver.findElement(By.linkText(projectName));
		WebElement cloneButton = toMappingProjectDetailsLink
				.findElement(By.xpath("../..//button[contains(@class,'clone-btn')]"));
		cloneButton.click();
		return this;
	}

	public MappingProjectsModel deleteMappingProject(String projectName)
	{
		WebElement toMappingProjectDetailsLink = driver.findElement(By.linkText(projectName));
		WebElement deleteButton = toMappingProjectDetailsLink
				.findElement(By.xpath("../..//button[contains(@class,'btn-danger')]"));
		deleteButton.click();
		okButton.click();
		return this;
	}

}
