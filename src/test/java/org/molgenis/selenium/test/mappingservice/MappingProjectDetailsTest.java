package org.molgenis.selenium.test.mappingservice;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import java.util.List;

import org.molgenis.JenkinsConfig;
import org.molgenis.selenium.model.mappingservice.MappingProjectDetailsModel;
import org.molgenis.selenium.test.AbstractSeleniumTest;
import org.molgenis.selenium.test.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ JenkinsConfig.class, Config.class })
public class MappingProjectDetailsTest extends AbstractSeleniumTest
{
	private static final Logger LOG = LoggerFactory.getLogger(MappingProjectDetailsTest.class);
	private MappingProjectDetailsModel model;

	// @BeforeClass
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
		homepage.menu().openSignInDialog().signIn(uid, pwd).menu().selectTagWizard().selectEntity("HOP_selenium")
				.tagAttributeWithTerms("Body_Mass_Index", "Height", "Weight")
				.tagAttributeWithTerms("Body_Mass_Index_1", "Height", "Weight")
				.tagAttributeWithTerms("Body_Mass_Index_2", "Height", "Weight").menu().signOut();
	}

	// @AfterClass
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
		tryDeleteEntities("testing_lifelines_hop");
		tryDeleteData("MappingProject", "MappingTarget", "EntityMapping", "AttributeMapping");
		model = homepage.menu().selectMappingService().addNewMappingProject("Hop hop hop", "HOP_selenium");

	}

	@AfterMethod
	public void afterMethod()
	{
		tryDeleteEntities("testing_lifelines_hop");
		tryDeleteData("MappingProject", "MappingTarget", "EntityMapping", "AttributeMapping");
	}

	@Test
	public void testMapLifelinesToHop()
	{

		assertEquals(model.addSource("lifelines_test").getMappingProjectTableData(),
				asList(asList("Gender (categorical)", "Sex"),
						asList("Measured Standing Height in m (decimal)", "Height in centimeter"),
						asList("Measured Standing Height in Meter (decimal)", "Height in centimeter"),
						asList("Measured Weight in Gram (decimal)", "Weight in gram"),
						asList("Measured Weight in KILOGRAM (decimal)", "Weight in gram"),
						asList("Fasting Glucose (decimal)", "If the participant fasting?"),
						asList("Triglycerides (decimal)", "Triglycerides"),
						asList("Body Mass Index kg/m2 (decimal)\n(Height and Weight)", "Height in centimeter"),
						asList("Body Mass Index kg/m^2 (decimal)\n(Height and Weight)", "Height in centimeter"),
						asList("Body Mass Index kg/m² (decimal)\n(Height and Weight)", "Height in centimeter"),
						asList("Current Consumption Frequency of Potatoes (categorical)",
								"How often did you eat boiled or mashed potatoes (also in stew) in the past month? Baked potatoes are asked later"),
				asList("History of Hypertension (categorical)",
						"Have you ever had high blood pressure? (Repeat) (1)")));
		assertEquals(model.createIntegratedDataset("testing_lifelines_hop").getTableData(),
				asList(asList("edit", "trash", "search", "", "Female", "1.675", "1.675", "98000", "98", "1", "1.04",
						"167.5", "167.5", "167.5", "", "Never + fewer than once a week", "",
						"Ever had high blood pressure", "lifelines_test")));
	}
}
