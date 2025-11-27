package com.solo.framework.mts.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.solo.framework.core.context.SoloFrameworkUserContextHolder;
import com.solo.framework.core.user.LoginUser;
import com.solo.framework.mts.entity.BaseEntity;
import com.solo.framework.mts.enums.DeletedEnum;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;

public class DefaultMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (! needFill(metaObject)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "deleted", Integer.class, DeletedEnum.NORMAL.getValue());
        this.strictInsertFill(metaObject, "version", Integer.class, 1);

        LoginUser<?> user = SoloFrameworkUserContextHolder.getUser();
        if (Objects.nonNull(user)) {
            this.strictInsertFill(metaObject, "createUserCode", String.class, user.getUserCode());
            this.strictInsertFill(metaObject, "createUserName", String.class, user.getUserName());
            this.strictInsertFill(metaObject, "updateUserCode", String.class, user.getUserCode());
            this.strictInsertFill(metaObject, "updateUserName", String.class, user.getUserName());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (! needFill(metaObject)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);

        LoginUser<?> user = SoloFrameworkUserContextHolder.getUser();
        if (Objects.nonNull(user)) {
            this.strictInsertFill(metaObject, "updateUserCode", String.class, user.getUserCode());
            this.strictInsertFill(metaObject, "updateUserName", String.class, user.getUserName());
        }
    }

    /**
     * 是否需要注入字段
     * @param metaObject 数据对象
     * @return 仅针对BaseEntity的子类进行注入
     */
    private boolean needFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        return originalObject instanceof BaseEntity;
    }

}
