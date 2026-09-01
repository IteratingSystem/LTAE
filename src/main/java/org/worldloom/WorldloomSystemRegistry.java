package org.worldloom;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

/** 保存游戏系统，并在每个阶段内解析显式顺序依赖。 */
public final class WorldloomSystemRegistry {
    private final ObjectMap<EnginePhase, Array<Entry>> entries = new ObjectMap<>();
    private final ObjectSet<Class<? extends BaseSystem>> registeredTypes = new ObjectSet<>();

    public SystemRegistration add(EnginePhase phase, BaseSystem system) {
        if (phase == null || system == null) {
            throw new IllegalArgumentException("phase and system cannot be null");
        }
        Class<? extends BaseSystem> type = system.getClass();
        if (!registeredTypes.add(type)) {
            throw new IllegalArgumentException("system type is already registered: " + type.getName());
        }
        Array<Entry> phaseEntries = entries.get(phase);
        if (phaseEntries == null) {
            phaseEntries = new Array<>();
            entries.put(phase, phaseEntries);
        }
        Entry entry = new Entry(system);
        phaseEntries.add(entry);
        return new SystemRegistration(entry);
    }

    public Array<BaseSystem> systemsFor(EnginePhase phase) {
        Array<Entry> source = entries.get(phase);
        Array<BaseSystem> result = new Array<>();
        if (source == null || source.isEmpty()) {
            return result;
        }
        Array<Entry> remaining = new Array<>(source);
        ObjectSet<Class<? extends BaseSystem>> emitted = new ObjectSet<>();
        while (remaining.notEmpty()) {
            Entry next = findReadyEntry(remaining, emitted);
            if (next == null) {
                throw new IllegalStateException("cyclic or unresolved system order in phase " + phase);
            }
            remaining.removeValue(next, true);
            emitted.add(next.system.getClass());
            result.add(next.system);
        }
        return result;
    }

    private Entry findReadyEntry(Array<Entry> remaining,
                                 ObjectSet<Class<? extends BaseSystem>> emitted) {
        for (int candidateIndex = 0; candidateIndex < remaining.size; candidateIndex++) {
            Entry candidate = remaining.get(candidateIndex);
            boolean ready = true;
            for (Class<? extends BaseSystem> dependency : candidate.after) {
                if (containsType(remaining, dependency) && !emitted.contains(dependency)) {
                    ready = false;
                    break;
                }
            }
            if (!ready) {
                continue;
            }
            for (int otherIndex = 0; otherIndex < remaining.size; otherIndex++) {
                Entry other = remaining.get(otherIndex);
                if (other != candidate && other.before.contains(candidate.system.getClass())) {
                    ready = false;
                    break;
                }
            }
            if (ready) {
                return candidate;
            }
        }
        return null;
    }

    private boolean containsType(Array<Entry> source, Class<? extends BaseSystem> type) {
        for (int i = 0; i < source.size; i++) {
            Entry entry = source.get(i);
            if (entry.system.getClass() == type) {
                return true;
            }
        }
        return false;
    }

    static final class Entry {
        final BaseSystem system;
        final ObjectSet<Class<? extends BaseSystem>> before = new ObjectSet<>();
        final ObjectSet<Class<? extends BaseSystem>> after = new ObjectSet<>();

        Entry(BaseSystem system) { this.system = system; }
    }
}
