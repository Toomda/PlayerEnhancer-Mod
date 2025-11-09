package toomda.playerenhancer.net;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import toomda.playerenhancer.PlayerEnhancer;

public record OpenAugmentC2S(int targetEntityId) implements CustomPayload {
    public static final Id<OpenAugmentC2S> ID =
            new Id<>(Identifier.of(PlayerEnhancer.MOD_ID, "open_augment"));

    // simple codec: just a VarInt with the entity id
    public static final PacketCodec<RegistryByteBuf, OpenAugmentC2S> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, OpenAugmentC2S::targetEntityId, OpenAugmentC2S::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
