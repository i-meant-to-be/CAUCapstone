package com.caucapstone.app.util

import com.caucapstone.app.R

enum class SettingType {
    BOOL, UINT, INT
}

sealed class SettingItem(
    val title: Int,
    val explanation: Int,
    val type: SettingType
) {
    object CVDocMode : SettingItem(
        R.string.setting_option_name_doc_mode,
        R.string.setting_option_expl_doc_mode,
        SettingType.BOOL
    )
    object CVRemoveGlare : SettingItem(
        R.string.setting_option_name_remove_glare,
        R.string.setting_option_expl_remove_glare,
        SettingType.BOOL
    )
    object CVColorSensitivity : SettingItem(
        R.string.setting_option_name_color_sensitivity,
        R.string.setting_option_expl_color_sensitivity,
        SettingType.INT
    )
    object RCFilterType : SettingItem(
        R.string.setting_option_name_filter_type,
        R.string.setting_option_expl_filter_type,
        SettingType.INT
    )
}