package com.example.submission_storyapp

import com.example.submission_storyapp.data.api.responses.ListStoryItem

object DataDummy {

    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                "url $i",
                "createdAt $i",
                "name $i",
                "description $i",
                i + 1.toDouble(),
                "id $i",
                i + 1.toDouble(),
            )
            items.add(quote)
        }
        return items
    }
}