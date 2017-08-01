import javafx.application.Application
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Modality
import javafx.stage.Stage
import model.Book
import model.Review
import services.finish
import services.initDriver
import java.io.IOException

class MainApp:Application() {

    private var rootLayout: BorderPane? = null
    lateinit var primaryStage: Stage
    var reviewData = FXCollections.observableArrayList<Review>()

    override fun start(primaryStage: Stage) {
        initDriver()

        this.primaryStage = primaryStage
        this.primaryStage.title = "BOOK REVIEWS"

        primaryStage.icons.add(Image("images/icon.png"))

        initRootLayout()

        showOverview()
    }

    @Throws(Exception::class)
    override fun stop() {
        finish()
    }

    companion object {
        //Entry point of the app
        @JvmStatic
        fun main(args: Array<String>?) {
            launch(MainApp::class.java)
        }
    }

    /**
     * Giving opportunity to choose the book.
     * @param books - books to choose.
     * *
     * @return selected book.
     */
    fun showChooseBookDialog(books: ObservableList<Book>): Book? {
        try {
            // Loading fxml.
            val loader = FXMLLoader()
            loader.location = MainApp::class.java.getResource("view/ChooseBookDialog.fxml")
            val page = loader.load<BorderPane>()

            // Dialog window Stage.
            val dialogStage = Stage()
            dialogStage.icons.add(Image("images/choice.png"))
            dialogStage.title = "Choose book"
            dialogStage.initModality(Modality.WINDOW_MODAL)
            dialogStage.initOwner(primaryStage)
            val scene = Scene(page)
            dialogStage.scene = scene

            val controller = loader.getController<ChooseBookController>()
            controller.dialogStage = dialogStage
            controller.setBooks(books)

            dialogStage.showAndWait()

            return controller.selectedBook
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * Initializes the root layout.
     */
    private fun initRootLayout() = try {
        // Loading from fxml.
        val loader = FXMLLoader()
        loader.location = MainApp::class.java.getResource("view/RootLayout.fxml")
        rootLayout = loader.load<BorderPane>()

        // Display the scene containing the root layout.
        primaryStage.scene = Scene(rootLayout)
        primaryStage.show()
        primaryStage.isResizable = false

        // Giving the controller access to the main application.
        val controller = loader.getController<RootLayoutController>()
        controller.mainApp = this
    } catch (e: IOException) {
        e.printStackTrace()
    }

    /**
     * Shows information about the reviews in the root layout.
     */
    private fun showOverview() = try {
        // Loading from fxml.
        val loader = FXMLLoader()
        loader.location = MainApp::class.java.getResource("view/Overview.fxml")
        val personOverview = loader.load<BorderPane>()

        rootLayout?.center = personOverview

        // Giving the controller access to the main application.
        val controller = loader.getController<OverviewController>()
        controller.mainApp = this
    } catch (e: IOException) {
        e.printStackTrace()
    }

}