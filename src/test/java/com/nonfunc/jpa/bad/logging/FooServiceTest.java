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

import static com.airhacks.rulz.em.EntityManagerProvider.persistenceUnit;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

import com.airhacks.rulz.em.EntityManagerProvider;

public class FooServiceTest {

	private static Logger logger = Logger.getLogger(FooServiceTest.class.getName());
	private static EntityManager entityManager;
	private static EntityTransaction transaction;
	private static FooService fooService;
	
	@ClassRule
	public static EntityManagerProvider provider = persistenceUnit("bad-logging");

	@Rule
	public Stopwatch stopwatch = new Stopwatch() {
		@Override
		protected void finished(long nanos, Description description) {
			log(description.getMethodName(), nanos);
		}
	};

	@BeforeClass
	public static void setUp() throws Exception {
		entityManager = provider.em();
		transaction = provider.tx();
		transaction.begin();

		FooFactory.create(entityManager, 100000);
		entityManager.flush();
		entityManager.clear();

		fooService = new FooService(entityManager);		
	}

	@AfterClass
	public static void tearDown() throws Exception {
		transaction.rollback();
	}

	@Before
	public void before() {
		HibernateInterceptor.loads = 0L;
		entityManager.clear();
	}
	
	@Test
	public void testExecuteAttached() {
		fooService.setLevel(Level.INFO);
		fooService.execute(false, false);
	}

	@Test
	public void testExecuteAttachedLoggingFine() {
		fooService.setLevel(Level.FINE);
		fooService.execute(false, false);
	}
	
	@Test
	public void testExecuteAttachedLoggingFineProxyAware() {
		fooService.setLevel(Level.FINE);
		fooService.execute(false, true);
	}
	
	@Test
	public void testExecuteDetached() {
		fooService.setLevel(Level.INFO);
		fooService.execute(true, false);
	}	

	@Test(expected=org.hibernate.LazyInitializationException.class)
	public void testExecuteDetachedLoggingFine() {
		fooService.setLevel(Level.FINE);
		fooService.execute(true, false);
	}
	
	@Test
	public void testExecuteDetachedLoggingFineProxyAware() {
		fooService.setLevel(Level.FINE);
		fooService.execute(true, true);
	}
	
	private static void log(String test, long nanos) {
		logger.info(String.format("Test %s took %d milliseconds with %d entity loads.", 
				test, TimeUnit.NANOSECONDS.toMillis(nanos), HibernateInterceptor.loads));		
	}
}
