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
import com.zyxist.dirtyplayground.core.journal.event.ResponseEvent;
import com.zyxist.dirtyplayground.core.svc.ProvidesService;
import com.zyxist.dirtyplayground.core.svc.RequiresServices;
import com.zyxist.dirtyplayground.core.svc.StartableService;
import java.util.function.Consumer;
import javax.inject.Inject;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

@RequiresServices({DatabaseManager.class})
@ProvidesService(Journal.class)
public class JournalImpl implements Journal, StartableService {
	private Subject<JournalEvent, JournalEvent> subject;
	private Scheduler scheduler;
	@Inject
	private JournalWriter journalWriter;
	
	@Override
	public void start() {
		subject = new SerializedSubject<>(PublishSubject.create());
		scheduler = Schedulers.newThread();
		subject.subscribe(journalWriter::persist);
	}
	
	@Override
	public void stop() {
		subject.onCompleted();
	}

	@Override
	public void publish(JournalEvent event) {
		subject.onNext(event);
	}

	@Override
	public <T extends JournalEvent, R extends ResponseEvent> void publishAndThen(T event, Class<R> responseType, Consumer<R> consumer) {
		subject
			.filter((ev) -> responseType.isAssignableFrom(ev.getClass()) && ((R) ev).isRespondingTo(event))
			.map((obj) -> (R) obj)
			.take(1)
			.subscribe((obj) -> consumer.accept(obj));
		subject.onNext(event);
	}

	@Override
	public <T> void consume(Class<? extends JournalEvent> eventClass, Consumer<T> consumer) {
		subject
			.observeOn(scheduler)
			.filter((ev) -> eventClass.isAssignableFrom(ev.getClass()))
			.map((obj) -> (T) obj)
			.subscribe((obj) -> consumer.accept(obj));
	}
}
