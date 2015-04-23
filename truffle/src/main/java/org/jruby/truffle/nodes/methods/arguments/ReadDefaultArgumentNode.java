/*
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.methods.arguments;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.BranchProfile;
import com.oracle.truffle.api.utilities.ValueProfile;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.runtime.RubyArguments;
import org.jruby.truffle.runtime.RubyContext;

/**
 * Read a pre-argument or returns a precomputed default value.
 */
public class ReadDefaultArgumentNode extends RubyNode {

    private final int index;

    private final BranchProfile outOfRangeProfile = BranchProfile.create();
    private final Object defaultValue;

    private final ValueProfile argumentValueProfile = ValueProfile.createPrimitiveProfile();

    public ReadDefaultArgumentNode(RubyContext context, SourceSection sourceSection, int index, Object defaultValue) {
        super(context, sourceSection);
        this.index = index;
        this.defaultValue = defaultValue;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        if (index >= RubyArguments.getUserArgumentsCount(frame.getArguments())) {
            outOfRangeProfile.enter();
            return defaultValue;
        }

        return argumentValueProfile.profile(RubyArguments.getUserArgument(frame.getArguments(), index));
    }
}
