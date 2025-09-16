package org.ltae.component.singleton;

import com.artemis.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import net.mostlyoriginal.api.Singleton;

@Singleton
public class Camera extends Component {
    public OrthographicCamera worldCamera;
}
