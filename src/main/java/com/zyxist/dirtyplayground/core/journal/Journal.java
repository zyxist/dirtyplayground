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

import com.zyxist.dirtyplayground.core.journal.event.JournalEvent;
import com.zyxist.dirtyplayground.core.journal.event.ResponseEvent;
import java.util.function.Consumer;

public interface Journal {
	public void publish(JournalEvent event);
	public <T extends JournalEvent, R extends ResponseEvent> void publishAndThen(T event, Class<R> responseType, Consumer<R> consumer);
	public <T> void consume(Class<? extends JournalEvent> event, Consumer<T> consumer);
}
