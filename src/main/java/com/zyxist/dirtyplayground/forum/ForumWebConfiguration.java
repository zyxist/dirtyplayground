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

package com.zyxist.dirtyplayground.forum;

import com.zyxist.dirtyplayground.core.web.WebConfiguration;
import com.zyxist.dirtyplayground.core.web.WebStack;


public class ForumWebConfiguration implements WebConfiguration {
	@Override
	public void configure(WebStack webstack) {
		webstack.router().get("/forum").handler((ctx) -> {
			ctx.response()
				.setChunked(true)
				.write("Hi, universe!")
				.end();
		});
	}
}
