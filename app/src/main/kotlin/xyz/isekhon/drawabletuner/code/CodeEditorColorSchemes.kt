package xyz.isekhon.drawabletuner.code

import android.util.Log
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.eclipse.tm4e.core.registry.IThemeSource
import xyz.isekhon.drawabletuner.App

object CodeEditorColorSchemes {
    private const val TAG = "CodeEditorColorSchemes"
    
    const val THEME_DRACULA = "dracula.json"
    const val THEME_GITHUB = "GitHub.tmTheme"
    val THEMES = arrayOf(THEME_DRACULA, THEME_GITHUB)
    
    init {
        val assets = App.instance.assets
        
        FileProviderRegistry.getInstance().addFileProvider(AssetsFileResolver(assets))
        val registry = ThemeRegistry.getInstance()
        
        for (theme in THEMES) {
            val path = "textmate/themes/$theme"
            try {
                registry.loadTheme(
                    ThemeModel(
                        IThemeSource.fromInputStream(
                            FileProviderRegistry.getInstance().tryGetInputStream(path),
                            path,
                            null
                        ),
                        theme
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load theme '$theme'", e)
            }
        }
    }
    
    fun loadTextMateColorScheme(theme: String): EditorColorScheme {
        return try {
            ThemeRegistry.getInstance().setTheme(theme)
            TextMateColorScheme.create(ThemeRegistry.getInstance())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load theme '$theme', using defaults", e)
            EditorColorScheme()
        }
    }
}
