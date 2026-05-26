package com.example.myapplication.quality.annotations

interface IssueAnnotationStore {
    fun ignoredFingerprints(fingerprints: Set<String>): Set<String>

    fun markIgnored(fingerprint: String, ignoredAtEpochMillis: Long = System.currentTimeMillis())

    fun removeIgnored(fingerprint: String)
}
