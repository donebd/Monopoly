package monopoly.`interface`

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import tornadofx.runAsync
import java.io.File

class Sound{

    fun motion() {
        val sound = Media(File("src/main/resources/monopoly/sounds/motion.wav").toURI().toString())
        play(sound, 1.0)
    }

    fun stonks() {
        val sound = Media(File("src/main/resources/monopoly/sounds/stonks.wav").toURI().toString())
        play(sound, .5)
    }

    fun collect() {
        val sound = Media(File("src/main/resources/monopoly/sounds/collect.mp3").toURI().toString())
        play(sound, .1)
    }

    fun prison() {
        val sound= Media(File("src/main/resources/monopoly/sounds/prison.wav").toURI().toString())
        play(sound, 1.0)
    }

    fun penalty() {
        val sound = Media(File("src/main/resources/monopoly/sounds/penalty.mp3").toURI().toString())
        play(sound, .25)
    }

    fun secret() {
        val sound = Media(File("src/main/resources/monopoly/sounds/secret.mp3").toURI().toString())
        play(sound, .2)
    }

    fun win() {
        val sound = Media(File("src/main/resources/monopoly/sounds/win.wav").toURI().toString())
        play(sound, .6)
    }

    private fun play(media: Media, volume: Double) {
        runAsync {
            val mediaPlayer = MediaPlayer(media)
            mediaPlayer.volumeProperty().set(volume)
            mediaPlayer.play()
        }
    }
}