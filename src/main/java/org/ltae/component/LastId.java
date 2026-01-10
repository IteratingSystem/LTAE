package org.ltae.component;

import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;

/**
 * @author: Last
 * @date: 2026/1/6
 * @description:在实体存档过后,用于记录此id,由于所有实体重建过程中,id会重新生成,故而需要此属性
 */

public class LastId extends SerializeComponent {
    //在实体存档过后,用于记录此id,由于所有实体重建过程中,id会重新生成,故而需要此属性
    @SerializeParam
    public int id;

    @Override
    public void beforeSerialization() {
        super.beforeSerialization();
        id = entityId;
    }
}
