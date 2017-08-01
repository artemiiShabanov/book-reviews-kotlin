package services

import Exceptions.BooksNotFoundException
import Exceptions.DriverWasClosedException
import com.google.common.io.Resources
import model.Book
import model.Review
import org.openqa.selenium.*
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.IOException
import java.util.*


private var driver: WebDriver? = null

fun initDriver() {
    val url = Resources.getResource("ChromeWDHome")
    try {
        System.setProperty("webdriver.chrome.driver", Resources.toString(url, com.google.common.base.Charsets.UTF_8))
        driver = ChromeDriver()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


//Google

/**
 * Searching for books on google books.
 * Using findBooksOzon() on fail.
 * @param title title.
 * *
 * @param author author.
 * *
 * @return set of books.
 * *
 * @throws BooksNotFoundException
 */
@Throws(BooksNotFoundException::class, DriverWasClosedException::class)
fun findBooksGoogle(title: String, author: String): HashSet<Book> {
    try {
        val result = HashSet<Book>()

        //Data entry for search
        driver?.get("https://books.google.ru/advanced_book_search?hl=ru")
        driver?.findElement(By.name("as_vt"))?.sendKeys(title)
        driver?.findElement(By.name("as_auth"))?.sendKeys(author)
        Select(driver?.findElement(By.name("num"))).selectByIndex(1)
        driver?.findElement(By.name("btnG"))?.click()

        try {
            //if we get error
            driver?.findElement(By.id("main-frame-error"))
            return findBooksOzon(title, author)
        } catch (e: NoSuchElementException) {
            try {
                //if we get capcha
                driver?.findElement(By.id("recaptcha"))
                return findBooksOzon(title, author)
            } catch (e1: NoSuchElementException) {
                try {
                    //is there are no such books
                    driver?.findElement(By.className("mnr-c"))
                    return findBooksOzon(title, author)
                } catch (e2: NoSuchElementException) {
                    val list = driver!!.findElements(By.className("rc"))
                    for (we in list) {
                        val b = createBookFromWEGoogle(we)
                        if (b != null) {
                            result.add(b)
                        }
                    }
                }

            }
        }
        return result
    } catch (e: WebDriverException) {
        finish()
        driver = ChromeDriver()
        throw DriverWasClosedException()
    }
}

/**
 * Supporting function to build book from google books.
 * @param we div containing information about a book.
 * @return book.
 */
private fun createBookFromWEGoogle(we: WebElement): Book? {
    val tmpTitle: String
    var tmpAuthor: String
    try {
        tmpTitle = we.findElement(By.cssSelector(".r a")).text
    } catch (e: Exception) {
        return null
    }

    try {
        tmpAuthor = we.findElement(By.className("fl")).text
    } catch (e: Exception) {
        tmpAuthor = ""
    }

    if (tmpAuthor === "Перевести эту страницу") {
        tmpAuthor = ""
    }
    return Book(tmpTitle, tmpAuthor)
}


//Ozon.ru

/**
 * Loading reviews from ozon.ru.
 * @param book what we are searching reviews for.
 * @return list of reviews(size = 0 if there are no reviews).
 */
@Throws(DriverWasClosedException::class)
fun loadReviewsOzon(book: Book): ArrayList<Review> {
    try {
        val result = ArrayList<Review>()

        //Date entry for search
        driver?.get("http://www.ozon.ru/context/div_book/")
        driver?.findElement(By.name("SearchText"))?.sendKeys(book.getTitle() + " " + book.getAuthor())
        driver?.findElement(By.className("eMainSearchBlock_ButtonWrap"))?.click()

        val wait = WebDriverWait(driver, 10)

        try {
            driver?.findElement(By.className("eZeroSearch_Top"))
            return result
        } catch (ex: NoSuchElementException) {
            try {
                driver?.findElement(By.cssSelector(".eOneTile_tileLink.jsUpdateLink"))
                driver?.get(driver!!.findElements(By.cssSelector(".eOneTile_tileLink.jsUpdateLink"))[1].getAttribute("href"))
            } catch (r: NoSuchElementException) {
                //there are only 1 book for this request
                //NOP
            }
            try {
                (driver as JavascriptExecutor).executeScript("window.scrollTo(0,document.body.scrollHeight);")
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.eCommentsHeader_Title_Link")))
                driver?.get(driver?.findElement(By.cssSelector("a.eCommentsHeader_Title_Link"))?.getAttribute("href"))

                val list = driver!!.findElements(By.cssSelector(".bComment.jsComment"))
                for (we in list) {
                    result.add(Review(we.findElement(By.className("eComment_Text_Text")).text, we.findElement(By.className("eComment_Info_Username_Link")).text, "ozon.ru", parseOzon(we.findElement(By.className("eComment_Info_Date")).text), getMarkFromWEOzon(we)))
                }
            } catch (e: NoSuchElementException) {
                return result
            } catch (e: TimeoutException) {
                return result
            }

        }

        return result

    } catch (e: WebDriverException) {
        finish()
        driver = ChromeDriver()
        throw DriverWasClosedException()
    }

}

/**
 * Supporting function to get mark on ozon.ru.
 * @param we
 * @return mark
 */
private fun getMarkFromWEOzon(we: WebElement): Int {
    val c = we.findElement(By.cssSelector(".bStars.inline")).getAttribute("class").split(" ")[2]
    return (c[1] - '0')*2
}

/**
 * Searching for books on ozon.ru
 * @param title book title.
 * @param author book author.
 * @return set of books for this arguments.
 */
@Throws(BooksNotFoundException::class, DriverWasClosedException::class)
fun findBooksOzon(title: String, author: String): HashSet<Book> {
    try {
        val result = HashSet<Book>()

        //Data entry for search
        driver?.get("http://www.ozon.ru/context/div_book/")
        driver?.findElement(By.name("SearchText"))?.sendKeys(title + " " + author)
        driver?.findElement(By.className("eMainSearchBlock_ButtonWrap"))?.click()

        try {
            //if there are no such books
            driver?.findElement(By.className("eZeroSearch_Top"))
            throw BooksNotFoundException()
        } catch (e: NoSuchElementException) {
            (driver as JavascriptExecutor).executeScript("window.scrollTo(0,document.body.scrollHeight);")
            val list = driver!!.findElements(By.cssSelector(".bOneTile.inline.jsUpdateLink"))
            var b: Book?
            for (we in list) {
                b = createBookFromWEOzon(we)
                if (b != null) {
                    result.add(b)
                }
            }
        }

        result.remove(Book("", ""))
        return result

    } catch (e: WebDriverException) {
        finish()
        driver = ChromeDriver()
        throw DriverWasClosedException()
    }
}

/**
 * Supporting function to build book from ozon.
 * @param we div with book info.
 * @return book.
 */
private fun createBookFromWEOzon(we: WebElement): Book? {
    val tmpTitle: String
    val tmpAuthor: String
    try {
        tmpTitle = we.findElement(By.cssSelector(".eOneTile_ItemName")).text
    } catch (e: NoSuchElementException) {
        return null
    }

    try {
        tmpAuthor = we.findElement(By.cssSelector(".bOneTileProperty.mPerson")).text
    } catch (e: NoSuchElementException) {
        tmpAuthor = ""
    }

    return Book(tmpTitle, tmpAuthor)
}


//Labirint

/**
 * Loading reviews from labirint.ru.
 * @param book what we are searching reviews for.
 * @return list of reviews(size = 0 if there are no reviews).
 */
@Throws(DriverWasClosedException::class)
fun loadReviewsLabirint(book:Book):ArrayList<Review> {
    try
    {
        val result = ArrayList<Review>()
        driver?.get("https://www.labirint.ru/")
        driver?.findElement(By.className("search-top-input"))?.sendKeys(book.getTitle() + " " + book.getAuthor())
        driver?.findElement(By.className("search-top-submit"))?.click()
        try
        {
            driver?.findElement(By.className("empty-result"))
            return result
        }
        catch (ex:NoSuchElementException) {
            try
            {
                driver?.findElement(By.cssSelector(".text.navisort-find-text.navisort-find-suggests.ui-autocomplete-input"))
                driver?.get(driver?.findElement(By.className("cover"))?.getAttribute("href"))
            }
            catch (r:NoSuchElementException) {
                //there are only 1 book
                //NOP
            }
            try
            {
                driver?.get(driver?.findElement(By.cssSelector("#product-comments-title a"))?.getAttribute("href"))
                val listAuthor = driver!!.findElements(By.cssSelector(".user-name a"))
                val listText = driver!!.findElements(By.cssSelector(".comment-text.content-comments p"))
                val listDate = driver!!.findElements(By.cssSelector(".comment-footer .date"))
                val listMark = driver!!.findElements(By.id("mark-stars"))
                for (i in listMark.indices)
                {
                    result.add(Review(listText[i].text, listAuthor[i].text, "labirint.ru", parseLabirint(listDate[i].text), getMarkFromWELabirint(listMark[i])))
                }
            }
            catch (e:NoSuchElementException) {
                return result
            }
        }
        return result
    }
    catch (e:WebDriverException) {
        finish()
        driver = ChromeDriver()
        throw DriverWasClosedException()
    }
}


/**
 * Supporting function to get mark on labirint.ru.
 * @param we
 * @return mark
 */
private fun getMarkFromWELabirint(we: WebElement): Int {
    var result = 0
    for(el in we.findElements(By.cssSelector(".star"))) {
        if (el.getAttribute("class").split(" ")[1].equals("full")) {
            result++;
        }
    }
    return result;
}

//Other stuff

/**
 * Closing driver. Running by MainApp.
 */
fun finish() {
    driver?.quit()
}
