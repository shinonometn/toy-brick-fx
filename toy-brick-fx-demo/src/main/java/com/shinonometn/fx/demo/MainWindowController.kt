package com.shinonometn.fx.demo

import com.shinonometn.fx.view.FxmlView
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField

class MainWindowController : FxmlView("views/MainWindow.fxml") {
    private val label: Label by fxId()
    private val textField: TextField by fxId()
    private val button: Button by fxId()
    private val exitButton: Button by fxId()

    private var xOffset = 0.0
    private var yOffset = 0.0

    private var isMouseDragging = false

    override suspend fun init() {
        label.textProperty().bind(textField.textProperty())

        root.apply {
            setOnMousePressed {
                xOffset = context.stage.x - it.screenX
                yOffset = context.stage.y - it.screenY
            }

            setOnMouseDragged {
                isMouseDragging = true

                if (!it.isPrimaryButtonDown) return@setOnMouseDragged

                context.stage.x = it.screenX + xOffset
                context.stage.y = it.screenY + yOffset

                it.consume()
            }

            setOnMouseReleased {
                isMouseDragging = false
            }
        }

        button.setOnAction {
            Alert(Alert.AlertType.INFORMATION).apply {
                headerText = "Hello"
                contentText = "You just inputted: ${textField.text}"
            }.showAndWait()
        }

        exitButton.setOnAction {
            context.app.onAppExitRequested()
        }
    }
}