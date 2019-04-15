package com.hea3ven.unstainer.sample;

import org.hamcrest.MatcherAssert;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import com.hea3ven.unstainer.api.TestDimension;
import com.hea3ven.unstainer.api.TestRequirement;
import com.hea3ven.unstainer.api.TestWorldType;
import com.hea3ven.unstainer.api.UnstainerInitializationTest;
import com.hea3ven.unstainer.api.UnstainerTest;
import com.hea3ven.unstainer.api.script.Script;
import com.hea3ven.unstainer.api.script.ScriptBuilder;

public class DispenserTest {
    @UnstainerInitializationTest
    public void testDispenserRegistered() {
        MatcherAssert.assertThat("Dispenser block is not registered",
                Registry.BLOCK.containsId(new Identifier("minecraft:dispenser")));
    }

    @UnstainerTest(world = TestWorldType.FLAT, dimension = TestDimension.OVERWORLD,
            requirement = TestRequirement.CHUNK)
    public Script testDispenseWater() {
        BlockPos dispenserPos = new BlockPos(0, 3, 0);
        return new ScriptBuilder().exec(ctx -> {
            BlockPos pos = ctx.getOrigin().add(dispenserPos);
            ctx.getWorld()
                    .setBlockState(pos, Blocks.DISPENSER.getDefaultState()
                            .with(DispenserBlock.FACING, Direction.SOUTH));
            DispenserBlockEntity dispenserEntity =
                    (DispenserBlockEntity) ctx.getWorld().getBlockEntity(pos);
            dispenserEntity.setInvStack(0, new ItemStack(Items.WATER_BUCKET, 1));
            ctx.getWorld().setBlockState(pos.south(), Blocks.AIR.getDefaultState());
        }).wait(1).exec(ctx -> {
            ctx.getWorld()
                    .setBlockState(ctx.getOrigin().add(dispenserPos).east(),
                            Blocks.REDSTONE_BLOCK.getDefaultState());
        }).wait(60).exec(ctx -> {
            BlockState state =
                    ctx.getWorld().getBlockState(ctx.getOrigin().add(dispenserPos).south());
            assert state.getBlock() == Blocks.WATER;
        }).build();
    }

    @UnstainerTest(requirement = TestRequirement.CHUNK)
    public Script testDispenseWaterFromBelow() {
        return new ScriptBuilder().setBlockState(new BlockPos(0, 2, 0),
                Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, Direction.UP))
                .exec(ctx -> {
                    DispenserBlockEntity dispenserEntity = (DispenserBlockEntity) ctx.getWorld()
                            .getBlockEntity(ctx.getOrigin().up(2));
                    dispenserEntity.setInvStack(0, new ItemStack(Items.WATER_BUCKET, 1));
                })
                .setBlockState(new BlockPos(0, 3, 0), Blocks.AIR.getDefaultState())
                .wait(1)
                .setBlockState(new BlockPos(0, 2, 1), Blocks.REDSTONE_BLOCK.getDefaultState())
                .wait(60)
                .assertBlockState(new BlockPos(0, 4, 0), Blocks.WATER.getDefaultState())
                .build();
    }
}
