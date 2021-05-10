package com.maxdreher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

open class ArrayAdapterBase<T>(
    private val inputContext: Context,
    @LayoutRes private val resource: Int
) : ArrayAdapter<T>(inputContext, resource) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView ?: LayoutInflater.from(context).inflate(resource, null)
    }
}