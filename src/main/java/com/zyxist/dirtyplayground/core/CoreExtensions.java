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
package com.zyxist.dirtyplayground.core;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;
import com.zyxist.dirtyplayground.core.svc.StartableService;
import com.zyxist.dirtyplayground.core.web.WebConfiguration;
import java.lang.annotation.Annotation;
import javax.inject.Singleton;

/**
 * Helper methods for registering various things in Guice, where the binding is
 * complex or non-trivial (i.e. multibindings).
 * 
 * @author Tomasz Jędrzejewski
 */
public final class CoreExtensions {
	public static void bindWebConfiguration(Binder binder, Class<? extends WebConfiguration> webCfgImpl) {
		Multibinder<WebConfiguration> cfgBinder = Multibinder.newSetBinder(binder, WebConfiguration.class);
		cfgBinder.addBinding().to(webCfgImpl);
	}
	
	public static void bindExistingService(Binder binder, Class<? extends Annotation> startupPlace, Class<? extends StartableService> serviceImpl) {
		Multibinder<StartableService> serviceBinder = Multibinder.newSetBinder(binder, StartableService.class, startupPlace);
		serviceBinder.addBinding().to(Key.get(serviceImpl));
	}
	
	/**
	 * Binds the service implementation to the service interface using the singleton scope, and additionally registers
	 * it as a startable service. Use {@link #bindExistingService(com.google.inject.Binder, java.lang.Class, java.lang.Class)}
	 * if you want to bind the service implementation on your own, and just register it as a startable service.
	 * 
	 * @param <T>
	 * @param <I>
	 * @param binder
	 * @param startupPlace Place where the services are run
	 * @param serviceIfc 
	 * @param serviceImpl Implementation class of the service, must implement <tt>StartableService</tt>, too. 
	 */
	public static <T, I extends T> void bindSingletonService(Binder binder, Class<? extends Annotation> startupPlace, Class<T> serviceIfc, Class<I> serviceImpl) {
		if (! StartableService.class.isAssignableFrom(serviceImpl)) {
			throw new IllegalArgumentException("The '"+serviceImpl.getCanonicalName()+"' must implement StartableService interface in order to be registered as a startable singleton service.");
		}
		binder.bind(serviceImpl).in(Singleton.class);
		binder.bind(serviceIfc).to(Key.get(serviceImpl));
		bindExistingService(binder, startupPlace, (Class<? extends StartableService>) serviceImpl);
	}
}
