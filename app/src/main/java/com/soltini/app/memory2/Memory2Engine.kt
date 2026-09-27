package com.soltini.app.memory2

import android.content.Context
import android.util.Log
import com.soltini.app.memory.MyraUnifiedMemory
import com.soltini.app.memory.UnifiedMemory
import com.soltini.app.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * RetrievedMemoryContext
 *
 * Scored, filtered, and ranked contextual memory returned to MYRA Orchestrator.
 */
data class RetrievedMemoryContext(
    val relevantItems: List<MemoryItem>,
    val workingSummary: String,
    val sessionSummary: String,
    val experienceRecommendation: Memory2Database.ExperienceRecord? = null,
    val formattedPromptBlock: String
)

/**
 * Memory2Engine
 *
 * Fully integrated contextual, persistent, and intelligent memory engine backed by MyraUnifiedMemory.
 * Provides unified storage, Hindi/Hinglish extraction, past topic recaps, and working/session memory.
 */
class Memory2Engine private constructor(
    private val context: Context,
    private val appSettings: AppSettings
) {
    companion object {
        private const val TAG = "Memory2Engine"

        @Volatile
        private var instance: Memory2Engine? = null

        fun getInstance(context: Context, appSettings: AppSettings): Memory2Engine =
            instance ?: synchronized(this) {
                instance ?: Memory2Engine(context.applicationContext, appSettings).also { instance = it }
            }
    }

    val unifiedMemory: MyraUnifiedMemory = MyraUnifiedMemory.getInstance(context)
    val working = WorkingMemory()
    val session = SessionMemory()
    val db: Memory2Database = Memory2Database.getInstance(context)
    val mem0: com.soltini.app.mem0.Mem0MemoryEngine = com.soltini.app.mem0.Mem0MemoryEngine.getInstance(context, appSettings)

    suspend fun retrieveContext(userRequest: String, maxItems: Int = 8): RetrievedMemoryContext =
        withContext(Dispatchers.IO) {
            val relevant = unifiedMemory.searchMemories(userRequest, limit = maxItems)
            val items = relevant.map { it.toMemoryItem() }
            val formatted = unifiedMemory.buildSystemPromptContext()

            RetrievedMemoryContext(
                relevantItems = items,
                workingSummary = working.getAllIntermediateResults().entries.joinToString("; ") { "${it.key}=${it.value}" },
                sessionSummary = session.getTopicsDiscussed().joinToString(", "),
                experienceRecommendation = null,
                formattedPromptBlock = formatted
            )
        }

    suspend fun evaluateAndStore(
        goal: String,
        outcomeSummary: String,
        toolSequence: List<String>,
        isSuccess: Boolean,
        userExplicitStatement: String? = null
    ) = withContext(Dispatchers.IO) {
        session.recordCompletedTask(goal, outcomeSummary, isSuccess, toolSequence)
        session.recordTopic(goal.take(30))

        // Multi-lingual fact extraction for user statements (Hindi, Hinglish, English)
        if (!userExplicitStatement.isNullOrBlank()) {
            unifiedMemory.extractFactsFromText(userExplicitStatement, isUser = true)
        } else if (goal.isNotBlank()) {
            unifiedMemory.extractFactsFromText(goal, isUser = true)
        }

        // If outcome summary contains facts or details, extract them
        if (outcomeSummary.isNotBlank()) {
            unifiedMemory.extractFactsFromText(outcomeSummary, isUser = false)
        }
    }

    fun saveLongTerm(
        key: String,
        fact: String,
        importance: Float = 0.7f,
        type: MemoryType = MemoryType.FACT,
        confidence: Float = 0.85f,
        userConfirmed: Boolean = true,
        sensitivityLevel: String = "NORMAL",
        tags: List<String> = emptyList()
    ): Boolean {
        unifiedMemory.saveMemory(
            content = fact,
            category = type.name,
            importance = importance,
            tags = tags,
            keyName = key
        )
        return true
    }

    fun saveExplicitMemory(
        text: String,
        type: MemoryType = MemoryType.FACT,
        confirmed: Boolean = true
    ): MemoryItem {
        val mem = unifiedMemory.saveMemory(
            content = text,
            category = type.name,
            importance = 0.95f,
            keyName = type.name.lowercase(Locale.ROOT)
        )
        return mem.toMemoryItem()
    }

    fun saveUserCorrection(correctionText: String, taskContext: String? = null): MemoryItem {
        val mem = unifiedMemory.saveMemory(
            content = correctionText,
            category = "CORRECTION",
            importance = 0.99f,
            keyName = "user_correction"
        )
        return mem.toMemoryItem()
    }

    fun getAllMemoriesUnified(): List<MemoryItem> {
        return unifiedMemory.getAllMemories().map { it.toMemoryItem() }
    }

    fun searchMemoriesUnified(query: String): List<MemoryItem> {
        return unifiedMemory.searchMemories(query, limit = 25).map { it.toMemoryItem() }
    }

    fun deleteMemory(id: String): Boolean {
        return unifiedMemory.deleteMemory(id)
    }

    fun deleteMemoriesMatching(query: String): Int {
        return unifiedMemory.deleteMemoriesMatching(query)
    }

    fun updateMemory(id: String, newContent: String, newType: MemoryType? = null, newConfirmed: Boolean = true): Boolean {
        return unifiedMemory.updateMemory(id, newContent, newType?.name)
    }

    fun clearAllMemories(): Boolean {
        return unifiedMemory.clearAllMemories()
    }

    fun getMemoriesSummaryForUser(): String {
        val memories = getAllMemoriesUnified()
        if (memories.isEmpty()) {
            return "Boss, abhi meri memory me koi saved facts ya preferences nahi hain."
        }
        val sb = StringBuilder("Boss, mujhe aapke baare me ye baatein yaad hain:\n")
        memories.take(10).forEachIndexed { index, item ->
            sb.append("${index + 1}. ${item.content}\n")
        }
        return sb.toString().trim()
    }
}

fun UnifiedMemory.toMemoryItem(): MemoryItem {
    return MemoryItem(
        id = id,
        type = MemoryType.fromString(category),
        category = MemoryCategory.LONG_TERM,
        key = keyName.ifBlank { category.lowercase(Locale.ROOT) },
        content = content,
        confidence = 0.95f,
        importance = importance,
        relevance = 1.0f,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastAccessedAt = lastRecalledAt,
        accessCount = recallCount,
        userConfirmed = userConfirmed,
        metadata = mapOf("tags" to tags.joinToString(","))
    )
}
