package org.worldloom;

import com.artemis.BaseSystem;

/** 同一执行阶段内的系统顺序约束。 */
public final class SystemRegistration {
    private final WorldloomSystemRegistry.Entry entry;

    SystemRegistration(WorldloomSystemRegistry.Entry entry) { this.entry = entry; }

    public SystemRegistration before(Class<? extends BaseSystem> systemType) {
        entry.before.add(systemType);
        return this;
    }

    public SystemRegistration after(Class<? extends BaseSystem> systemType) {
        entry.after.add(systemType);
        return this;
    }
}
