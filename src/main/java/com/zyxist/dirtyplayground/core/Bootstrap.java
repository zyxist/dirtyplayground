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

import com.zyxist.dirtyplayground.core.svc.ServiceRunner;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
	
	private final ServiceRunner serviceRunner;
	private final CountDownLatch untilStopped = new CountDownLatch(1);
	
	@Inject
	public Bootstrap(ServiceRunner serviceRunner) {
		this.serviceRunner = Objects.requireNonNull(serviceRunner);
	}
	
	public void execute() {
		this.serviceRunner.execute(() -> {
			try {
				launchStopper();
				untilStopped.await();
			} catch (InterruptedException exception) {
				log.info("Process interrupted");
			}
		});
	}

	private void launchStopper() {
		Runtime.getRuntime().addShutdownHook(new Thread("Halter") {
			@Override
			public void run() {
				untilStopped.countDown();
			}
		});
	}
}
