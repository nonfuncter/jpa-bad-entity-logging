package com.nonfunc.jpa.bad.logging;

/*
 * #%L
 * em
 * %%
 * Copyright (C) 2016 nonfunc.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FooService {

	private static final Logger logger = Logger.getLogger(FooService.class.getName());
	EntityManager entityManager;
	
	public FooService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public void setLevel(Level level) {
		logger.setLevel(level);
	}
	
	public long execute(boolean detach, boolean proxyAwareToString) {
		
		boolean log = logger.isLoggable(Level.FINE);		
		long result = 0;		
		List<Foo> foos = findFoos(entityManager);
		
		if (detach) entityManager.clear();
				
		for (Foo foo : foos) {
			
			if (log) log(proxyAwareToString, foo);
			
			//Some random logic here
			result += foo.getCode();
		}
		
		return result;
	}
	
	private void log(boolean proxyAwareToString, Foo foo) {
		if (proxyAwareToString) {
			logger.fine(String.format("Processing: %s", foo.proxyAwareToString()));
		} else {
			logger.fine(String.format("Processing: %s", foo));
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Foo> findFoos(EntityManager entityManager) {
		Query query = entityManager.createQuery("SELECT f FROM Foo f", Foo.class);
		return (List<Foo>) query.getResultList();		
	}
}