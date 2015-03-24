/*
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.runtime.subsystems;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.nodes.Node;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.jruby.truffle.runtime.RubyCallStack;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.backtrace.Backtrace;
import org.jruby.truffle.runtime.core.RubyThread;
import org.jruby.truffle.runtime.util.Consumer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@SuppressWarnings("restriction")
public class InstrumentationServerManager {

    private final RubyContext context;
    private final int port;

    private HttpServer server;
    private volatile boolean shuttingDown = false;

    public InstrumentationServerManager(RubyContext context, int port) {
        this.context = context;
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.createContext("/stacks", new HttpHandler() {

            @Override
            public void handle(HttpExchange httpExchange) {
                throw new UnsupportedOperationException("Safepoints");
            }

        });

        server.createContext("/break", new HttpHandler() {

            @Override
            public void handle(HttpExchange httpExchange) {
                throw new UnsupportedOperationException("Safepoints");
            }

        });

        server.start();
    }

    public void shutdown() {
        if (server != null) {
            shuttingDown = true;
            // Leave it some time to send the remaining bytes.
            server.stop(1);
        }
    }

}
