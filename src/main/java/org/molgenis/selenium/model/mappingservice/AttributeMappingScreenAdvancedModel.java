package org.molgenis.selenium.model.mappingservice;

import static org.molgenis.selenium.util.MappingServiceUtil.clickOnEditAttributeMappingTableByIndex;
import static org.molgenis.selenium.util.MappingServiceUtil.getValueFromAlgorithmEditorInAttributeMapping;

import org.molgenis.data.rest.client.MolgenisClient;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class AttributeMappingScreenAdvancedModel extends AbstractMappingServiceAppModel
{
	public static final Logger LOG = LoggerFactory.getLogger(AttributeMappingScreenModel.class);
	private static final String ALGORITHM_FOR_MAPPING_GENDER_IN_LIFELINES = "$('GENDER').map({\"1\":\"0\",\"2\":\"1\"}, null, null).value();";

	public AttributeMappingScreenAdvancedModel(WebDriver driver, MolgenisClient molgenisClient)
	{
		super(driver, molgenisClient);
	}

	public void testGenderAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(1, 2, driver);

		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_GENDER_IN_LIFELINES);
	}
}
