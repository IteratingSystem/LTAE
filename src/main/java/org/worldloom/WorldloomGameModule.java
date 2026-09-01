package org.worldloom;

/** 游戏项目通过模块集中注册自己的 Artemis 系统。 */
@FunctionalInterface
public interface WorldloomGameModule {
    void registerSystems(WorldloomSystemRegistry systems);
}
