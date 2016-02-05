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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

public class BarFactory {

	private BarFactory() {}
	
	public static List<Bar> create(EntityManager entityManager, long num) {
		List<Bar> result = new ArrayList<>();
		for (long i = 0; i < num; i++) {
			Bar bar = new Bar();
			bar.setCode(i);
			bar.setDescription(String.valueOf(i));
			entityManager.persist(bar);
			result.add(bar);
		}
		return result;
	}	
}
