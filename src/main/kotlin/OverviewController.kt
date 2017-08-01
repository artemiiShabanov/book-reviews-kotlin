import Exceptions.BookNotSelectedException
import Exceptions.BooksNotFoundException
import Exceptions.DriverWasClosedException
import Exceptions.ReviewsNotFoundException
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.stage.Stage
import model.Book
import model.Review
import services.findBooksGoogle
import services.format
import services.loadReviewsLabirint
import services.loadReviewsOzon
import java.time.LocalDate
import java.util.*

class OverviewController {

    //Table items.
    @FXML
    lateinit private var reviewTable: TableView<Review>
    @FXML
    lateinit private var textColumn: TableColumn<Review, String>
    @FXML
    lateinit private var dateColumn: TableColumn<Review, LocalDate>
    @FXML
    lateinit private var  resourceColumn: TableColumn<Review, String>

    //Chosen review items.
    @FXML
    lateinit private var  chosenDateLabel: Label
    @FXML
    lateinit private var chosenSourceLabel: Label
    @FXML
    lateinit private var  chosenAuthorLabel: Label
    @FXML
    lateinit private var  chosenText: TextArea
    @FXML
    lateinit private var  stars: HBox

    //Search items.
    @FXML
    lateinit private var bookName: Label
    @FXML
    lateinit private var  nameField: TextField
    @FXML
    lateinit private var authorField: TextField
    @FXML
    lateinit private var  btnFind: Button

    // Main app ref.
    var mainApp: MainApp? = null
    set(value) {
        field = value
        // Connecting data to review table
        reviewTable.items = mainApp?.reviewData
    }
    /**
     * Initialization.
     * Running after fxml loading.
     */
    @FXML
    private fun initialize() {
        textColumn.setCellValueFactory { cellData -> cellData.value.textProperty() }
        dateColumn.setCellValueFactory { cellData -> cellData.value.dateProperty() }
        resourceColumn.setCellValueFactory { cellData -> cellData.value.resourceProperty() }

        reviewTable.fixedCellSize = 25.0

        // Cleaning extra review info
        showReviewDetails(null)

        // Adding listener to row choosing.
        reviewTable.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue -> showReviewDetails(newValue) }

        nameField.textProperty().addListener { observable, oldValue, newValue -> disable() }
        authorField.textProperty().addListener { observable, oldValue, newValue -> disable() }
    }

    /**
     * Disable find button if it is necessary.
     * @return
     */
    private fun disable() {
        btnFind.isDisable = nameField.text.isEmpty() && authorField.text.isEmpty()
    }

    /**
     * Filling fields from review parameter.
     * Review = null - cleaning all fields.

     * @param review — review or null
     */
    private fun showReviewDetails(review: Review?) {
        if (review != null) {
            // Filling.
            chosenAuthorLabel.text = review.getAuthor()
            chosenSourceLabel.text = review.getResource()
            chosenText.isVisible = true
            chosenText.text = review.getText()
            chosenDateLabel.text = format(review.getDate())
            stars.children.clear()
            for (i in 0..review.getMark() - 1) {
                val img = ImageView("images/star.png")
                img.fitHeight = 20.0
                img.fitWidth = 20.0
                stars.children.add(img)
            }
            for (i in review.getMark()..9) {
                val img = ImageView("images/empty-star.png")
                img.fitHeight = 20.0
                img.fitWidth = 20.0
                stars.children.add(img)
            }
        } else {
            // Cleaning.
            chosenDateLabel.text = ""
            chosenSourceLabel.text = ""
            chosenAuthorLabel.text = ""
            chosenText.isVisible = false
            stars.children.clear()
        }
    }

    /**
     * Searching button pressed.
     */
    @FXML
    private fun handleSearch() {
        try {
            bookName.text = search(nameField.text, authorField.text)
        } catch (e: ReviewsNotFoundException) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Ooops"
            alert.headerText = "There are no reviews for this book"
            val stage = alert.dialogPane.scene.window as Stage
            stage.icons.add(Image("images/alert.png"))

            alert.showAndWait()
        } catch (e: BookNotSelectedException) {
            //NOP
        } catch (e: BooksNotFoundException) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Ooops"
            alert.headerText = "There are no such books"
            val stage = alert.dialogPane.scene.window as Stage
            stage.icons.add(Image("images/alert.png"))

            alert.showAndWait()
        } catch (e: DriverWasClosedException) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Ooops"
            alert.headerText = "Some problems with a web. Try again."
            val stage = alert.dialogPane.scene.window as Stage
            stage.icons.add(Image("images/alert.png"))

            alert.showAndWait()
        }

    }

    /**
     * Main search method.
     * @return book title.
     * *
     * @throws ReviewsNotFoundException if there are no such books in the Internet.
     * *
     * @throws BookNotSelectedException if user did not select any book.
     */
    @Throws(ReviewsNotFoundException::class, BookNotSelectedException::class, BooksNotFoundException::class, DriverWasClosedException::class)
    private fun search(title: String, author: String): String {

        val bookSet: HashSet<Book>
        val selectedBook: Book?
        val newReviews: ArrayList<Review>
        val reviewData = mainApp?.reviewData

        bookSet = findBooksGoogle(title, author)

        when (bookSet.size) {
            0 -> throw BooksNotFoundException()
            1 -> {
                selectedBook = bookSet.iterator().next()
                reviewData?.clear()
                newReviews = loadReviewsOzon(selectedBook)
                newReviews.addAll(loadReviewsLabirint(selectedBook))
                if (newReviews.size == 0) {
                    throw ReviewsNotFoundException()
                }
                reviewData?.addAll(newReviews)
            }
            else -> {
                selectedBook = mainApp?.showChooseBookDialog(FXCollections.observableArrayList(bookSet))
                if (selectedBook == null) throw BookNotSelectedException()
                reviewData?.clear()
                newReviews = loadReviewsOzon(selectedBook)
                newReviews.addAll(loadReviewsLabirint(selectedBook))
                if (newReviews.size == 0) {
                    throw ReviewsNotFoundException()
                }
                reviewData?.addAll(newReviews)
            }
        }

        return selectedBook.getTitle()
    }





}