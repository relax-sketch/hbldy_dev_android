package com.example.myapplication.quality.rules

import java.io.File
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdditionalRuleFixtureTest {
    @Test
    fun additionalRules_matchBadFixtureAndPassGoodFixture() {
        val expected = expectedResult()
        openFixture().use { connection ->
            assertEquals(
                expected.getJSONArray("badPlotExpectedAdditionalRuleIds").toStringList(),
                matchingRules(connection, expected.getString("badPlotId")),
            )
            assertEquals(
                expected.getJSONArray("goodPlotExpectedAdditionalRuleIds").toStringList(),
                matchingRules(connection, expected.getString("goodPlotId")),
            )
        }
    }

    @Test
    fun relatedLineRule_doesNotReturnRecordBelongingToAnotherPlot() {
        val expected = expectedResult()
        val rule = addedRules().single { it.id == "ADD_GRASS_029" }
        openFixture().use { connection ->
            assertTrue(matches(connection, rule, expected.getString("badPlotId")))
            assertFalse(matches(connection, rule, expected.getString("goodPlotId")))
        }
    }

    private fun matchingRules(connection: Connection, plotId: String): List<String> =
        addedRules().filter { matches(connection, it, plotId) }.map(EmbeddedRule::id)

    private fun matches(connection: Connection, rule: EmbeddedRule, plotId: String): Boolean =
        connection.prepareStatement(rule.sql.replace(":ydId", "?")).use { statement ->
            statement.setString(1, plotId)
            statement.executeQuery().use { results -> results.next() }
        }

    private fun addedRules(): List<EmbeddedRule> =
        EmbeddedRuleSetParser.parse(assetRuleSet().readText()).rules
            .filter { it.sourceId == "grassland-additional-20260526" }

    private fun openFixture(): Connection {
        Class.forName("org.sqlite.JDBC")
        val copiedFixture = Files.createTempFile("grassland-quality-check-", ".zdb")
        javaClass.getResourceAsStream("/fixtures/grassland-quality-check-fixture.zdb").use { source ->
            requireNotNull(source) { "Fixture resource is missing." }
            Files.copy(source, copiedFixture, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        }
        return DriverManager.getConnection("jdbc:sqlite:$copiedFixture").also { connection ->
            connection.createStatement().use { it.execute("PRAGMA query_only=ON") }
        }
    }

    private fun expectedResult(): JSONObject =
        javaClass.getResourceAsStream("/fixtures/grassland-quality-check-expected.json").use { source ->
            requireNotNull(source) { "Expected fixture output is missing." }
            JSONObject(source.bufferedReader(Charsets.UTF_8).readText())
        }

    private fun assetRuleSet(): File =
        listOf(
            File("src/main/assets/rules/rule-set.json"),
            File("app/src/main/assets/rules/rule-set.json"),
        ).first(File::isFile)

    private fun org.json.JSONArray.toStringList(): List<String> =
        (0 until length()).map(::getString)
}
