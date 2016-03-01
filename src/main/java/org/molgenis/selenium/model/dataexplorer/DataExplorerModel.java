package org.molgenis.selenium.model.dataexplorer;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.molgenis.selenium.model.AbstractModel;
import org.molgenis.selenium.model.component.Select2Model;
import org.molgenis.selenium.model.dataexplorer.annotators.AnnotatorModel;
import org.molgenis.selenium.model.dataexplorer.data.DataModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a model of the MOLGENIS Data Explorer user interface
 */
public class DataExplorerModel extends AbstractModel
{
	private static final Logger LOG = LoggerFactory.getLogger(DataExplorerModel.class);

	public static enum DeleteOption
	{
		DATA, DATA_AND_METADATA;
	}

	private final Select2Model entityModel;

	@FindBy(css = ".page-next a")
	private WebElement next;

	@FindBy(css = ".page-prev a")
	private WebElement previous;

	@FindBy(id = "entity-class-name")
	private WebElement entityClassName;

	@FindBy(id = "dropdownMenu1")
	private WebElement deleteDropdownMenu;

	@FindBy(id = "delete-data-btn")
	private WebElement deleteDataButton;

	@FindBy(id = "delete-data-metadata-btn")
	private WebElement deleteDataMetadataBtn;

	@FindBy(css = "[data-bb-handler=confirm]")
	private WebElement confirmButton;

	@FindBy(linkText = "Annotators")
	private WebElement annotatorTab;

	@FindBy(linkText = "Data")
	private WebElement dataTab;

	@FindBy(css = "a.tree-deselect-all-btn")
	private WebElement deselectAllButton;

	@FindBy(css = "div.molgenis-tree span.fancytree-has-children span.fancytree-checkbox")
	private List<WebElement> treeFolders;

	@FindBy(css = ".molgenis-table-container tbody tr")
	private List<WebElement> tableRows;

	@FindBy(css = "#copy-data-btn")
	private WebElement copyCheckBtn;

	public DataExplorerModel(WebDriver driver)
	{
		super(driver);
		entityModel = new Select2Model(driver, "dataset-select", false);
	}

	public void deleteEntity(DeleteOption deleteOption)
	{
		String selectedEntity = getSelectedEntityTitle();
		LOG.info("deleteEntity {}, mode={} ...", selectedEntity, deleteOption);
		deleteDropdownMenu.click();

		switch (deleteOption)
		{
			case DATA:
				deleteDataButton.click();
				break;
			case DATA_AND_METADATA:
				deleteDataMetadataBtn.click();
				break;
			default:
				break;
		}
		confirmButton.click();
		spinner().waitTillDone(20, TimeUnit.SECONDS);
	}

	public DataExplorerModel selectEntity(String entityLabel)
	{
		LOG.info("selectEntity", entityLabel);
		entityModel.select(entityLabel);
		return this;
	}

	public String getSelectedEntityTitle()
	{
		return entityClassName.getText();
	}

	public DataExplorerModel next()
	{
		next.click();
		return this;
	}

	public DataExplorerModel previous()
	{
		previous.click();
		return this;
	}

	public AnnotatorModel selectAnnotatorTab()
	{
		LOG.info("Select annotator tab...");
		annotatorTab.click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, AnnotatorModel.class);
	}

	public DataModel selectDataTab()
	{
		LOG.info("Select data tab...");
		dataTab.click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);
		return PageFactory.initElements(driver, DataModel.class);
	}

	public DataExplorerModel deselectAll()
	{
		deselectAllButton.click();
		return this;
	}

	/**
	 * Clicks on an attribute's checkbox in the attribute tree.
	 * 
	 * @param attributeName
	 * @return
	 */
	public DataExplorerModel clickAttribute(String attributeName)
	{
		LOG.info("Click on attribute: " + attributeName);
		driver.findElement(
				By.xpath("//div[@class='molgenis-tree']//li[span/span/text()='" + attributeName
						+ "']/span/span[@class='fancytree-checkbox']")).click();
		return this;
	}

	/**
	 * Retrieves the currently displayed table data, row by row. The first three columns are skipped.
	 */
	public List<List<String>> getTableData()
	{
		LOG.info("getTableData...");
		List<List<String>> result = getTableData(tableRows).stream().map(l -> l.subList(3, l.size())).collect(toList());
		LOG.debug("getTableData result={}", result);
		return result;
	}

	/**
	 * Returns the fully qualified name of the currently displayed entity, based on the driver's URL.
	 */
	public Optional<String> getEntityNameFromURL()
	{
		try
		{
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(driver.getCurrentUrl()), "UTF-8");
			return params.stream().filter(nvp -> "entity".equals(nvp.getName())).findFirst()
					.map(NameValuePair::getValue);
		}
		catch (URISyntaxException e)
		{
			return empty();
		}
	}

	public DataExplorerModel copyEntity(String newEntityName)
	{
		LOG.info("Start copy");

		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.visibilityOf(copyCheckBtn));
		copyCheckBtn.click();

		WebElement input = driver.findElement(By.cssSelector("input.bootbox-input"));
		input.clear();
		input.sendKeys(newEntityName);

		WebElement okBtn = driver.findElement(By.cssSelector("div.bootbox .modal-footer button.btn-primary"));
		okBtn.click();
		spinner().waitTillDone(10, TimeUnit.SECONDS);

		LOG.info("Finished copy");
		return this;
	}
}
