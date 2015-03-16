package org.molgenis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration that reads system property or environment variable and puts all properties in it into the Environment.
 */
@Configuration
@PropertySource(value = "file:${jenkins_propsfile}", name = "jenkins")
public class JenkinsConfig
{
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

}