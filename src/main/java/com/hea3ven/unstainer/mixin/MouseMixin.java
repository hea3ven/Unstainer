package com.hea3ven.unstainer.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Mouse;

import com.hea3ven.unstainer.client.MockMouse;

@Mixin(Mouse.class)
public class MouseMixin implements MockMouse {
    @Shadow
    private void onMouseButton(long long_1, int int_1, int int_2, int int_3) {
    }

    @Override
    public void simulateMouseButton(long handle) {
        this.onMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_PRESS, 0);
    }
}
