package com.freeman.dragfloatcontainer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Toast

/**
 * Created by Freeman on 2020/10/13
 */
class TestChildView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {
    init {
        setOnClickListener {
            Toast.makeText(getContext(), "Child View is clicked", Toast.LENGTH_SHORT).show()
        }
    }
}