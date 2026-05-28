package com.example.myapplication.quality.rules

class UnsafeRuleSqlException(message: String) : IllegalArgumentException(message)

object ReadOnlySqlValidator {
    private val forbiddenKeyword = Regex(
        """\b(INSERT|UPDATE|DELETE|CREATE|DROP|ALTER|ATTACH|DETACH|PRAGMA|VACUUM|REINDEX)\b|\bREPLACE\s+INTO\b""",
        RegexOption.IGNORE_CASE,
    )
    private val leadingQuery = Regex("""^\s*(SELECT|WITH)\b""", RegexOption.IGNORE_CASE)

    fun validate(rule: EmbeddedRule) {
        val normalized = removeQuotedContentAndComments(rule.sql)
        if (!leadingQuery.containsMatchIn(normalized)) {
            throw UnsafeRuleSqlException("Rule '${rule.id}' must contain a SELECT or WITH query.")
        }
        val forbidden = forbiddenKeyword.find(normalized)?.value
        if (forbidden != null) {
            throw UnsafeRuleSqlException("Rule '${rule.id}' contains forbidden SQL keyword '$forbidden'.")
        }
        if (containsMultipleStatements(normalized)) {
            throw UnsafeRuleSqlException("Rule '${rule.id}' must contain a single SQL statement.")
        }
        if (":ydId" !in rule.sql) {
            throw UnsafeRuleSqlException("Rule '${rule.id}' is missing required :ydId scope parameter.")
        }
    }

    private fun containsMultipleStatements(sql: String): Boolean {
        val withoutTrailingTerminator = sql.trim().removeSuffix(";").trim()
        return ';' in withoutTrailingTerminator
    }

    private fun removeQuotedContentAndComments(sql: String): String =
        sql.replace(Regex("""--[^\r\n]*"""), " ")
            .replace(Regex("""/\*.*?\*/""", setOf(RegexOption.DOT_MATCHES_ALL)), " ")
            .replace(Regex("""'(?:''|[^'])*'"""), "''")
            .replace(Regex(""""(?:""|[^"])*""""), "\"\"")
}
