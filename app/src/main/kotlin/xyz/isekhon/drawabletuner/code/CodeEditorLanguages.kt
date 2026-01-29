package xyz.isekhon.drawabletuner.code

import android.util.Log
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.dsl.languages
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import xyz.isekhon.drawabletuner.App

object CodeEditorLanguages {
    private const val TAG = "CodeEditorLanguages"
    
    val LANGUAGES = arrayOf("kotlin.tmLanguage", "xml.tmLanguage.json")
    const val SCOPE_NAME_KOTLIN = "source.kotlin"
    const val SCOPE_NAME_XML = "text.xml"
    
    init {
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(App.instance.assets)
        )
        
        for (language in LANGUAGES) {
            val languageName = language.substring(0, language.indexOf('.'))
            try {
                GrammarRegistry.getInstance().loadGrammars(
                    languages {
                        language(languageName) {
                            grammar = "textmate/$language"
                            defaultScopeName(if (language == LANGUAGES[1]) "text" else "source")
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load language '$language'", e)
            } catch (e: NoSuchMethodError) {
                Log.e(TAG, "Probably running on a low API device", e)
            }
        }
    }
    
    fun loadTextMateLanguage(scopeName: String): Language {
        return try {
            TextMateLanguage.create(scopeName, true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create language from scope name '$scopeName', using empty one as default language", e)
            EmptyLanguage()
        } catch (e: NoSuchMethodError) {
            Log.e(TAG, "Failed to create language from scope name '$scopeName', using empty one as default language", e)
            EmptyLanguage()
        }
    }
}
