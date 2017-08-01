import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import services.finish

class TestFX : ApplicationTest() {

    var ma:MainApp = MainApp()

    override fun start(stage: Stage?){
        ma.start(Stage())
    }

    @Test fun t1() {
        press(KeyCode.TAB)
        write("Война и мир")
        press(KeyCode.ENTER)
        finish()
    }

    @Test fun t2() {
        clickOn("File")
        clickOn("Save as .txt")
        finish()
    }

    @Test fun t3() {
        clickOn("Help")
        clickOn("About")
        finish()
    }

}