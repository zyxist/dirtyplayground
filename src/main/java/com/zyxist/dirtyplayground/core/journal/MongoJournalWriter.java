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

package com.zyxist.dirtyplayground.core.journal;

import com.zyxist.dirtyplayground.core.database.DatabaseManager;
import com.zyxist.dirtyplayground.core.journal.event.JournalEvent;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoJournalWriter implements JournalWriter {
	private static final Logger log = LoggerFactory.getLogger(MongoJournalWriter.class);
	
	@Inject
	private DatabaseManager db;

	@Override
	public void persist(JournalEvent event) {
		log.debug("Persisting event...");
		db.mongo().save("journal", new JsonObject(Json.encode(event)), (res) -> {});
	}
}
