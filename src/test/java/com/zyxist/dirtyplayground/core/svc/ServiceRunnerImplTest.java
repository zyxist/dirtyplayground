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
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceRunnerImplTest {
	private ServiceComposer composer;
	private StartableService svc1;
	private StartableService svc2;
	private StartableService svc3;
	private Runnable someCode;
	
	@Before
	public void prepareMocks() {
		composer = mock(ServiceComposer.class);
		svc1 = mock(StartableService.class);
		svc2 = mock(StartableService.class);
		svc3 = mock(StartableService.class);
		someCode = mock(Runnable.class);
	}
	
	
	@Test
	public void testAllServicesStart() {
		// Given
		List<StartableService> svcs = servicesWillLaunchInOrder(svc1, svc2, svc3);
		InOrder inOrder = inOrder(svc1, svc2, svc3, someCode);
		ServiceRunnerImpl serviceRunner = new ServiceRunnerImpl(composer, ImmutableSet.copyOf(svcs));
		
		// When
		serviceRunner.execute(someCode);
		
		// Then
		inOrder.verify(svc1).start();
		inOrder.verify(svc2).start();
		inOrder.verify(svc3).start();
		inOrder.verify(someCode).run();
		inOrder.verify(svc3).stop();
		inOrder.verify(svc2).stop();
		inOrder.verify(svc1).stop();
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testStartupTerminatedByException() {
		// Given
		List<StartableService> svcs = servicesWillLaunchInOrder(svc1, svc2, svc3);
		InOrder inOrder = inOrder(svc1, svc2, svc3, someCode);
		ServiceRunnerImpl serviceRunner = new ServiceRunnerImpl(composer, ImmutableSet.copyOf(svcs));
		doThrow(new RuntimeException("Fail!")).when(svc2).start();
		
		// When
		serviceRunner.execute(someCode);
		
		// Then
		inOrder.verify(svc1).start();
		inOrder.verify(svc2).start();
		inOrder.verify(svc1).stop();
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testExecutionTerminatedByException() {
		// Given
		List<StartableService> svcs = servicesWillLaunchInOrder(svc1, svc2, svc3);
		InOrder inOrder = inOrder(svc1, svc2, svc3, someCode);
		ServiceRunnerImpl serviceRunner = new ServiceRunnerImpl(composer, ImmutableSet.copyOf(svcs));
		doThrow(new RuntimeException("Fail!")).when(someCode).run();
		
		// When
		try {
			serviceRunner.execute(someCode);
			fail("Exception not thrown!");
		} catch (RuntimeException exception) {
			assertEquals("Fail!", exception.getMessage());
		}
		
		// Then
		inOrder.verify(svc1).start();
		inOrder.verify(svc2).start();
		inOrder.verify(svc3).start();
		inOrder.verify(someCode).run();
		inOrder.verify(svc3).stop();
		inOrder.verify(svc2).stop();
		inOrder.verify(svc1).stop();
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testExceptionWhileStoppingServices() {
		// Given
		List<StartableService> svcs = servicesWillLaunchInOrder(svc1, svc2, svc3);
		InOrder inOrder = inOrder(svc1, svc2, svc3, someCode);
		ServiceRunnerImpl serviceRunner = new ServiceRunnerImpl(composer, ImmutableSet.copyOf(svcs));
		doThrow(new RuntimeException("Fail!")).when(svc2).stop();
		
		// When
		serviceRunner.execute(someCode);
		
		// Then
		inOrder.verify(svc1).start();
		inOrder.verify(svc2).start();
		inOrder.verify(svc3).start();
		inOrder.verify(someCode).run();
		inOrder.verify(svc3).stop();
		inOrder.verify(svc2).stop();
		inOrder.verify(svc1).stop();
		inOrder.verifyNoMoreInteractions();
	}
	
	public List<StartableService> servicesWillLaunchInOrder(StartableService ... svcs) {
		Set<StartableService> inputSet = ImmutableSet.copyOf(svcs);
		List<StartableService> finalList = ImmutableList.copyOf(svcs);
		when(composer.compose(inputSet)).thenReturn(finalList);
		return finalList;
	}
}
