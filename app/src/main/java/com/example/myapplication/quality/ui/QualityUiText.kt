package com.example.myapplication.quality.ui

import com.example.myapplication.quality.rules.RuleSeverity

internal fun titleFor(screen: QualityScreen): String = when (screen) {
    QualityScreen.SOURCE -> "草原监测质检"
    QualityScreen.SCOPE -> "选择质检范围"
    QualityScreen.PROGRESS -> "质检进行中"
    QualityScreen.SUMMARY -> "质检结果"
    QualityScreen.DETAIL -> "样地详情"
}

internal fun severityLabel(severity: RuleSeverity): String = when (severity) {
    RuleSeverity.MANDATORY -> "强制性"
    RuleSeverity.ADVISORY -> "提示性"
}
