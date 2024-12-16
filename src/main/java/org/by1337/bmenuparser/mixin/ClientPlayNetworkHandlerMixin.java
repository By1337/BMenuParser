package org.by1337.bmenuparser.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.by1337.bmenuparser.event.NetworkEvent;
import org.by1337.bmenuparser.event.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(at = @At("HEAD"), method = "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V")
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.CHAT_EVENT.invoker().onChatPacket(packet);
    }

    @Inject(at = @At("HEAD"), method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V")
    private void onGameMessage(PlaySoundS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.SOUND_EVENT.invoker().on(new SoundEvent(
                packet.getSound(),
                packet.getVolume(),
                packet.getVolume()
        ));
    }

    @Inject(at = @At("HEAD"), method = "onParticle(Lnet/minecraft/network/packet/s2c/play/ParticleS2CPacket;)V")
    private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.PARTICLE.invoker().onPacket(packet);
    }

    @Inject(at = @At("HEAD"), method = "onCooldownUpdate(Lnet/minecraft/network/packet/s2c/play/CooldownUpdateS2CPacket;)V")
    private void onCooldownUpdate(CooldownUpdateS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.COOLDOWN_UPDATE.invoker().onPacket(packet);
    }
}
