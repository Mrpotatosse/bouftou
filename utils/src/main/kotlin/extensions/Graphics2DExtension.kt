package extensions

import java.awt.Color
import java.awt.Graphics2D

fun Graphics2D.fillRectRaw(colorRaw: Long, canvasW: Int, canvasH: Int) {
    val bgR = ((colorRaw shr 16) and 0xFF).toInt()
    val bgG = ((colorRaw shr 8) and 0xFF).toInt()
    val bgB = ((colorRaw) and 0xFF).toInt()
    this.color = if (colorRaw == 0L) Color(0x2B, 0x2B, 0x2B) else Color(bgR, bgG, bgB)
    this.fillRect(0, 0, canvasW, canvasH)
}