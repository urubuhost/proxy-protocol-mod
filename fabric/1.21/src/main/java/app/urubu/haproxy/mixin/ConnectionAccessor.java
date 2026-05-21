package app.urubu.haproxy.mixin;

import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(ClientConnection.class)
public interface ConnectionAccessor {

    // net.minecraft.network.ClientConnection#address
    @Accessor
    void setAddress(SocketAddress socketAddress);
}
