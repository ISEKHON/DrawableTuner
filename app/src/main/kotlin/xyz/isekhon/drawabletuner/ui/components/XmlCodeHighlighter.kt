package xyz.isekhon.drawabletuner.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun XmlCodeHighlighter(
    xmlCode: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    
    val highlightedCode = highlightXml(xmlCode, isDark)
    
    Surface(
        modifier = modifier,
        color = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8F8F8)
    ) {
        SelectionContainer {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = highlightedCode,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                )
            }
        }
    }
}

private fun highlightXml(code: String, isDark: Boolean): AnnotatedString {
    // Define colors for dark and light themes
    val tagColor = if (isDark) Color(0xFF569CD6) else Color(0xFF0000FF)
    val attributeNameColor = if (isDark) Color(0xFF9CDCFE) else Color(0xFFFF0000)
    val attributeValueColor = if (isDark) Color(0xFFCE9178) else Color(0xFF0000FF)
    val commentColor = if (isDark) Color(0xFF6A9955) else Color(0xFF008000)
    val textColor = if (isDark) Color(0xFFD4D4D4) else Color(0xFF000000)
    val bracketColor = if (isDark) Color(0xFF808080) else Color(0xFF808080)
    
    return buildAnnotatedString {
        var i = 0
        
        while (i < code.length) {
            when {
                // XML Comments
                code.startsWith("<!--", i) -> {
                    val commentEnd = code.indexOf("-->", i)
                    if (commentEnd != -1) {
                        withStyle(SpanStyle(color = commentColor, fontWeight = FontWeight.Normal)) {
                            append(code.substring(i, commentEnd + 3))
                        }
                        i = commentEnd + 3
                    } else {
                        withStyle(SpanStyle(color = commentColor)) {
                            append(code.substring(i))
                        }
                        break
                    }
                }
                
                // XML Tags
                code[i] == '<' -> {
                    val tagEnd = code.indexOf('>', i)
                    if (tagEnd != -1) {
                        val tagContent = code.substring(i, tagEnd + 1)
                        highlightTag(tagContent, tagColor, attributeNameColor, attributeValueColor, bracketColor)
                        i = tagEnd + 1
                    } else {
                        withStyle(SpanStyle(color = textColor)) {
                            append(code[i])
                        }
                        i++
                    }
                }
                
                // Regular text
                else -> {
                    val nextTag = code.indexOf('<', i)
                    val textEnd = if (nextTag != -1) nextTag else code.length
                    val text = code.substring(i, textEnd)
                    
                    if (text.isNotBlank()) {
                        withStyle(SpanStyle(color = textColor)) {
                            append(text)
                        }
                    } else {
                        append(text)
                    }
                    i = textEnd
                }
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightTag(
    tag: String,
    tagColor: Color,
    attributeNameColor: Color,
    attributeValueColor: Color,
    bracketColor: Color
) {
    var i = 0
    
    // Opening bracket
    withStyle(SpanStyle(color = bracketColor)) {
        append('<')
    }
    i++
    
    // Handle closing tags
    if (i < tag.length && tag[i] == '/') {
        withStyle(SpanStyle(color = bracketColor)) {
            append('/')
        }
        i++
    }
    
    // Tag name
    val tagNameEnd = tag.indexOfAny(charArrayOf(' ', '/', '>'), i)
    if (tagNameEnd != -1) {
        withStyle(SpanStyle(color = tagColor, fontWeight = FontWeight.Bold)) {
            append(tag.substring(i, tagNameEnd))
        }
        i = tagNameEnd
    }
    
    // Attributes
    while (i < tag.length && tag[i] != '>') {
        when {
            tag[i].isWhitespace() -> {
                append(tag[i])
                i++
            }
            
            tag[i] == '/' -> {
                withStyle(SpanStyle(color = bracketColor)) {
                    append('/')
                }
                i++
            }
            
            else -> {
                // Attribute name
                val attrEnd = tag.indexOf('=', i)
                if (attrEnd != -1 && attrEnd < tag.length - 1) {
                    withStyle(SpanStyle(color = attributeNameColor)) {
                        append(tag.substring(i, attrEnd))
                    }
                    append('=')
                    i = attrEnd + 1
                    
                    // Attribute value
                    if (i < tag.length && tag[i] == '"') {
                        val valueEnd = tag.indexOf('"', i + 1)
                        if (valueEnd != -1) {
                            withStyle(SpanStyle(color = attributeValueColor)) {
                                append(tag.substring(i, valueEnd + 1))
                            }
                            i = valueEnd + 1
                        } else {
                            withStyle(SpanStyle(color = attributeValueColor)) {
                                append(tag.substring(i))
                            }
                            i = tag.length
                        }
                    }
                } else {
                    append(tag[i])
                    i++
                }
            }
        }
    }
    
    // Closing bracket
    if (i < tag.length) {
        withStyle(SpanStyle(color = bracketColor)) {
            append('>')
        }
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
