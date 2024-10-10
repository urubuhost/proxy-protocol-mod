package app.urubu.haproxy.mixin;

import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(NetworkManager.class)
public interface ConnectionAccessor {

    // net.minecraft.network.NetworkManager#socketAddress
    @Accessor
    void setSocketAddress(SocketAddress socketAddress);
}
