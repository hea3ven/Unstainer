package com.hea3ven.unstainer.api.script

import com.hea3ven.tools.commonutils.util.ItemStackUtil
import com.hea3ven.unstainer.api.Action
import com.hea3ven.unstainer.api.TestContext
import com.hea3ven.unstainer.client.MockMouse
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BoundingBox
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
import java.util.*
import java.util.function.Consumer

class Script(val actions: List<Action>) {}

class ScriptBuilder {
    private val actions = mutableListOf<Action>()

    fun build() = Script(actions)

    fun exec(closure: Consumer<TestContext>): ScriptBuilder {
        return run(closure::accept)
    }

    fun run(closure: (TestContext) -> Any): ScriptBuilder {
        actions.add(RunAction(closure))
        return this
    }

    fun wait(count: Int): ScriptBuilder {
        actions.add(TickWaitAction(count))
        return this
    }

    fun setBlockState(pos: BlockPos, state: BlockState): ScriptBuilder {
        actions.add(RunAction({ ctx -> ctx.world.setBlockState(ctx.origin.add(pos), state) }))
        return this
    }

    fun playerMode(mode: GameMode): ScriptBuilder {
        actions.add(RunAction({ ctx ->
            val player = ctx.world.players[0] as ServerPlayerEntity
            player.setGameMode(mode)
        }))
        return this
    }

    fun playerMove(pos: Vec3d, yaw: Float, pitch: Float): ScriptBuilder {
        actions.add(RunAction({ ctx ->
            val player = ctx.world.players[0] as ServerPlayerEntity
            //            player.interactionManager.gameMode = GameMode.SPECTATOR
            val absPos = pos.add(Vec3d(ctx.origin))
            val lookAt = Vec3d(0.5, 4.5, 0.5)
            val eyesPos = absPos //.add(0.0, 1.5, 0.0)
            val dX = eyesPos.x - lookAt.x
            val dY = eyesPos.y - lookAt.y
            val dZ = eyesPos.z - lookAt.z
            System.out.println("yaw = " + Math.atan2(dZ, dX).toFloat())
            System.out.println("pitch = " + (Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI).toFloat())
            player.networkHandler.teleportRequest(absPos.x, absPos.y, absPos.z.toDouble(), yaw,
                    pitch, //-45.0f, 55.0f, ///*Math.atan2(dZ, dX).toFloat()*/, (Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI).toFloat(),
                    EnumSet.allOf(PlayerPositionLookS2CPacket.Flag::class.java))
        }))
        return this
    }

    fun playerInventory(inventoryHandler: (PlayerInventory) -> Unit): ScriptBuilder {
        actions.add(RunAction({ ctx ->
            val player = ctx.world.players[0] as ServerPlayerEntity
            inventoryHandler.invoke(player.inventory)
        }))
        return this
    }

    fun playerStartLeftClick(): ScriptBuilder {
        actions.add(RunAction({ ctx ->
            val client = MinecraftClient.getInstance()
            (client.mouse as MockMouse).simulateMouseButton(client.window.handle)
        }))

        return this
    }

    fun assertBlockState(pos: BlockPos, expected: BlockState): ScriptBuilder {
        actions.add(RunAction({ ctx ->
            val result = ctx.world.getBlockState(ctx.origin.add(pos))
            if (expected != result) {
                throw AssertionError("Expected block at $pos to be $expected but was $result")
            }
        }))
        return this
    }

    fun assertItemEntities(box: BoundingBox, expected: Array<ItemStack>): ScriptBuilder {
        actions.add(RunAction { ctx ->
            val extraItems = mutableListOf<ItemStack>()
            val result = ctx.world.getEntities(ItemEntity::class.java,
                    BoundingBox(ctx.origin.x + box.minX, ctx.origin.y + box.minY, ctx.origin.z + box.minZ,
                            ctx.origin.x + box.maxX, ctx.origin.y + box.maxY, ctx.origin.z + box.maxZ))
            for (itemEntity in result) {
                val stack = itemEntity.stack.copy()
                for (expectedStack in expected) {
                    if (ItemStackUtil.areStacksCombinable(stack,
                                    expectedStack) && expectedStack.amount >= stack.amount) {
                        expectedStack.amount -= stack.amount
                        stack.amount = 0
                        break;
                    }
                }
                if (!stack.isEmpty) {
                    extraItems.add(stack)
                }
            }
            val missingItems = expected.filter { it.isEmpty.not() }
            if (missingItems.isNotEmpty()) {
                throw AssertionError("Expected item entities at $box not found: $missingItems")
            }
            if (extraItems.isNotEmpty()) {
                throw AssertionError("Not expected item entities at $box found: $extraItems")
            }
        })
        return this
    }
}