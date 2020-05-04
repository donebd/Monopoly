import tornadofx.*

class MyApp: App(MyView::class)

class MyView: View("Monopoly") {
    override val root = vbox {
        button("Press me")
        label("Waiting")
    }
}
