package org.molgenis.selenium.test.restapi;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.molgenis.JenkinsConfig;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.data.rest.client.bean.LoginResponse;
import org.molgenis.data.rest.client.bean.QueryResponse;
import org.molgenis.util.GsonConfig;
import org.molgenis.util.GsonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Automated version of the REST API test sheet.
 */
@ContextConfiguration(classes =
{ JenkinsConfig.class, GsonConfig.class })
public class RestAPITest extends AbstractTestNGSpringContextTests
{
	private static final Logger LOG = LoggerFactory.getLogger(RestAPITest.class);

	private Map<String, Object> testUser;
	private MolgenisClient client;

	@Value("${test.baseurl}")
	private String baseUrl;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;

	private String adminToken;

	@Autowired
	private GsonHttpMessageConverter gsonHttpMessageConverter;

	public RestAPITest()
	{
		ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object> builder();
		builder.put("active", true);
		builder.put("username", "test");
		builder.put("password_", "secret");
		builder.put("superuser", false);
		builder.put("changePassword", false);
		builder.put("Email", "test@example.com");
		testUser = builder.build();
	}

	public void loginAdmin()
	{
		LoginResponse response = client.login(uid, pwd);
		adminToken = response.getToken();
	}

	public void grant(String userName, String entityName, String permission)
	{
		Object id = getUserId(userName);
		client.create(adminToken, "UserAuthority", ImmutableMap.<String, Object> of("role",
				"ROLE_ENTITY_" + permission + "_" + entityName.toUpperCase(), "molgenisUser", id));
	}

