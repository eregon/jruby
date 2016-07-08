/*
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.language.objects.shared;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.HiddenKey;
import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jruby.truffle.RubyContext;
import org.jruby.truffle.Layouts;
import org.jruby.truffle.language.Options;
import org.jruby.truffle.language.objects.ObjectGraph;

public abstract class SharedObjects {

    private static final HiddenKey SHARED = new HiddenKey("shared");
    private static final Object SHARED_VALUE = true;
    private static final Location SHARED_LOCATION = Layout.createLayout().createAllocator().constantLocation(SHARED_VALUE);
    public static final Property SHARED_PROPERTY = Property.create(SHARED, SHARED_LOCATION, 0);

    public static void startSharing(RubyContext context) {
        shareContextRoots(context);
    }

    private static void shareContextRoots(RubyContext context) {
        final Deque<DynamicObject> stack = new ArrayDeque<>();

        // Share global variables (including new ones)
        for (DynamicObject object : context.getCoreLibrary().getGlobalVariables().dynamicObjectValues()) {
            stack.push(object);
        }

        // Share all named modules and constants (including the shared TOPLEVEL_BINDING)
        stack.push(context.getCoreLibrary().getObjectClass());

        // Share all threads since they are accessible via Thread.list
        for (DynamicObject thread : context.getThreadManager().iterateThreads()) {
            stack.push(thread);
        }

        long t0 = System.currentTimeMillis();
        shareObjects(stack);
        if (Options.SHARED_OBJECTS_DEBUG) {
            System.err.println("Sharing roots took " + (System.currentTimeMillis() - t0) + " ms");
        }
    }

    public static void shareDeclarationFrame(DynamicObject block) {
        final Deque<DynamicObject> stack = new ArrayDeque<>();

        if (Options.SHARED_OBJECTS_DEBUG) {
            System.err.println("Sharing decl frame of " + Layouts.PROC.getSharedMethodInfo(block).getSourceSection().getShortDescription());
        }

        final MaterializedFrame declarationFrame = Layouts.PROC.getDeclarationFrame(block);
        stack.addAll(ObjectGraph.getObjectsInFrame(declarationFrame));

        shareObjects(stack);
    }

    private static void shareObjects(Deque<DynamicObject> stack) {
        while (!stack.isEmpty()) {
            final DynamicObject object = stack.pop();

            if (share(object)) {
                stack.addAll(ObjectGraph.getAdjacentObjects(object));
            }
        }
    }

    @TruffleBoundary
    private static void shareObject(Object value) {
        final Deque<DynamicObject> stack = new ArrayDeque<>();
        stack.add((DynamicObject) value);
        shareObjects(stack);
    }

    public static boolean isShared(DynamicObject object) {
        return Options.SHARED_OBJECTS && isShared(object.getShape());
    }

    public static boolean isShared(Shape shape) {
        return Options.SHARED_OBJECTS && shape.hasProperty(SHARED);
    }

    public static void writeBarrier(Object value) {
        if (value instanceof DynamicObject && !isShared((DynamicObject) value)) {
            shareObject(value);
        }
    }

    public static void propagate(DynamicObject source, Object value) {
        if (isShared(source)) {
            writeBarrier(value);
        }
    }

    private static boolean share(DynamicObject object) {
        if (isShared(object)) {
            return false;
        }

        object.updateShape();
        final Shape oldShape = object.getShape();
        final Shape newShape = oldShape.addProperty(SHARED_PROPERTY);
        object.setShapeAndGrow(oldShape, newShape);
        return true;
    }

}
