package com.fmisser.gtc.base.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统提示文案
 */

@Component
public class SystemTips {
    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    // 小助手系列消息

    // 新用户注册消息
    public String assistNewUserMsg(String nick) {
        String localeMsg = localeMessageUtil.getLocaleMessage("msg.assist.new_user");
//        return String.format(localeMsg, nick);
        return localeMsg;
    }
}