	public void createUser()
	{
		try
		{
			client.create(adminToken, "MolgenisUser", testUser);
			grant("test", "LoggingEvent", "WRITE");
			grant("test", "ScriptType", "READ");
			grant("test", "UserAuthority", "COUNT");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void deleteUser()
	{
		Object id = getUserId("test");
		if (id != null)
		{
			QueryResponse response = client.queryEquals(adminToken, "UserAuthority", "molgenisUser", id);
			for (Map<String, Object> item : response.getItems())
			{
				System.out.println("Deleting UserAuthority" + item);
				client.delete(adminToken, "UserAuthority", item.get("id"));
			}

			response = client.queryEquals(adminToken, "MolgenisToken", "molgenisUser", id);
			for (Map<String, Object> item : response.getItems())
			{
				System.out.println("Deleting MolgenisToken" + item);
				client.delete(adminToken, "MolgenisToken", item.get("id"));
			}
			client.delete(adminToken, "MolgenisUser", id);
		}
	}

	protected Object getUserId(String username)
	{
		QueryResponse response = client.queryEquals(adminToken, "MolgenisUser", "username", username);
		if (response.getItems().isEmpty())
		{
			return null;
		}
		return response.getItems().get(0).get("id");
	}

	@PostConstruct
	public void startup()
	{
		createClient();
		loginAdmin();
		deleteUser();
		createUser();
	}

	protected void createClient()
	{
		RestTemplate template = new RestTemplate();
		template.setMessageConverters(asList(gsonHttpMessageConverter));
		String apiHref = baseUrl + "/api/v1";
		client = new MolgenisClient(template, apiHref);
	}

	@Test
	public void testAnonymousUserHasNoReadPermissions()
	{
		LOG.info("Test that anonymous user cannot read protected entities...");

		LOG.info("Test that anonymous user cannot read LoggingEvent...");
		try
		{
			client.get(null, "LoggingEvent");
			fail("anonymous user should not be able to retrieve LoggingEvent");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No READ permission on entity LoggingEvent");
		}

		LOG.info("Test that anonymous user cannot read ScriptType...");
		try
		{
			client.get(null, "ScriptType");
			fail("anonymous user should not be able to retrieve ScriptType");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No READ permission on entity ScriptType");
		}

		LOG.info("Test that anonymous user cannot read UserAuthority...");
		try
		{
			client.get(null, "UserAuthority");
			fail("anonymous user should not be able to retrieve UserAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No READ permission on entity UserAuthority");
		}

		LOG.info("Test that anonymous user cannot read GroupAuthority...");
		try
		{
			client.get(null, "GroupAuthority");
			fail("anonymous user should not be able to retrieve GroupAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No READ permission on entity GroupAuthority");
		}
	}

	public static String parseErrorMessage(HttpClientErrorException actual)
	{
		Gson gson = new Gson();
		TypeToken<Map<String, List<Map<String, String>>>> type = new TypeToken<Map<String, List<Map<String, String>>>>()
		{
		};
		Map<String, List<Map<String, String>>> object = gson.fromJson(actual.getResponseBodyAsString(), type.getType());
		return object.get("errors").get(0).get("message");
	}

	@Test
	public void testTestUserHasReadPermissionOnLoggingEventAndScriptTypeButNotOnUserAuthorityAndGroupAuthority()
	{
		LOG.info("Test test user permissions...");

		LOG.debug("login test user...");
		String token = client.login("test", "secret").getToken();

		LOG.info("Test that test user can read LoggingEvents...");
		client.get(token, "LoggingEvent");

		LOG.info("Test that test user can read ScriptType...");
		client.get(token, "ScriptType");

		LOG.info("Test that test user cannot read UserAuthority...");
		try
		{
			client.get(token, "UserAuthority");
			fail("test user should not be able to retrieve UserAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No READ permission on entity UserAuthority");
		}

		LOG.info("Test that test user cannot read GroupAuthority...");
		try
		{
			client.get(token, "GroupAuthority");
			fail("test user should not be able to retrieve GroupAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No READ permission on entity GroupAuthority");
		}
	}

	@Test
	public void testTestUserCanDeleteLoggingEvent()
	{
		LOG.info("Test that test user can delete logging event...");
		LOG.debug("Login test user...");
		String token = client.login("test", "secret").getToken();
		
		LOG.debug("Retrieve LoggingEvents...");
		QueryResponse response = client.get(token, "LoggingEvent");
		Object identifier = response.getItems().get(0).get("identifier");
		
		LOG.debug("Delete the first LoggingEvent...");
		client.delete(token, "LoggingEvent", identifier);
		
		LOG.debug("Make sure the deleted LoggingEvent is gone...");
		try
		{
			client.get(token, "LoggingEvent", identifier);
		}
		catch (HttpClientErrorException hcee)
		{
			assertEquals(hcee.getStatusCode(), NOT_FOUND);
			assertEquals(parseErrorMessage(hcee), "LoggingEvent " + identifier + " not found");
		}
	}

	@Test
	public void testTestUserCannotDeleteScriptTypeR()
	{
		LOG.info("Test that test user isnot authorized to delete ScriptType R...");
		LOG.debug("login test user...");
		String token = client.login("test", "secret").getToken();
		
		LOG.debug("try to delete ScriptType R...");
		try
		{
			client.delete(token, "ScriptType", "R");
			fail("Test user should not be able to delete script type R");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No WRITE permission on entity ScriptType");
		}
	}

	@Test
	public void testTestUserCannotDeleteUserAuthority()
	{
		LOG.info("Test that test user isnot authorized to delete UserAuthority...");
		LOG.debug("login test user...");
		String token = client.login("test", "secret").getToken();
		
		LOG.debug("Try to delete nonexistent UserAuthority...");
		try
		{
			client.delete(token, "UserAuthority", "blah");
			fail("Test user should not be able to delete UserAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No WRITE permission on entity UserAuthority");
		}
	}

	@Test
	public void testTestUserCannotDeleteGroupAuthority()
	{
		LOG.info("Test that test user isnot authorized to delete GroupAuthority...");
		LOG.debug("login test user...");
		String token = client.login("test", "secret").getToken();
		
		LOG.debug("Try to delete nonexistent GroupAuthority...");
		try
		{
			client.delete(token, "GroupAuthority", "blah");
			fail("Test user should not be able to delete GroupAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No WRITE permission on entity GroupAuthority");
		}
	}

	@Test
	public void testLogoutWithoutTokenFails()
	{
		LOG.info("Test that logout without token throws an exception...");
		try
		{
			client.logout(null);
			fail("Cannot logout without token in the header");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), BAD_REQUEST);
			assertEquals(parseErrorMessage(actual), "Missing token in header");
		}
	}

	@Test
	public void testLogoutInvalidatesToken()
	{
		LOG.info("Test that succesful logout invalidates the token");
		String token = client.login("test", "secret").getToken();
		client.logout(token);
		try
		{
			client.get(token, "LoggingEvent");
			fail("logout should invalidate token");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No READ permission on entity LoggingEvent");
		}
	}
}
