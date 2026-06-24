package org.ltae.component.input;


import com.artemis.World;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;
import org.ltae.test.ReflectionsTest;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.Set;

/**
 * 操控组件
 *
 * <p>在状态机或者其它地方处理输入控制实体,可以使用此组件来控制相应的代码是否生效</p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/6/23 11:52
 * @see InputProcess
 */
public class InputProcess extends SerializeComponent {
    // 是否开启操控
    @SerializeParam
    public boolean enabled;
    @SerializeParam
    public String simpleName;

    public InputProcessing processing;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
        // 1. 获取当前类所在的编译输出目录或 JAR 路径（物理路径）
        URL codeSource = InputProcess.class.getProtectionDomain().getCodeSource().getLocation();
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
