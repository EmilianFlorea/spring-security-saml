/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package sample.config;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.provider.provisioning.SamlProviderProvisioning;
import org.springframework.security.saml.provider.service.SamlAuthenticationRequestFilter;
import org.springframework.security.saml.provider.service.ServiceProvider;
import org.springframework.security.saml.provider.service.ServiceProviderMetadataFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final SamlProviderProvisioning<ServiceProvider> provisioning;

	public SecurityConfig(SamlProviderProvisioning<ServiceProvider> provisioning) {
		this.provisioning = provisioning;
	}

	@Bean
	public Filter metadataFilter() {
		return new ServiceProviderMetadataFilter(provisioning);
	}

	@Bean
	public Filter authenticationRequestFilter() {
		return new SamlAuthenticationRequestFilter(provisioning);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.addFilterAfter(metadataFilter(), BasicAuthenticationFilter.class)
			.addFilterAfter(authenticationRequestFilter(), metadataFilter().getClass())
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/saml/sp/**").permitAll() //TODO - based on configuration
			.anyRequest().authenticated()
			.and()
			.formLogin().loginPage("/saml/sp/select") //TODO - based on configuration
		;
	}
}
