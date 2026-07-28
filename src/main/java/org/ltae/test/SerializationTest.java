package org.ltae.test;


import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;

import java.io.*;

/**
 *
 *
 * <p></p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/7/28 10:17
 * @see SerializationTest
 */
public class SerializationTest {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        final WorldSerializationManager manager = new WorldSerializationManager();
        World world = new World(new WorldConfiguration().setSystem(manager));
        manager.setSerializer(new JsonArtemisSerializer(world));


        SaveFileFormat saveFileFormat = new SaveFileFormat();

//        manager.process();

        final InputStream is = SerializationTest.class.getResourceAsStream("level.json");
        manager.load(is, SaveFileFormat.class);


        final PrintWriter writer = new PrintWriter("level.json", "UTF-8");
//        manager.save(writer, new SaveFileFormat(entities));
        writer.close();

        final StringWriter writerStr = new StringWriter();
//        manager.save(writerStr, new SaveFileFormat(entities));
        String json = writerStr.toString();
    }

    private String subscriptionToJson(EntitySubscription subscription) {
        final StringWriter writer = new StringWriter();
        final SaveFileFormat save = new SaveFileFormat(subscription.getEntities());
//        world.getSystem(WorldSerializationManager.class).save(writer, save);
        return writer.toString();
    }
}
