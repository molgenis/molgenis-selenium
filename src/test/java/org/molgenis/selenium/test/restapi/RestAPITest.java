package org.molgenis.selenium.test.restapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

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
		client.create(adminToken, "sys_sec_UserAuthority", ImmutableMap.of("role",
				"ROLE_ENTITY_" + permission + "_" + entityName.toUpperCase(), "User", id));
	}

	public void createUser()
	{
		try
		{
			client.create(adminToken, "sys_sec_User", testUser);
			grant("test", "sys_scr_Script", "WRITE");
			grant("test", "sys_scr_ScriptType", "READ");
			grant("test", "sys_sec_UserAuthority", "COUNT");
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
			QueryResponse response = client.queryEquals(adminToken, "sys_sec_UserAuthority", "User", id);
			for (Map<String, Object> item : response.getItems())
			{
				System.out.println("Deleting UserAuthority" + item);
				client.delete(adminToken, "sys_sec_UserAuthority", item.get("id"));
			}

			response = client.queryEquals(adminToken, "sys_sec_Token", "User", id);
			for (Map<String, Object> item : response.getItems())
			{
				System.out.println("Deleting sys_sec_Token" + item);
				client.delete(adminToken, "sys_sec_Token", item.get("id"));
			}
			client.delete(adminToken, "sys_sec_User", id);
		}
	}

	protected Object getUserId(String username)
	{
		QueryResponse response = client.queryEquals(adminToken, "sys_sec_User", "username", username);
		if (response.getItems().isEmpty())
		{
			return null;
		}
		Object userId = response.getItems().get(0).get("id");
		return userId;
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
			client.get(null, "sys_scr_Script");
			fail("anonymous user should not be able to retrieve Script");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No [COUNT] permission on entity [sys_scr_Script]");
		}

		LOG.info("Test that anonymous user cannot read ScriptType...");
		try
		{
			client.get(null, "sys_scr_ScriptType");
			fail("anonymous user should not be able to retrieve ScriptType");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No [COUNT] permission on entity [sys_scr_ScriptType]");
		}

		LOG.info("Test that anonymous user cannot read UserAuthority...");
		try
		{
			client.get(null, "sys_sec_UserAuthority");
			fail("anonymous user should not be able to retrieve sys_sec_UserAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No [COUNT] permission on entity [sys_sec_UserAuthority]");
		}

		LOG.info("Test that anonymous user cannot read GroupAuthority...");
		try
		{
			client.get(null, "sys_sec_GroupAuthority");
			fail("anonymous user should not be able to retrieve sys_sec_GroupAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No [COUNT] permission on entity [sys_sec_GroupAuthority]");
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

		LOG.info("Test that test user can read Script...");
		client.get(token, "sys_scr_Script");

		LOG.info("Test that test user can read ScriptType...");
		client.get(token, "sys_scr_ScriptType");

		LOG.info("Test that test user cannot read UserAuthority...");
		try
		{
			client.get(token, "sys_sec_UserAuthority");
			fail("test user should not be able to retrieve UserAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No [READ] permission on entity [sys_sec_UserAuthority]");
		}

		LOG.info("Test that test user cannot read sys_sec_GroupAuthority...");
		try
		{
			client.get(token, "sys_sec_GroupAuthority");
			fail("test user should not be able to retrieve sys_sec_GroupAuthority");
		}
		catch (HttpClientErrorException actual)
		{
			assertEquals(actual.getStatusCode(), UNAUTHORIZED);
			assertEquals(parseErrorMessage(actual), "No [COUNT] permission on entity [sys_sec_GroupAuthority]");
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
			assertEquals(parseErrorMessage(actual), "No [WRITE] permission on entity [sys_sec_GroupAuthority]");
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
			assertEquals(parseErrorMessage(actual), "No [COUNT] permission on entity LoggingEvent");
		}
	}
}
