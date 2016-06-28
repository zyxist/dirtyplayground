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

package com.zyxist.dirtyplayground.core.web;

import com.zyxist.dirtyplayground.core.journal.Journal;
import com.zyxist.dirtyplayground.core.svc.ProvidesService;
import com.zyxist.dirtyplayground.core.svc.RequiresServices;
import com.zyxist.dirtyplayground.core.svc.StartableService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;

@RequiresServices({Journal.class, Vertx.class})
@ProvidesService(WebStack.class)
public class WebStackImpl implements WebStack, StartableService {
	private final Vertx vertx;
	private final Set<WebConfiguration> configs;
	
	private HttpServer server;
	private Router router;
	
	@Inject
	public WebStackImpl(Vertx vertx, Set<WebConfiguration> configs) {
		this.vertx = vertx;
		this.configs = Objects.requireNonNull(configs);
	}
	
	@Override
	public void start() {
		server = vertx.createHttpServer();
		router = Router.router(vertx);
		server.requestHandler(router::accept).listen(8080);
		
		for (WebConfiguration config: configs) {
			config.configure(this);
		}
	}
	
	@Override
	public void stop() {
		server.close();
	}

	@Override
	public HttpServer httpServer() {
		if (null == server) {
			throw new IllegalStateException("WebStack service not started!");
		}
		return server;
	}

	@Override
	public Router router() {
		if (null == router) {
			throw new IllegalStateException("Router service not started!");
		}
		return router;
	}

}
