package xyz.isekhon.drawabletuner.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.isekhon.drawabletuner.data.model.DrawableSpec
import java.io.File

/**
 * Repository for managing DrawableSpec persistence using JSON file storage
 * Modern alternative to Room database - simpler and perfect for this use case
 */
class DrawableSpecRepository(context: Context) {
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    private val specsFile = File(context.filesDir, "drawable_specs.json")
    
    private val _specs = MutableStateFlow<List<DrawableSpec>>(emptyList())
    val specs: Flow<List<DrawableSpec>> = _specs.asStateFlow()
    
    init {
        loadSpecs()
    }
    
    private fun loadSpecs() {
        try {
            if (specsFile.exists()) {
                val jsonString = specsFile.readText()
                val loadedSpecs = json.decodeFromString<List<DrawableSpec>>(jsonString)
                _specs.value = loadedSpecs
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _specs.value = emptyList()
        }
    }
    
    suspend fun getAllSpecs(): List<DrawableSpec> {
        return _specs.value
    }
    
    suspend fun getSpecById(id: Long): DrawableSpec? {
        return _specs.value.find { it.id == id.toInt() }
    }
    
    suspend fun insertSpec(spec: DrawableSpec): Long {
        val currentSpecs = _specs.value.toMutableList()
        val newId = (currentSpecs.maxOfOrNull { it.id } ?: 0) + 1
        val newSpec = spec.copy(id = newId)
        currentSpecs.add(newSpec)
        saveSpecs(currentSpecs)
        return newId.toLong()
    }
    
    suspend fun updateSpec(spec: DrawableSpec) {
        val currentSpecs = _specs.value.toMutableList()
        val index = currentSpecs.indexOfFirst { it.id == spec.id }
        if (index != -1) {
            currentSpecs[index] = spec
            saveSpecs(currentSpecs)
        }
    }
    
    suspend fun deleteSpec(id: Int) {
        val currentSpecs = _specs.value.toMutableList()
        currentSpecs.removeIf { it.id == id }
        saveSpecs(currentSpecs)
    }
    
    private fun saveSpecs(specs: List<DrawableSpec>) {
        try {
            val jsonString = json.encodeToString(specs)
            specsFile.writeText(jsonString)
            _specs.value = specs
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: DrawableSpecRepository? = null
        
        fun getInstance(context: Context): DrawableSpecRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = DrawableSpecRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
