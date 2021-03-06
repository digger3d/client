package org.kamiblue.client.gui.hudgui.elements.misc

import net.minecraft.client.Minecraft
import org.kamiblue.client.event.SafeClientEvent
import org.kamiblue.client.event.events.RenderEvent
import org.kamiblue.client.gui.hudgui.LabelHud
import org.kamiblue.client.setting.GuiConfig.setting
import org.kamiblue.client.util.CircularArray
import org.kamiblue.client.util.TickTimer
import org.kamiblue.client.util.graphics.AnimationUtils
import org.kamiblue.event.listener.listener
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object FPS : LabelHud(
    name = "FPS",
    category = Category.MISC,
    description = "Frame per second in game"
) {

    private val showAverage = setting("Show Average", true)
    private val showMin = setting("Show Min", false)
    private val showMax = setting("Show Max", false)

    private val timer = TickTimer()
    private var prevFps = 0
    private var currentFps = 0

    private val longFps = CircularArray.create<Int>(10)

    private var prevAvgFps = 0
    private var currentAvgFps = 0

    init {
        listener<RenderEvent> {
            if (timer.tick(1000L)) {
                prevFps = currentFps
                currentFps = Minecraft.getDebugFPS()

                longFps.add(currentFps)

                prevAvgFps = currentAvgFps
                currentAvgFps = longFps.average().roundToInt()
            }
        }
    }

    override fun SafeClientEvent.updateText() {
        val deltaTime = AnimationUtils.toDeltaTimeFloat(timer.time) / 1000.0f
        val fps = (prevFps + (currentFps - prevFps) * deltaTime).roundToInt()
        val avg = (prevAvgFps + (currentAvgFps - prevAvgFps) * deltaTime).roundToInt()

        var min = 6969
        var max = 0
        for (value in longFps) {
            if (value != 0) min = min(value, min)
            max = max(value, max)
        }

        displayText.add("FPS", secondaryColor)
        displayText.add(fps.toString(), primaryColor)

        if (showAverage.value) {
            displayText.add("AVG", secondaryColor)
            displayText.add(avg.toString(), primaryColor)
        }

        if (showMin.value) {
            displayText.add("MIN", secondaryColor)
            displayText.add(min.toString(), primaryColor)
        }

        if (showMax.value) {
            displayText.add("MAX", secondaryColor)
            displayText.add(max.toString(), primaryColor)
        }
    }

}