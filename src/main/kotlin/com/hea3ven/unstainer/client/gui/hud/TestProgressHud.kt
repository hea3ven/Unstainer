package com.hea3ven.unstainer.client.gui.hud

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import java.util.UUID

import com.google.common.collect.Maps
import com.mojang.blaze3d.platform.GlStateManager

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.hud.ClientBossBar
import net.minecraft.client.network.packet.BossBarS2CPacket
import net.minecraft.entity.boss.BossBar
import net.minecraft.util.Identifier

import com.hea3ven.unstainer.test.TestResults

@Environment(EnvType.CLIENT)
class TestProgressHud : DrawableHelper() {
    private val bossBars = Maps.newLinkedHashMap<UUID, ClientBossBar>()

    fun draw(client: MinecraftClient) {
        val int_1 = client.window.scaledWidth
        val int_2 = 12

        val int_3 = int_1 / 2 - 91
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client.textureManager.bindTexture(BAR_TEX)
        drawBossBar(int_3, int_2)
        val string_1 = "Running Tests"
        val int_5 = client.textRenderer.getStringWidth(string_1)
        val int_6 = int_1 / 2 - int_5 / 2
        val int_7 = int_2 - 9
        client.textRenderer.drawWithShadow(string_1, int_6.toFloat(), int_7.toFloat(), 16777215)
        client.textRenderer.javaClass
    }

    private fun drawBossBar(int_1: Int, int_2: Int) {
        blit(int_1, int_2, 0, 5 * 2, 182, 5)
        //        if (bossBar_1.getOverlay() != BossBar.Overlay.PROGRESS) {
        //            blit(int_1, int_2, 0, 80 + (bossBar_1.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        //        }

        if (testResults != null) {
            val int_3 = (testResults!!.progress * 183.0f).toInt()
            if (int_3 > 0) {
                blit(int_1, int_2, 0, 5 * 2 + 5, int_3, 5)
                //            if (bossBar_1.getOverlay() != BossBar.Overlay.PROGRESS) {
                //                blit(int_1, int_2, 0, 80 + (bossBar_1.getOverlay().ordinal() - 1) * 5 * 2 + 5,
                //                        int_3, 5);
                //            }
            }
        }
    }

    fun handlePacket(bossBarS2CPacket_1: BossBarS2CPacket) {
        if (bossBarS2CPacket_1.type == BossBarS2CPacket.Type.ADD) {
            bossBars[bossBarS2CPacket_1.uuid] = ClientBossBar(bossBarS2CPacket_1)
        } else if (bossBarS2CPacket_1.type == BossBarS2CPacket.Type.REMOVE) {
            bossBars.remove(bossBarS2CPacket_1.uuid)
        } else {
            (bossBars[bossBarS2CPacket_1.uuid] as ClientBossBar).handlePacket(bossBarS2CPacket_1)
        }
    }

    fun clear() {
        bossBars.clear()
    }

    fun shouldPlayDragonMusic(): Boolean {
        if (!bossBars.isEmpty()) {
            val var1 = bossBars.values.iterator()

            while (var1.hasNext()) {
                val bossBar_1 = var1.next() as BossBar
                if (bossBar_1.hasDragonMusic()) {
                    return true
                }
            }
        }

        return false
    }

    fun shouldDarkenSky(): Boolean {
        if (!bossBars.isEmpty()) {
            val var1 = bossBars.values.iterator()

            while (var1.hasNext()) {
                val bossBar_1 = var1.next() as BossBar
                if (bossBar_1.darkenSky) {
                    return true
                }
            }
        }

        return false
    }

    fun shouldThickenFog(): Boolean {
        if (!bossBars.isEmpty()) {
            val var1 = bossBars.values.iterator()

            while (var1.hasNext()) {
                val bossBar_1 = var1.next() as BossBar
                if (bossBar_1.thickenFog) {
                    return true
                }
            }
        }

        return false
    }

    companion object {
        var testResults: TestResults? = null
        private val BAR_TEX = Identifier("textures/gui/bars.png")
    }
}
