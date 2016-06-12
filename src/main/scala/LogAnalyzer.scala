package org.soulcave.loganalyzer

import javafx.event.{ActionEvent, EventHandler}

import scala.io.Source
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.KeyCombination.ControlDown
import scalafx.scene.input.{KeyCode, KeyCodeCombination}
import scalafx.scene.paint.Color._
import scalafx.stage.FileChooser

object  LogAnalyzer extends JFXApp {

  stage = new PrimaryStage {
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

      content = List(menuBar)

      exitItem.onAction = new EventHandler[ActionEvent] {
        override def handle(event:ActionEvent) {
          Platform.exit()
        }
      }

      openItem.onAction = new EventHandler[ActionEvent] {
        override def handle(event:ActionEvent): Unit = {
          val fileChooser = new FileChooser()

          val selectedFile = fileChooser.showOpenDialog(stage)

          Source.fromFile(selectedFile).getLines().drop(2).map {
            line => {
              if (line.matches("[0-9\\s"))
                {
                  for (value <- line.split(" "))
                    {

                    }
                }
            }
          }
        }
      }
    }
  }


}
