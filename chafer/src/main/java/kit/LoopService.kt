package kit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.GLFW.glfwPollEvents

class LoopService {
    fun start(block: suspend CoroutineScope.() -> Unit) = runBlocking {
        launch { block() }
        while (true) {
            glfwPollEvents()
        }
    }
}