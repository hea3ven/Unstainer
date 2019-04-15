package com.hea3ven.unstainer.client.gui

import com.hea3ven.unstainer.test.TestResult
import com.hea3ven.unstainer.test.TestResults
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.resource.language.I18n
import net.minecraft.network.chat.TextComponent

class UnstainerResultScreen(private val results: TestResults) : Screen(TextComponent("Test Results")) {

    private var f = true

    override fun init() {
        super.init()
        addButton(ButtonWidget(width / 2 - 100, height - 20 - 5, 200, 20, I18n.translate("menu.quit"), {
            minecraft!!.scheduleStop();
        }));
    }

    override fun render(int_1: Int, int_2: Int, float_1: Float) {
        this.renderDirtBackground(0);
        super.render(int_1, int_2, float_1)
        this.drawCenteredString(this.font, this.title.formattedText, this.width / 2, this.height / 4 - 60 + 20,
                16777215);
        this.drawString(this.font,
                "${results.results.values.count { it == TestResult.SUCCESS }} successful, ${results.results.values.count { it == TestResult.FAIL }} failed, ${results.results.values.count { it == TestResult.ERROR }} error",
                this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
    }

    override fun tick() {
        super.tick()
        if (minecraft != null && minecraft!!.world != null && f) {
            f = false
        }
    }
}