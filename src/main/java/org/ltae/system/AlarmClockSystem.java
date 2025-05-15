package org.ltae.system;

import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.AlarmClock;

/**
 * @Auther WenLong
 * @Date 2025/5/15 10:36
 * @Description 闹钟系统
 **/
@One(AlarmClock.class)
public class AlarmClockSystem extends IteratingSystem {
    private M<AlarmClock> mAlarmClock;
    @Override
    protected void process(int entityId) {
        AlarmClock alarmClock = mAlarmClock.get(entityId);
        float atTime = alarmClock.atTime;
        if (atTime > 0){
            atTime -= world.getDelta()*1000;
            alarmClock.isFinished = false;
        }
        if (atTime <= 0){
            alarmClock.isFinished = true;
        }
    }
}
