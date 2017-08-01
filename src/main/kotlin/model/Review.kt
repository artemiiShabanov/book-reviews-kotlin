package model

import javafx.beans.property.*
import java.time.LocalDate

/**
 * Class-model Review.
 */
class Review(text:String?, author:String?, resource:String?, date:LocalDate?, mark:Int) {

    private val text:StringProperty
    private val author:StringProperty
    private val resource:StringProperty
    private val date:ObjectProperty<LocalDate>
    private val mark:IntegerProperty

    init{
        this.text = SimpleStringProperty(text)
        this.author = SimpleStringProperty(author)
        this.resource = SimpleStringProperty(resource)
        this.date = SimpleObjectProperty<LocalDate>(date)
        this.mark = SimpleIntegerProperty(mark)
    }

    override fun toString():String = author.get() + "\r\nоценка:" + mark.get().toString() + "\r\n" + text.get() + "\r\n" + date.toString() + "\t" + resource.get() + "\r\n"

    //Getters.

    fun getText():String = text.get() ?: "empty review"

    fun textProperty():StringProperty = text


    fun getResource():String = resource.get() ?: "unknown resource"

    fun resourceProperty():StringProperty = resource


    fun getAuthor():String = author.get() ?: "anonymous"

    fun authorProperty():StringProperty = author


    fun getDate():LocalDate? = date?.get()

    fun dateProperty():ObjectProperty<LocalDate> = date


    fun getMark():Int = mark.get()

    fun markProperty():IntegerProperty = mark
}