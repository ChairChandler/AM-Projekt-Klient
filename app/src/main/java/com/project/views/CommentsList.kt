package com.project.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.project.R
import com.project.models.Comment
import kotlinx.android.synthetic.main.list_item.view.*

class CommentsArrayAdapter(context: Context, contacts: List<Comment>) :
    ArrayAdapter<Comment>(context, 0, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val comment = getItem(position) as Comment

        rootView.date.text = this.parseDate(comment.dateUTC)
        rootView.user.text = comment.user
        rootView.score.rating = comment.score.toFloat()
        rootView.description.text = comment.comment

        return rootView
    }

    private fun parseDate(date: String): String {
        return """${date.slice(IntRange(0, 9))} ${date.slice(IntRange(11, 18))}"""
    }
}