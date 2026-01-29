package xyz.isekhon.drawabletuner.ui.viewmodel

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.isekhon.drawabletuner.data.repository.DrawableSpecRepository
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesFactory
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom
import xyz.isekhon.drawabletuner.data.model.DrawableSpec
import xyz.isekhon.drawabletuner.utils.DrawableBuilder
import xyz.isekhon.drawabletuner.utils.PropertiesExchange
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

class DrawableViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = DrawableSpecRepository.getInstance(application)
    
    private val _properties = MutableStateFlow(DrawablePropertiesFactory.createDefault())
    val properties: StateFlow<DrawablePropertiesInRoom> = _properties.asStateFlow()
    
    private val _drawable = MutableStateFlow<Drawable?>(null)
    val drawable: StateFlow<Drawable?> = _drawable.asStateFlow()
    
    private val _currentSpec = MutableStateFlow(DrawableSpec.createTemp())
    val currentSpec: StateFlow<DrawableSpec> = _currentSpec.asStateFlow()
    
    private val _isEdited = MutableStateFlow(false)
    val isEdited: StateFlow<Boolean> = _isEdited.asStateFlow()
    
    private val _savedSpecs = MutableStateFlow<List<DrawableSpec>>(emptyList())
    val savedSpecs: StateFlow<List<DrawableSpec>> = _savedSpecs.asStateFlow()
    
    init {
        updateDrawable()
        loadSavedSpecs()
    }
    
    private fun updateDrawable() {
        val props = _properties.value
        _drawable.value = DrawableBuilder()
            .batch(PropertiesExchange.fromRoom(props))
            .build()
    }
    
    fun updateProperty(propertyName: String, value: Any) {
        val newProps = _properties.value.copy()
        
        try {
            // Handle special case for cornerRadius which uses setter method
            if (propertyName == "cornerRadius" && value is Int) {
                newProps.setCornerRadius(value)
            } else {
                // Handle regular properties
                val property = DrawablePropertiesInRoom::class.memberProperties
                    .filterIsInstance<KMutableProperty1<DrawablePropertiesInRoom, Any>>()
                    .find { it.name == propertyName }
                
                property?.let {
                    when (value) {
                        is Int -> (it as? KMutableProperty1<DrawablePropertiesInRoom, Int>)?.set(newProps, value)
                        is Float -> (it as? KMutableProperty1<DrawablePropertiesInRoom, Float>)?.set(newProps, value)
                        is Boolean -> (it as? KMutableProperty1<DrawablePropertiesInRoom, Boolean>)?.set(newProps, value)
                    }
                }
            }
            
            _properties.value = newProps
            updateDrawable()
            checkIfEdited()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun applySpec(spec: DrawableSpec) {
        _currentSpec.value = spec
        _properties.value = spec.properties.copy()
        updateDrawable()
        _isEdited.value = false
    }
    
    fun createNewSpec() {
        val newSpec = DrawableSpec.createTemp()
        _currentSpec.value = newSpec
        _properties.value = newSpec.properties.copy()
        updateDrawable()
        _isEdited.value = true
    }
    
    fun saveCurrentSpec(name: String? = null) {
        viewModelScope.launch {
            val spec = _currentSpec.value
            spec.properties = _properties.value
            
            if (spec.id == 0) {
                // New spec
                name?.let { spec.name = it }
                val id = repository.insertSpec(spec)
                val savedSpec = repository.getSpecById(id)
                savedSpec?.let {
                    _currentSpec.value = it
                    _isEdited.value = false
                }
            } else {
                // Update existing spec
                repository.updateSpec(spec)
                _isEdited.value = false
            }
            loadSavedSpecs()
        }
    }
    
    private fun loadSavedSpecs() {
        viewModelScope.launch {
            repository.specs.collect { specs ->
                _savedSpecs.value = specs
            }
        }
    }
    
    private fun checkIfEdited() {
        _isEdited.value = _currentSpec.value.id == 0 || 
            _properties.value != _currentSpec.value.properties
    }
}
