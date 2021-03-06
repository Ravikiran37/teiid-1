/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.jboss;

import java.io.Serializable;
import java.security.Principal;

import javax.security.auth.Subject;

import org.jboss.security.SecurityContext;
import org.jboss.security.SubjectInfo;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.security.SecurityHelper;

public class JBossSecurityHelper implements SecurityHelper, Serializable {
	private static final long serialVersionUID = 3598997061994110254L;

	@Override
	public Object associateSecurityContext(Object newContext) {
		SecurityContext context = SecurityActions.getSecurityContext();
		if (newContext != context) {
			SecurityActions.setSecurityContext((SecurityContext)newContext);
		}
		return context;
	}

	@Override
	public void clearSecurityContext() {
		SecurityActions.clearSecurityContext();
	}
	
	@Override
	public Object getSecurityContext() {
		return SecurityActions.getSecurityContext();
	}	
	
	@Override
	public Object createSecurityContext(String securityDomain, Principal p, Object credentials, Subject subject) {
		return SecurityActions.createSecurityContext(p, credentials, subject, securityDomain);
	}

	@Override
	public Subject getSubjectInContext(String securityDomain) {
		SecurityContext sc = SecurityActions.getSecurityContext();
		if (sc != null && sc.getSecurityDomain().equals(securityDomain)) {
			SubjectInfo si = sc.getSubjectInfo();
			Subject subject = si.getAuthenticatedSubject();
			return subject;
		}		
		return null;
	}

	@Override
	public boolean sameSubject(String securityDomain, Object context, Subject subject) {
		if (context == null) {
			throw new TeiidRuntimeException(IntegrationPlugin.Event.TEIID50090, IntegrationPlugin.Util.gs(IntegrationPlugin.Event.TEIID50090));
		}
		SecurityContext previousContext = (SecurityContext)context;
		Subject previousUser = previousContext.getSubjectInfo().getAuthenticatedSubject();
		
		SecurityContext currentContext = SecurityActions.getSecurityContext();
		if (currentContext != null && currentContext.getSecurityDomain().equals(securityDomain)) {
			Subject currentUser = currentContext.getSubjectInfo().getAuthenticatedSubject();
			if (previousUser.equals(currentUser)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getSecurityDomain(Object context) {
		return ((SecurityContext)context).getSecurityDomain();
	}
	
}
