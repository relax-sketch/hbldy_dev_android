package com.example.myapplication.quality.domain

fun cleanProjectCountyLabel(projectName: String): String {
    val withoutPrefix = projectName.removePrefix("综合监测-")
    val withoutTimestamp = withoutPrefix.replace(Regex("-\\d+$"), "")
    return withoutTimestamp.ifBlank { projectName }
}

fun ZdbSourceRef.countyDisplayLabel(): String = cleanProjectCountyLabel(projectName)
