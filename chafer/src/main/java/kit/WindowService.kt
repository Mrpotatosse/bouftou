package kit

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.NULL
import java.awt.Dimension

class WindowService {
    @Volatile
    private var running = true

    fun open(size: Dimension = Dimension(1280, 1024), title: String = "Main App") {
        GLFWErrorCallback.createPrint(System.err).set()
        check(glfwInit()) { "Failed to initialize GLFW" }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        val window = glfwCreateWindow(size.width, size.height, title, NULL, NULL)
        check(window != NULL) { "Failed to create GLFW window" }
        glfwSetKeyCallback(window) { win, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(win, true)
        }
        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)
        glfwShowWindow(window)
        Runtime.getRuntime().addShutdownHook(Thread { running = false })
        GL.createCapabilities()
        while (running && !glfwWindowShouldClose(window)) {
            glfwSwapBuffers(window)
            glfwPollEvents()
        }
        destroy(window)
    }

    fun destroy(window: Long) {
        glfwDestroyWindow(window)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }
}