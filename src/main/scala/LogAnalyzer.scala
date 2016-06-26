import java.time._
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.BorderPane
import javafx.scene.{chart => jfxsc}

import scala.collection.JavaConverters._
import scala.io.Source
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart._
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.KeyCombination.ControlDown
import scalafx.scene.input.{KeyCode, KeyCodeCombination}
import scalafx.scene.paint.Color._
import scalafx.scene.{Scene, SceneAntialiasing}
import scalafx.stage.FileChooser

object  LogAnalyzer extends JFXApp {

  var dataBuffer = new ObservableBuffer[jfxsc.XYChart.Series[String, Number]]()

  stage = new PrimaryStage {

    var scaleFactor = 1
    val xAxis = new CategoryAxis
    val yAxis = new NumberAxis
    val lowerX = xAxis.boundsInLocal.value.getMinX
    val upperX = xAxis.boundsInLocal.value.getMaxX
    val lineChart = new LineChart(xAxis, yAxis)
    lineChart.title = "VMStat"
    val bPane = new BorderPane()
    bPane.setCenter(lineChart)


    val menuBar = new MenuBar()
    val fileMenu = new Menu("File")
    val openItem = new MenuItem("Open")
    openItem.accelerator = new KeyCodeCombination(KeyCode.O, ControlDown)
    val exitItem = new MenuItem("Exit")
    exitItem.accelerator = new KeyCodeCombination(KeyCode.E, ControlDown)
    fileMenu.items = List(openItem, exitItem)
    menuBar.menus = List(fileMenu)
    menuBar.setPrefWidth(600)
    bPane.setTop(menuBar)
    title = "Log Analyzer"

    scene = new Scene(600, 400, true, SceneAntialiasing.Balanced) {
      fill = Black
      content.addAll(bPane)
    }

    exitItem.onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent) {
        Platform.exit()
      }
    }

    openItem.onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        val fileChooser = new FileChooser()

        val selectedFile = fileChooser.showOpenDialog(stage)

        val vmsStat = new VmStatObject("node", LocalDateTime.now())

        for (line <- Source.fromFile(selectedFile).getLines().drop(2)) {
          if (line.matches("[[0-9\\-:]*\\s*]*")) {
            val values = line.trim().split("\\s+")

            vmsStat.addProcs("r", Integer.valueOf(values(0)))
            vmsStat.addProcs("b", Integer.valueOf(values(1)))
            vmsStat.addMemory("swpd", Integer.valueOf(values(2)))
            vmsStat.addMemory("free", Integer.valueOf(values(3)))
            vmsStat.addMemory("buff", Integer.valueOf(values(4)))
            vmsStat.addSwap("si", Integer.valueOf(values(5)))
            vmsStat.addSwap("so", Integer.valueOf(values(6)))
            vmsStat.addSwap("bi", Integer.valueOf(values(7)))
            vmsStat.addIo("bi", Integer.valueOf(values(8)))
            vmsStat.addIo("bo", Integer.valueOf(values(9)))
            vmsStat.addSystem("in", Integer.valueOf(values(10)))
            vmsStat.addSystem("cs", Integer.valueOf(values(11)))
            vmsStat.addCpu("us", Integer.valueOf(values(12)))
            vmsStat.addCpu("sy", Integer.valueOf(values(13)))
            vmsStat.addCpu("id", Integer.valueOf(values(14)))
            vmsStat.addCpu("wa", Integer.valueOf(values(15)))
            vmsStat.addCpu("st", Integer.valueOf(values(16)))
            vmsStat.addTimeStamp(values(17) + " " + values(18))
          }
        }
        dataBuffer = createChartData(vmsStat)
        lineChart.data = dataBuffer
      }
    }
    /* lineChart.onMouseClicked(mouseHandler);
    lineChart.setOnMouseDragged(mouseHandler);
    lineChart.setOnMouseEntered(mouseHandler);
    lineChart.setOnMouseExited(mouseHandler);
    lineChart.setOnMouseMoved(mouseHandler);
    lineChart.setOnMousePressed(mouseHandler);
    lineChart.setOnMouseReleased(mouseHandler); */
    lineChart.onScroll = new EventHandler[ScrollEvent] {
      override def handle(event: ScrollEvent) {

        event.consume()

        if (event.getDeltaY() == 0) {
          return
        }
        val SCALE_DELTA = 10
        scaleFactor = scaleFactor + (if (event.getDeltaY() > 0) SCALE_DELTA else -SCALE_DELTA)

        lineChart.data.setValue(cutDataBuffer(dataBuffer, scaleFactor))
      }

      
    }

    private def cutDataBuffer(dataBuffer: ObservableBuffer[jfxsc.XYChart.Series[String, Number]], scaleFactor: Number): ObservableBuffer[jfxsc.XYChart.Series[String, Number]] = {
      val answer = new ObservableBuffer[jfxsc.XYChart.Series[String, Number]]()
      if (dataBuffer.get(0).getData.size() > scaleFactor.intValue())
        dataBuffer foreach {
          serie => {
            val subList = serie.getData.subList(0 + scaleFactor.intValue(), serie.getData.size() - scaleFactor.intValue())
            val subSerie = new XYChart.Series[String, Number]
            subSerie.name = serie.nameProperty().getValueSafe
            for (data <- subList.asScala) subSerie.data.get().add(data)
            answer.add(subSerie)
          }
        }
      else
        answer.addAll(dataBuffer)
      answer
    }

    private def createChartData(vmsStat: VmStatObject): ObservableBuffer[jfxsc.XYChart.Series[String, Number]] = {

      val answer = new ObservableBuffer[jfxsc.XYChart.Series[String, Number]]()

      val procsR = new XYChart.Series[String, Number] {
        name = "Procs R"
      }
      val procsB = new XYChart.Series[String, Number] {
        name = "Procs B"
      }
      val cpuUs = new XYChart.Series[String, Number] {
        name = "Cpu Us"
      }
      val cpuSy = new XYChart.Series[String, Number] {
        name = "Cpu Sy"
      }
      val cpuId = new XYChart.Series[String, Number] {
        name = "Cpu Id"
      }
      val cpuWa = new XYChart.Series[String, Number] {
        name = "Cpu Wa"
      }
      val cpuSt = new XYChart.Series[String, Number] {
        name = "Cpu St"
      }

      for (i <- 0 until (vmsStat.timeStamp.length - 1)) {
        procsR.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i).toString, vmsStat.procs.get("r").get(i)))
        procsB.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i).toString, vmsStat.procs.get("b").get(i)))
        cpuUs.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i).toString, vmsStat.cpu.get("us").get(i)))
        cpuSy.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i).toString, vmsStat.cpu.get("sy").get(i)))
        cpuId.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i).toString, vmsStat.cpu.get("id").get(i)))
        cpuWa.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i).toString, vmsStat.cpu.get("wa").get(i)))
        cpuSt.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i).toString, vmsStat.cpu.get("st").get(i)))

      }
      answer.addAll(procsR, procsB, cpuUs, cpuSy, cpuId, cpuWa, cpuSt)
      answer
    }
  }
}
