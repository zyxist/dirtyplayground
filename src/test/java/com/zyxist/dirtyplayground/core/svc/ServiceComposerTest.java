/*
 * Copyright 2016 Tomasz Jędrzejewski <http://www.zyxist.com/>.
 *
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
 */

package com.zyxist.dirtyplayground.core.svc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class ServiceComposerTest {
	private ServiceComposer composer = new ServiceComposer();
	private Foo foo = new Foo();
	private Bar bar = new Bar();
	private Joe joe = new Joe();
	private Moo moo = new Moo();
	
	@Test
	public void testComposingSingleService() {
		// Given
		Set<StartableService> services = ImmutableSet.of(foo);
		
		// When
		List<StartableService> composed = composer.compose(services);
		
		// Then
		assertEquals(ImmutableList.of(foo), composed);
	}
	
	@Test
	public void testComposingTwoIndependentServices() {
		// Given
		Set<StartableService> services = ImmutableSet.of(foo, bar);
		
		// When
		List<StartableService> composed = composer.compose(services);
		
		// Then
		assertEquals(2, composed.size());
		assertTrue(composed.contains(foo));
		assertTrue(composed.contains(bar));
	}

	@Test
	public void testComposingServiceWithOneDependency() {
		// Given
		Set<StartableService> services = ImmutableSet.of(bar, joe);
		
		// When
		List<StartableService> composed = composer.compose(services);
		
		// Then
		assertEquals(ImmutableList.of(bar, joe), composed);
	}
	
	@Test
	public void testComposingServiceWithTwoDependencies() {
		// Given
		Set<StartableService> services = ImmutableSet.of(bar, joe, moo);
		
		// When
		List<StartableService> composed = composer.compose(services);
		
		// Then
		assertEquals(ImmutableList.of(bar, joe, moo), composed);
	}
	
	@ProvidesService(Foo.class)
	class Foo implements StartableService {
	}
	
	@ProvidesService(Bar.class)
	class Bar implements StartableService {
	}
	
	@ProvidesService(Joe.class)
	@RequiresServices(Bar.class)
	class Joe implements StartableService {
	}
	
	@RequiresServices({Bar.class, Joe.class})
	@ProvidesService(Moo.class)
	class Moo implements StartableService {
	}
}
