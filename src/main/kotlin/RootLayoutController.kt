import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.FileWriter
import java.io.IOException

class RootLayoutController {
    // Main app ref.
    var mainApp: MainApp? = null

    @FXML
    private fun aboutHandler() {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "About this application"
        alert.headerText = "It is the system for collecting book reviews\n\n" + "Here you can find opinions of people from all over the world for any book you want(almost any)"
        val stage = alert.dialogPane.scene.window as Stage
        stage.icons.add(Image("images/info.png"))
        alert.showAndWait()
    }

    @FXML
    private fun printTxtHendler() {
        //Getting data
        val list = mainApp!!.reviewData
        if (list.size == 0) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Ooops"
            alert.headerText = "Review list is empty."
            val stage = alert.dialogPane.scene.window as Stage
            stage.icons.add(Image("images/alert.png"))

            alert.showAndWait()
            return
        }

        //Asking for file name
        val fileChooser = FileChooser()
        fileChooser.title = "Open Resource File"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.txt"))
        val file = fileChooser.showSaveDialog(mainApp?.primaryStage)

        //Saving
        try {
            FileWriter(file, false).use { writer ->
                for (r in list) {
                    writer.write(r.toString() + "\r\n")
                }
                writer.flush()
            }
        } catch (ex: IOException) {
            println(ex.message)
        }

    }
}