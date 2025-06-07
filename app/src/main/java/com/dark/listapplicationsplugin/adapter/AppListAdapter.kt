package com.dark.listapplicationsplugin.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dark.listapplicationsplugin.dp
import com.dark.listapplicationsplugin.fillMaxWidth
import com.dark.listapplicationsplugin.model.AppInfo
import com.dark.listapplicationsplugin.size
import com.dark.listapplicationsplugin.wrapContentSize

class AppListAdapter(
    private val apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit

) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {


    inner class AppViewHolder(container: ViewGroup) : RecyclerView.ViewHolder(container) {
        val appIcon = ImageView(container.context)
        val appName = TextView(container.context)

        init {
            appIcon.apply {
                size(20.dp)
            }
            appName.apply {
                layoutParams = wrapContentSize()
                textSize = 18f
                setPadding(16, 0, 0, 0)
            }

            val row = LinearLayout(container.context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
                addView(appIcon)
                addView(appName)
            }

            container.addView(row)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val context = parent.context

        val root = CardView(context).apply {
            radius = 16f
            cardElevation = 8f
            useCompatPadding = true
            layoutParams = fillMaxWidth()
            setContentPadding(16, 16, 16, 16)
        }

        return AppViewHolder(root)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.appIcon.setImageDrawable(app.icon)
        holder.appName.text = app.name
        holder.itemView.setOnClickListener {
            onAppClick(app)
        }
    }

}
