import Exceptions.BooksNotFoundException
import model.Book
import org.junit.Test
import services.*

class TestWebLogic {
    init {
        initDriver()
    }

    @Test fun testBooksSearch() {
        //normal situation
        assert(findBooksGoogle("изучаем Java", " ") == hashSetOf(Book("Изучаем Java: [пер. с англ.]", "Кэтти Сьерра"), Book("Изучаем Java EE 7: - Страница 1", "Гонсалвес Энтони")))
        //no books situation
        try { assert(findBooksGoogle("qqqqqqqq", "wwwwwwww").size == 0) }
        catch(e: BooksNotFoundException) { /*NOP*/ }
        finish()
    }

    @Test fun testReviewsSearch() {
        //many reviews on ozon
        val size = loadReviewsOzon(Book("преступление и наказание", "Достоевский")).size
        assert( size == 1 || size == 6)
        //many reviews on labirint
        assert(loadReviewsLabirint(Book("Война и мир","Толстой")).size == 3)
        //no reviews ozon
        val zerosize =loadReviewsOzon(Book("Поиск", "")).size
        assert(zerosize == 0 || zerosize == 9)
        finish()
    }
}