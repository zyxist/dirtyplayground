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

/**
 * As DI providers have to be simple and deterministic, we cannot put there any
 * initialization code. <tt>StartableService</tt> allows us making a service with
 * start/stop sequence.
 *
 * <p>How to use:</p>
 * <ol>
 *  <li>Create an interface <tt>Foo</tt> for the service</li>
 *  <li>Create an implementation of <tt>Foo</tt> and <tt>StartableService</tt></li>
 *  <li>Use {@link ProvidesService} and {@link RequiresServices} annotations on the implementation class to specify the
 *    dependencies between services.</li>
 *  <li>Register the service using {@link com.zyxist.dirtyplayground.core.CoreExtensions#bindSingletonService(com.google.inject.Binder, java.lang.Class, java.lang.Class, java.lang.Class)}</li>
 * </ol>
 * 
 * @author Tomasz Jędrzejewski
 */
public interface StartableService {
	default public void start() {
	}
	
	default public void stop() {
	}
}
