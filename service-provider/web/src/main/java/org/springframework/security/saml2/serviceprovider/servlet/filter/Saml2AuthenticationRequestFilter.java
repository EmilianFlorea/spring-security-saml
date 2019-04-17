/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.security.saml2.serviceprovider.servlet.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.saml2.model.authentication.Saml2AuthenticationRequest;
import org.springframework.security.saml2.serviceprovider.binding.Saml2HttpMessageData;
import org.springframework.security.saml2.serviceprovider.servlet.authentication.Saml2AuthenticationRequestResolver;
import org.springframework.security.saml2.serviceprovider.servlet.binding.Saml2HttpMessageResponder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class Saml2AuthenticationRequestFilter extends OncePerRequestFilter {

	private final Saml2AuthenticationRequestResolver resolver;
	private final Saml2HttpMessageResponder responder;
	private final RequestMatcher matcher;

	public Saml2AuthenticationRequestFilter(Saml2AuthenticationRequestResolver resolver,
											Saml2HttpMessageResponder responder,
											RequestMatcher matcher) {
		this.resolver = resolver;
		this.matcher = matcher;
		this.responder = responder;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		if (matcher.matches(request)) {
			logger.debug("Creating SAML2 SP Authentication Request.");
			Saml2HttpMessageData mvcModel = resolveAuthenticationRequest(request, response);
			responder.sendSaml2Message(mvcModel, request, response);
			logger.debug("SAML2 SP Authentication Request Sent to Browser");
		}
		else {
			filterChain.doFilter(request, response);
		}
	}

	private Saml2HttpMessageData resolveAuthenticationRequest(HttpServletRequest request,
															  HttpServletResponse response) {
		Saml2AuthenticationRequest authn = resolver.resolve(request, response);
		return new Saml2HttpMessageData(
			authn,
			null,
			authn.getDestination(),
			request.getParameter("RelayState")
		);
	}


}
