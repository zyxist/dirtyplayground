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

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Used for resolving the dependencies between the startable services ({@link StartableService})
 * to ensure they start in correct order. The annotation shall be put on a class that implements
 * some service. The service interface is used as a discriminator of the delivered functionality.
 * 
 * @author Tomasz Jędrzejewski
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ProvidesService {
	Class<?> value();
}
