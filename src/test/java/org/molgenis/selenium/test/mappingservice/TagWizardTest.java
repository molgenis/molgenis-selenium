package org.molgenis.selenium.test.mappingservice;

import static java.util.Arrays.asList;

import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.mappingservice.TagWizardModel;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.molgenis.selenium.test.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class TagWizardTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(TagWizardTest.class);
	private TagWizardModel model;

	@BeforeClass
	public void beforeClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("HOP_selenium", "HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium",
				"DIS_HBP_Ref_selenium", "lifelines_test", "test_GENDER_Ref_test", "test_NUCHTER1_Ref_test",
				"test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test", "prevend_test", "test_SEX_Ref_test");
		tryDeleteData("Ontology_OntologyTerm", "Ontology_OntologyTermDynamicAnnotation",
				"Ontology_OntologyTermNodePath", "Ontology_OntologyTermSynonym", "Ontology_Ontology");
		restClient.logout(token);
		importFiles("org/molgenis/selenium/mappingservice/mappingservice-test.xlsx",
				"org/molgenis/selenium/mappingservice/biobank_ontology_test.owl.zip",
				"org/molgenis/selenium/mappingservice/uo_test.owl.zip");
	}

	@AfterClass
	public void afterClass()
	{
		token = restClient.login(uid, pwd).getToken();
		tryDeleteEntities("HOP_selenium", "HOP_GENDER_Ref_selenium", "FOOD_POTATOES_Ref_selenium",
				"DIS_HBP_Ref_selenium", "lifelines_test", "test_GENDER_Ref_test", "test_NUCHTER1_Ref_test",
				"test_FOOD59A1_Ref_test", "test_HEALTH351_Ref_test", "prevend_test", "test_SEX_Ref_test");
		tryDeleteData("Ontology_OntologyTerm", "Ontology_OntologyTermDynamicAnnotation",
				"Ontology_OntologyTermNodePath", "Ontology_OntologyTermSynonym", "Ontology_Ontology");
		restClient.logout(token);
	}

	@BeforeMethod
	public void beforeMethod() throws InterruptedException
	{
		model = homepage.selectTagWizard().selectEntity("HOP_selenium").clearTags();
	}

	@Test
	public void testAutomaticTagging()
	{
		LOG.info("testAutomaticTagging()");
		model.selectOntologies("biobank_ontology_test", "uo_test").doAutomatedTagging();

		Assert.assertEquals(model.getAttributeTags(),
				asList(asList("id\nid", ""), asList("HOP_GENDER\nGender", "Gender"),
						asList("PM_HEIGHT_MEASURE\nMeasured Standing Height in m", "(Height and meter)"),
						asList("PM_HEIGHT_MEASURE_1\nMeasured Standing Height in Meter", "(Height and meter)"),
						asList("PM_WEIGHT_MEASURE\nMeasured Weight in Gram", "(Weight and gram)"),
						asList("PM_WEIGHT_MEASURE_1\nMeasured Weight in KILOGRAM", "(kilogram and Weight)"),
						asList("LAB_GLUC_FASTING\nFasting Glucose",
								"(Glucose and Fasting)"),
						asList("LAB_TRIG\nTriglycerides", ""),
						asList("Body_Mass_Index\nBody Mass Index kg/m2",
								"(kilogram per square meter and kilogram and meter)"),
				asList("Body_Mass_Index_1\nBody Mass Index kg/m^2",
						"(kilogram per square meter and kilogram and meter)"),
				asList("Body_Mass_Index_2\nBody Mass Index kg/m²",
						"(kilogram per square meter and kilogram and meter)"),
				asList("FOOD_POTATOES\nCurrent Consumption Frequency of Potatoes", ""),
				asList("DIS_HBP\nHistory of Hypertension", "Hypertension")));
	}

	@Test
	public void testManualTagging() throws InterruptedException
	{
		LOG.info("testAutomaticTagging()");
		model.selectOntologies("biobank_ontology_test", "uo_test").tagAttributeWithTerms("PM_WEIGHT_MEASURE", "Weight",
				"Body Weight");
		Assert.assertEquals(model.getAttributeTags().get(4),
				asList("PM_WEIGHT_MEASURE\nMeasured Weight in Gram", "(Weight and Body Weight)"));
	}
}
