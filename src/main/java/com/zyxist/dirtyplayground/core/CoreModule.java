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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import static com.zyxist.dirtyplayground.core.CoreExtensions.bindExistingService;
import com.zyxist.dirtyplayground.core.database.DatabaseManager;
import com.zyxist.dirtyplayground.core.database.DatabaseManagerImpl;
import com.zyxist.dirtyplayground.core.journal.Journal;
import com.zyxist.dirtyplayground.core.journal.JournalImpl;
import com.zyxist.dirtyplayground.core.journal.JournalWriter;
import com.zyxist.dirtyplayground.core.journal.MongoJournalWriter;
import com.zyxist.dirtyplayground.core.svc.ServiceComposer;
import com.zyxist.dirtyplayground.core.svc.ServiceRunner;
import com.zyxist.dirtyplayground.core.svc.ServiceRunnerFactory;
import com.zyxist.dirtyplayground.core.svc.ServiceRunnerImpl;
import com.zyxist.dirtyplayground.core.svc.StartableService;
import com.zyxist.dirtyplayground.core.web.VertxProvider;
import com.zyxist.dirtyplayground.core.web.WebStack;
import com.zyxist.dirtyplayground.core.web.WebStackImpl;
import io.vertx.core.Vertx;
import java.util.Set;
import static com.zyxist.dirtyplayground.core.CoreExtensions.bindSingletonService;
import javax.inject.Singleton;

public class CoreModule extends AbstractModule {

	@Override
	protected void configure() {
		Multibinder<StartableService> serviceBinder = Multibinder.newSetBinder(binder(), StartableService.class, CoreService.class);
		
		bind(Vertx.class).toProvider(VertxProvider.class);
		
		install(new FactoryModuleBuilder()
			.implement(ServiceRunner.class, ServiceRunnerImpl.class)
			.build(ServiceRunnerFactory.class));
		bind(ServiceComposer.class);
		
		bind(VertxServiceImpl.class).in(Singleton.class);
		bindExistingService(binder(), CoreService.class, VertxServiceImpl.class);
		
		bindSingletonService(binder(), CoreService.class, Journal.class, JournalImpl.class);
		bindSingletonService(binder(), CoreService.class, WebStack.class, WebStackImpl.class);
		bindSingletonService(binder(), CoreService.class, DatabaseManager.class, DatabaseManagerImpl.class);
		
		bind(JournalWriter.class).to(MongoJournalWriter.class);
	}

	@Provides
	public Bootstrap provideBootstrap(ServiceRunnerFactory factory, @CoreService Set<StartableService> services) {
		return new Bootstrap(factory.createServiceRunner(services));
	}
}
