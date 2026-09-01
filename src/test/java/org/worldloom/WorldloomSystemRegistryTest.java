package org.worldloom;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorldloomSystemRegistryTest {
    @Test
    void resolvesBeforeAndAfterWithinPhase() {
        WorldloomSystemRegistry registry = new WorldloomSystemRegistry();
        registry.add(EnginePhase.UPDATE, new SecondSystem())
            .after(FirstSystem.class);
        registry.add(EnginePhase.UPDATE, new ThirdSystem());
        registry.add(EnginePhase.UPDATE, new FirstSystem())
            .before(ThirdSystem.class);

        Array<BaseSystem> systems = registry.systemsFor(EnginePhase.UPDATE);

        assertEquals(FirstSystem.class, systems.get(0).getClass());
        assertEquals(SecondSystem.class, systems.get(1).getClass());
        assertEquals(ThirdSystem.class, systems.get(2).getClass());
    }

    @Test
    void rejectsCyclicOrder() {
        WorldloomSystemRegistry registry = new WorldloomSystemRegistry();
        registry.add(EnginePhase.UPDATE, new FirstSystem())
            .after(SecondSystem.class);
        registry.add(EnginePhase.UPDATE, new SecondSystem())
            .after(FirstSystem.class);

        assertThrows(IllegalStateException.class,
            () -> registry.systemsFor(EnginePhase.UPDATE));
    }

    private static final class FirstSystem extends BaseSystem {
        @Override protected void processSystem() { }
    }

    private static final class SecondSystem extends BaseSystem {
        @Override protected void processSystem() { }
    }

    private static final class ThirdSystem extends BaseSystem {
        @Override protected void processSystem() { }
    }
}
