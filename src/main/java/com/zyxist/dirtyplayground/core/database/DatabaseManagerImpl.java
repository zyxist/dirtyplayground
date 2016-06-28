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

package com.zyxist.dirtyplayground.core.database;

import com.zyxist.dirtyplayground.core.svc.ProvidesService;
import com.zyxist.dirtyplayground.core.svc.RequiresServices;
import com.zyxist.dirtyplayground.core.svc.ServiceException;
import com.zyxist.dirtyplayground.core.svc.StartableService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;

@RequiresServices({Vertx.class})
@ProvidesService(DatabaseManager.class)
public class DatabaseManagerImpl implements DatabaseManager, StartableService {
	@Inject
	private Vertx vertx;
	private boolean connectionSuccessful = false;
	private MongoClient mongo;
	
	@Override
	public void start() throws InterruptedException, ServiceException {
		CountDownLatch untilStarted = new CountDownLatch(1);
		mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "journal"));
		mongo.save("dummy", new JsonObject(), (res) -> {
			connectionSuccessful = res.succeeded();
			untilStarted.countDown();
		});
		untilStarted.await();
		if (!connectionSuccessful) {
			mongo.close();
			throw new ServiceException("Cannot start MongoDB client: connection failed.");
		}
	}
	
	@Override
	public void stop() {
		if (connectionSuccessful) {
			mongo.close();
		}
	}

	@Override
	public MongoClient mongo() {
		return mongo;
	}
}
