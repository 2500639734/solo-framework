package com.solo.framework.core.constant;

public class SoloFrameworkPropertiesPrefixConstant {

    private SoloFrameworkPropertiesPrefixConstant() {

    }

    public static final String SOLO_FRAMEWORK_PREFIX = "solo.framework";

    /******************************************* web prefix start *******************************************/
    public static final String SOLO_FRAMEWORK_WEB_PREFIX = SOLO_FRAMEWORK_PREFIX + ".web";
    public static final String SOLO_FRAMEWORK_WEB_SWAGGER_PREFIX = SOLO_FRAMEWORK_WEB_PREFIX + ".swagger";
    public static final String SOLO_FRAMEWORK_WEB_SWAGGER_CONCAT_PREFIX = SOLO_FRAMEWORK_WEB_PREFIX + ".concat";

    /******************************************* fastjson prefix start *******************************************/
    public static final String SOLO_FRAMEWORK_FAST_JSON_PREFIX = SOLO_FRAMEWORK_WEB_PREFIX + ".fastjson";

}
