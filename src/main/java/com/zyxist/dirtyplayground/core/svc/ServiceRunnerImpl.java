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

import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRunnerImpl implements ServiceRunner {
	private static final Logger log = LoggerFactory.getLogger(ServiceRunnerImpl.class);
	private final ServiceComposer serviceComposer;
	private final Set<StartableService> services;
	
	@Inject
	public ServiceRunnerImpl(ServiceComposer composer, @Assisted Set<StartableService> services) {
		this.serviceComposer = Objects.requireNonNull(composer);
		this.services = Objects.requireNonNull(services);
	}

	@Override
	public void execute(Runnable serviceAwareCode) {
		List<StartableService> orderedServices = serviceComposer.compose(services);
		List<StartableService> stopOrderedServices = Lists.reverse(orderedServices);
		Set<StartableService> correctlyStarted = new HashSet<>();
		
		try {
			if (startServices(orderedServices, correctlyStarted)) {
				serviceAwareCode.run();
			}
		} finally {
			stopServices(stopOrderedServices, correctlyStarted);
		}
	}
	
	private boolean startServices(List<StartableService> services, Set<StartableService> correctlyStarted) {
		for (StartableService svc: services) {
			String name = svc.getClass().getSimpleName();
			try {
				log.info("Starting service: " + name);
				svc.start();
				log.info("Started service: " + name);
				correctlyStarted.add(svc);
			} catch (Exception exception) {
				log.error("Service " + name + " failed during startup.", exception);
				return false;
			}
		}
		return true;
	}
	
	private void stopServices(List<StartableService> services, Set<StartableService> correctlyStarted) {
		for (StartableService svc: services) {
			if (correctlyStarted.contains(svc)) {
				String name = svc.getClass().getSimpleName();
				try {
					log.info("Stopping service: " + name);
					svc.stop();
					log.info("Stopped service: " + name);
				} catch (Exception exception) {
					log.error("Service " + name + " failed during shutdown.", exception);
				}
			}
		}
	}
}
