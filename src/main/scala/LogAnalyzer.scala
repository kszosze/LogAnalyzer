package org.soulcave.loganalyzer

import java.time._
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.{chart => jfxsc}

import scala.io.Source
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.chart._
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.KeyCombination.ControlDown
import scalafx.scene.input.{KeyCode, KeyCodeCombination}
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color._
import scalafx.stage.FileChooser

object  LogAnalyzer extends JFXApp {

  stage = new PrimaryStage {

    val xAxis = new CategoryAxis
    val yAxis = new NumberAxis
    val lineChart = new LineChart(xAxis, yAxis)
    lineChart.title = "VMStat"

    title = "Log Analyzer"
    scene = new Scene(600, 400) {
      fill = Black
      val menuBar = new MenuBar()
      val fileMenu = new Menu("File")
      val openItem = new MenuItem("Open")
      openItem.accelerator = new KeyCodeCombination(KeyCode.O,ControlDown)
      val exitItem = new MenuItem("Exit")
      exitItem.accelerator = new KeyCodeCombination(KeyCode.E,ControlDown)
      fileMenu.items = List(openItem, exitItem)
      menuBar.menus = List(fileMenu)
      menuBar.setPrefWidth(600)
      val pane = new StackPane {
        children = lineChart
      }
      content = List(menuBar, pane)

      exitItem.onAction = new EventHandler[ActionEvent] {
        override def handle(event:ActionEvent) {
          Platform.exit()
        }
      }

      openItem.onAction = new EventHandler[ActionEvent] {
        override def handle(event:ActionEvent): Unit = {
          val fileChooser = new FileChooser()

          val selectedFile = fileChooser.showOpenDialog(stage)

          val vmsStat = new VmStatObject("node", LocalDateTime.now())

          for (line <- Source.fromFile(selectedFile).getLines().drop(2)) {
            if (line.matches("[[0-9\\-:]*\\s*]*"))
                {
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
          lineChart.data = createChartData(vmsStat)
          }
        }
      }
    }

  private def createChartData(vmsStat: VmStatObject): ObservableBuffer[jfxsc.XYChart.Series[String, Number]] = {

    val answer = new ObservableBuffer[jfxsc.XYChart.Series[String, Number]]()

    val procsR = new XYChart.Series[String, Number] {
      name = "Procs R"
    }
    val procsB = new XYChart.Series[String, Number] {
      name = "Procs B"
    }
    val memorySwpd = new XYChart.Series[String, Number] {
      name = "Memory Swpd"
    }
    val memoryFree = new XYChart.Series[String, Number] {
      name = "Memory Free"
    }
    val memoryBuff = new XYChart.Series[String, Number] {
      name = "Memory Buff"
    }
    val swapSi = new XYChart.Series[String, Number] {
      name = "Swap Si"
    }
    val swapSo = new XYChart.Series[String, Number] {
      name = "Swap So"
    }
    val swapBi = new XYChart.Series[String, Number] {
      name = "Swap Bi"
    }
    for (i <- 1 to vmsStat.timeStamp.length) {
      procsR.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.procs.get("r").get(i)))
      procsB.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.procs.get("b").get(i)))
      memorySwpd.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.memory.get("swpd").get(i)))
      memoryFree.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.memory.get("free").get(i)))
      memoryBuff.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.memory.get("buff").get(i)))
      swapSi.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.memory.get("si").get(i)))
      swapSo.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.memory.get("so").get(i)))
      swapBi.data.get().add(XYChart.Data[String, Number](vmsStat.timeStamp(i), vmsStat.memory.get("bi").get(i)))
    }
    answer.addAll(procsR, procsB, memorySwpd, memoryFree, memoryBuff, swapSi, swapSo, swapBi)
    answer
  }
  }
