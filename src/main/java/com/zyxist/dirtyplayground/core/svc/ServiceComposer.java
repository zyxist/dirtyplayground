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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of Kahn's algorithm for topological sorting that produces the order the services
 * should be started in to satisfy their inter-dependencies. The annotations {@link ProvidesService}
 * and {@link RequiresServices} are used for constructing the graph to be sorted.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ServiceComposer {
	
	public List<StartableService> compose(Set<StartableService> unorderedServices) {
		Multimap<Class<?>, StartableService> servicesReachableFrom = findServiceReachabilityGraph(unorderedServices);
		Deque<StartableService> toProcess = findRoots(unorderedServices);
		List<StartableService> output = new ArrayList<>(unorderedServices.size());
		
		while (!toProcess.isEmpty()) {
			StartableService svc = toProcess.poll();
			output.add(svc);
			
			findProvidedService(svc).ifPresent((provided) -> {
				Iterator<StartableService> reachableServices = servicesReachableFrom.get(provided).iterator();
				while (reachableServices.hasNext()) {
					StartableService reachable = reachableServices.next();
					reachableServices.remove();
					if (!servicesReachableFrom.containsValue(reachable)) {
						toProcess.add(reachable);
					}
				}
			});
		}
		if (!servicesReachableFrom.isEmpty()) {
			throw new RuntimeException("Cycle detected in services!");
		}
		return output;
	}

	private Deque<StartableService> findRoots(Set<StartableService> unorderedServices) {
		Deque<StartableService> output = new LinkedList<>();
		main:for (StartableService svc: unorderedServices) {
			RequiresServices def = svc.getClass().getAnnotation(RequiresServices.class);
			if (null != def && def.value().length > 0) {
				continue main;
			}
			output.add(svc);
		}
		return output;
	}
	
	private Multimap<Class<?>, StartableService> findServiceReachabilityGraph(Set<StartableService> unorderedServices) {
		Multimap<Class<?>, StartableService> servicesReachableFrom = LinkedHashMultimap.create();
		for (StartableService svc: unorderedServices) {
			RequiresServices def = svc.getClass().getAnnotation(RequiresServices.class);
			if (null != def) {
				for (Class<?> required: def.value()) {
					servicesReachableFrom.put(required, svc);
				}
			}
		}
		return servicesReachableFrom;
	}
	
	private Optional<Class<?>> findProvidedService(StartableService svc) {
		ProvidesService def = svc.getClass().getAnnotation(ProvidesService.class);
		if (null != def) {
			return Optional.of(def.value());
		}
		return Optional.empty();
	}
}
