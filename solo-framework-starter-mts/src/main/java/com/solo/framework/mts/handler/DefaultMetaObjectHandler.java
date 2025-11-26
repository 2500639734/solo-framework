package com.solo.framework.mts.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.solo.framework.mts.entity.BaseEntity;
import com.solo.framework.mts.enums.DeletedEnum;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class DefaultMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (needInject(metaObject)) {
            // TODO：当前用户 / 企业id，暂不支持
            LocalDateTime now = LocalDateTime.now();
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
            this.strictInsertFill(metaObject, "deleted", Integer.class, DeletedEnum.NORMAL.getValue());
            this.strictInsertFill(metaObject, "version", Integer.class, 1);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (needInject(metaObject)) {
            // TODO：当前用户 / 企业id，暂不支持
            LocalDateTime now = LocalDateTime.now();
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
        }
    }

    /**
     * 是否需要注入字段
     * @param metaObject 数据对象
     * @return 仅针对BaseEntity的子类进行注入
     */
    private boolean needInject(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        return originalObject instanceof BaseEntity;
    }

}
