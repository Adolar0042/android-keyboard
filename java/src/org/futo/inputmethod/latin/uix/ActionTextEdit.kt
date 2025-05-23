package org.futo.inputmethod.latin.uix

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView


class ActionEditText(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {
    var inputConnection: InputConnection? = null
        private set

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        inputConnection = super.onCreateInputConnection(outAttrs)
        return inputConnection
    }

    private var textChanged: (String) -> Unit = { }
    fun setTextChangeCallback(
        textChanged: (String) -> Unit
    ) {
        this.textChanged = textChanged
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        // For some strange reason this IS null sometimes, even though it
        // shouldn't be
        if(textChanged != null) {
            textChanged(text?.toString() ?: "")
        }
    }
}


@Composable
fun ActionTextEditor(
    text: MutableState<String>,
    multiline: Boolean = false,
    textSize: TextUnit = 16.sp,
    typeface: Typeface? = null,
    autocorrect: Boolean = false
) {
    val context = LocalContext.current
    val manager = if(LocalInspectionMode.current) {
        null
    } else {
        LocalManager.current
    }

    val height = with(LocalDensity.current) {
        48.dp.toPx()
    }

    val inputType = if(multiline) {
        EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
    } else {
        EditorInfo.TYPE_CLASS_TEXT
    } or if(autocorrect) {
        EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT
    } else {
        0
    }

    val color = LocalContentColor.current
    val textSizeToUse = with(LocalDensity.current) { textSize.toPx() }
    val typefaceToUse = typeface
    if(!LocalInspectionMode.current) {
        val editText = remember {
            ActionEditText(context).apply {
                this.inputType = inputType

                setTextChangeCallback { text.value = it }

                setText(text.value)
                setTextColor(color.toArgb())

                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeToUse)
                typefaceToUse?.let { setTypeface(it) }

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                privateImeOptions = if(!autocorrect) {
                    "org.futo.inputmethod.latin.NoSuggestions=1"
                } else {
                    ""
                }

                setHeight(height.toInt())

                val editorInfo = EditorInfo().apply {
                    this.inputType = inputType
                    this.packageName = context.packageName
                }
                onCreateInputConnection(editorInfo)

                manager?.overrideInputConnection(inputConnection!!, editorInfo)

                // Remove underline and padding
                background = null
                setPadding(0, 0, 0, 0)

                requestFocus()
            }
        }

        LaunchedEffect(text.value) {
            if(text.value != editText.getText().toString()) {
                editText.setText(text.value)
            }
        }


        AndroidView(
            factory = { editText },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            onRelease = {
                manager?.unsetInputConnection()
            }
        )

        val fgColor = LocalContentColor.current
        val primaryColor = MaterialTheme.colorScheme.primary

        LaunchedEffect(fgColor, primaryColor) {
            editText.setTextColor(fgColor.toArgb())
            editText.setHintTextColor(fgColor.copy(alpha = 0.7f).toArgb())
            editText.highlightColor = primaryColor.copy(alpha = 0.7f).toArgb()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                editText.textCursorDrawable?.setTint(primaryColor.toArgb())
                editText.textSelectHandle?.setTint(primaryColor.toArgb())
                editText.textSelectHandleLeft?.setTint(primaryColor.toArgb())
                editText.textSelectHandleRight?.setTint(primaryColor.toArgb())
            }
        }
    }
}
