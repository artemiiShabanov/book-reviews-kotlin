package model

import services.*
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

/**
 * Class-model Book.
 */
class Book(title: String?,author: String?) {

    private val title:StringProperty
    private val author:StringProperty

    init{
        this.title = SimpleStringProperty(title)
        this.author = SimpleStringProperty(author)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        else if (other is Book)
            return equalStrings(title.get(), other.title.get()) && equalAuthors(author.get(), other.author.get())
        else
            return false
    }

    override fun hashCode(): Int = smartHash(title.get()) + stupidHash(author.get())

    //Getters.

    fun getTitle(): String = title.get() ?: "no title"

    fun titleProperty(): StringProperty = title


    fun getAuthor(): String = author.get() ?: "no author"

    fun authorProperty(): StringProperty = author

}