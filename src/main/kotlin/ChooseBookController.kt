import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.stage.Stage
import model.Book


class ChooseBookController {

    //items
    @FXML
    lateinit private var bookTable: TableView<Book>
    @FXML
    lateinit private var titleColumn: TableColumn<Book, String>
    @FXML
    lateinit private var authorColumn: TableColumn<Book, String>

    var dialogStage: Stage? = null
    var selectedBook: Book? = null

    @FXML
    private fun initialize() {
        titleColumn.setCellValueFactory { cellData -> cellData.value.titleProperty() }
        authorColumn.setCellValueFactory { cellData -> cellData.value.authorProperty() }
    }

    /**
     * Setting book list.
     * @param bookList
     */
    fun setBooks(bookList: ObservableList<Book>) {
        bookTable.items = bookList
    }

    /**
     * Button Select clicked.
     */
    @FXML
    private fun handleSelect() {
        selectedBook = bookTable.selectionModel.selectedItem
        dialogStage?.close()
    }

    /**
     * Button Cancel clicked.
     */
    @FXML
    private fun handleCancel() {
        dialogStage?.close()
    }
}