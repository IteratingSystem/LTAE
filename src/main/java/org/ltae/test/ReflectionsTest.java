package org.ltae.test;

import com.artemis.World;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import java.net.URL;
import java.util.Set;

public class ReflectionsTest {
    public static void main(String[] args) {
        // 1. 获取当前类所在的编译输出目录或 JAR 路径（物理路径）
        URL codeSource = ReflectionsTest.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println("扫描根路径: " + codeSource.getPath());

        // 2. 创建 Reflections，只扫描该物理路径下的所有类
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(codeSource)                     // 物理隔离
                .setScanners(new SubTypesScanner(false)) // false 表示包含所有类
        );

        // 3. 获取该路径下的所有类
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        System.out.println("扫描到的总类数: " + allClasses.size());

        // 4. 动态获取当前模块的根包名（如 "org"）
        String rootPackage = ReflectionsTest.class.getPackage().getName().split("\\.")[0];
        System.out.println("根包名: " + rootPackage);

        // 5. 过滤出属于你项目的类（以根包名开头）
        allClasses.stream()
                .filter(c -> c.getName().startsWith(rootPackage))
                .forEach(System.out::println);
    }
}