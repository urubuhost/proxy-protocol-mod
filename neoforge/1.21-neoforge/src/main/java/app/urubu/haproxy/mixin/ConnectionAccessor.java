package app.urubu.haproxy.mixin;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(Connection.class)
public interface ConnectionAccessor {

    // net.minecraft.network.Connection#address
    @Accessor
    void setAddress(SocketAddress socketAddress);
}
