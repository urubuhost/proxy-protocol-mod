package app.urubu.haproxy.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CIDRMatcher {

    public static final List<CIDRMatcher> RFC1918 = createRFC1918();

    private final int maskBits;
    private final int maskBytes;
    private final boolean singleIP;
    private final InetAddress cidrAddress;

    private CIDRMatcher(String ipAddress) {
        String[] split = ipAddress.split("/");

        String parsedIPAddress;
        if (split.length >= 2) {
            parsedIPAddress = split[0];

            this.maskBits = Integer.parseInt(split[1]);
            this.singleIP = maskBits == 32;
        } else {
            parsedIPAddress = ipAddress;

            this.maskBits = -1;
            this.singleIP = true;
        }

        this.maskBytes = singleIP ? -1 : maskBits / 8;

        try {
            cidrAddress = InetAddress.getByName(parsedIPAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean matches(InetAddress inetAddress) {
        if (!cidrAddress.getClass().equals(inetAddress.getClass())) {
            return false;
        }

        if (singleIP) {
            return inetAddress.equals(cidrAddress);
        }

        byte[] inetAddressBytes = inetAddress.getAddress();
        byte[] requiredAddressBytes = cidrAddress.getAddress();

        byte finalByte = (byte) (0xFF00 >> (maskBits & 0x07));

        for (int i = 0; i < maskBytes; i++) {
            if (inetAddressBytes[i] != requiredAddressBytes[i]) {
                return false;
            }
        }

        if (finalByte != 0) {
            return (inetAddressBytes[maskBytes] & finalByte) == (requiredAddressBytes[maskBytes] & finalByte);
        }

        return true;
    }

    // https://en.wikipedia.org/wiki/Private_network#Private_IPv4_addresses
    private static List<CIDRMatcher> createRFC1918() {
        List<String> rfc1918 = new ArrayList<>();
        rfc1918.add("10.0.0.0/8");
        rfc1918.add("172.16.0.0/12");
        rfc1918.add("192.168.0.0/16");

        return rfc1918.stream()
                .map(CIDRMatcher::new)
                .collect(Collectors.toList());
    }
}
