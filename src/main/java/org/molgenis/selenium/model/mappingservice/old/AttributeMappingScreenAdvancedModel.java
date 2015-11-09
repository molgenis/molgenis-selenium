package org.molgenis.selenium.model.mappingservice.old;

import static org.molgenis.selenium.util.MappingServiceUtil.clickOnEditAttributeMappingTableByIndex;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnGoBackToOneMappingProject;
import static org.molgenis.selenium.util.MappingServiceUtil.clickOnSaveButtonInAttributeMapping;
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
	private static final String ALGORITHM_FOR_MAPPING_HEIGHT_METER_SYMBOL_IN_LIFELINES = "$('HEIGHT').unit('cm').toUnit('m').value();";
	private static final String ALGORITHM_FOR_MAPPING_HEIGHT_METER_IN_LIFELINES = "$('HEIGHT').unit('cm').toUnit('m').value();";
	private static final String ALGORITHM_FOR_MAPPING_WEIGHT_GRAM_IN_LIFELINES = "$('WEIGHT').unit('g').value();";
	private static final String ALGORITHM_FOR_MAPPING_WEIGHT_KILOGRAM_IN_LIFELINES = "$('WEIGHT').unit('g').toUnit('kg').value();";
	private static final String ALGORITHM_FOR_MAPPING_FASTING_GLUCOSE_IN_LIFELINES = "$('NUCHTER1').map({0: null, 1: $('GLU').value()}).value()";
	private static final String ALGORITHM_FOR_MAPPING_TRIGLYCERIDES_IN_LIFELINES = "$('TGL').value();";
	private static final String ALGORITHM_FOR_MAPPING_BODY_MASS_INDEX_IN_LIFELINES = "$('WEIGHT').div(1000.0).div($('HEIGHT').div(100.0).pow(2)).value()";
	private static final String ALGORITHM_FOR_MAPPING_POTATON_CONSUMPTION_IN_LIFELINES = "$('FOOD59A1').map({\"1\":\"4\",\"2\":\"4\",\"3\":\"4\",\"4\":\"3\",\"5\":\"2\",\"6\":\"2\",\"7\":\"1\"}, null, null).value();";
	private static final String ALGORITHM_FOR_MAPPING_HYPERTENSION_IN_LIFELINES = "$('DIS_HBP').map({\"1\":\"1\",\"2\":\"0\",\"3\":\"9\"}, null, null).value();";

	public AttributeMappingScreenAdvancedModel(WebDriver driver, MolgenisClient molgenisClient)
	{
		super(driver, molgenisClient);
	}

	public void testGenderAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(1, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_GENDER_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testHeightAlgorithmMeterSymbolLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(2, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_HEIGHT_METER_SYMBOL_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testHeightMeterAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(3, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_HEIGHT_METER_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testWeightGramAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(4, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_WEIGHT_GRAM_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testWeightKiloGramAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(5, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_WEIGHT_KILOGRAM_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testFastingGlucoseAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(6, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_FASTING_GLUCOSE_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testTriglyceridesAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(7, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_TRIGLYCERIDES_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testBodyMassIndexOneAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(8, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_BODY_MASS_INDEX_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testBodyMassIndexTwoAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(9, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_BODY_MASS_INDEX_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testBodyMassIndexThreeAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(10, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_BODY_MASS_INDEX_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testPotatoConsumptionAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(11, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_POTATON_CONSUMPTION_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}

	public void testHypertensionAlgorithmInLifeLines() throws InterruptedException
	{
		clickOnEditAttributeMappingTableByIndex(12, 2, driver);
		Assert.assertEquals(getValueFromAlgorithmEditorInAttributeMapping(driver),
				ALGORITHM_FOR_MAPPING_HYPERTENSION_IN_LIFELINES);
		clickOnSaveButtonInAttributeMapping(driver);
		clickOnGoBackToOneMappingProject(driver);
	}
}