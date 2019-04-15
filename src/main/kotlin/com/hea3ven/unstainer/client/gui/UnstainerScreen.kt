package com.hea3ven.unstainer.client.gui

import com.hea3ven.unstainer.api.TestRegistry
import com.hea3ven.unstainer.client.gui.hud.TestProgressHud
import com.hea3ven.unstainer.test.TestExecutor
import com.hea3ven.unstainer.test.discovery.TestScanner
import com.hea3ven.unstainer.test.game.MinecraftGameManager
import net.fabricmc.fabric.api.event.client.ClientTickCallback
import net.fabricmc.fabric.api.event.world.WorldTickCallback
import net.minecraft.client.gui.screen.Screen
import net.minecraft.network.chat.TextComponent

class UnstainerScreen : Screen(TextComponent("Unstainer")) {
	private lateinit var testExecutor: TestExecutor

	private var initializations = 0

	override fun init() {
		super.init()
		minecraft!!.options.pauseOnLostFocus = false
//		val testInitializers = TestRegistry.containers
/*		(FabricLoader.getInstance() as net.fabricmc.loader.FabricLoader)
				.getEntrypoints()
				.allMods
				.filterIsInstance(TestModInitializer::class.java)*/
		val testScanner = TestScanner()
		testExecutor = TestExecutor(testScanner.scan(TestRegistry.containers), MinecraftGameManager())
		initializations++
		TestProgressHud.testResults = testExecutor.results
	}

	override fun tick() {
		if (initializations == 2) {
			WorldTickCallback.EVENT.register(WorldTickCallback(testExecutor::onTick))
			ClientTickCallback.EVENT.register(ClientTickCallback(testExecutor::onClientTick))
			testExecutor.start()
//            val bossBar = ServerBossBar(StringTextComponent("Running Tests"), BossBar.Color.YELLOW,
//                    BossBar.Overlay.PROGRESS)
//            minecraft!!.inGameHud.bossBarHud.handlePacket(BossBarS2CPacket(BossBarS2CPacket.Type.ADD, bossBar))
		}
	}

}