/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zyxist.dirtyplayground.core;

import com.zyxist.dirtyplayground.core.svc.ProvidesService;
import com.zyxist.dirtyplayground.core.svc.StartableService;
import io.vertx.core.Vertx;
import java.util.Objects;
import javax.inject.Inject;

@ProvidesService(Vertx.class)
public class VertxServiceImpl implements StartableService {
	private final Vertx vertx;
	
	@Inject
	public VertxServiceImpl(Vertx vertx) {
		this.vertx = Objects.requireNonNull(vertx);
	}

	@Override
	public void stop() {
		this.vertx.close();
	}
}
