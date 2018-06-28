package com.gclfax.common.utils;

import com.gclfax.modules.sys.entity.SysUserEntity;
import org.apache.shiro.SecurityUtils;

/**
 * Created by chenmy on 2018/6/21.
 */
public class UserContextUtils {
    public static SysUserEntity getUser() {
        return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    public static Long getUserId() {
        return getUser().getUserId();
    }
}
