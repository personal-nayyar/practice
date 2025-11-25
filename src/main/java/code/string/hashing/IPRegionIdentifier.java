package code.string.hashing;

import java.util.*;

/**
 The system should identify a user's region based on their IP adreess.
 Ipv$ addressed are written as four numbers serrated y periods, like "127.10.20.30". Each number is called an octer. Based on the first octer, address fall into one the five regions:
 1. 0.0.0.0 -127.255.255.255
 2.128.0.0.0 - 191.255.255.255
 3.192.0.0.0 - 223.255.255.255
 4.224.0.0.0 - 239.255.255.255
 5.240.0.0.0 - 255.255.255.255
 you are given a list of IP addresssed. For each one, check of it is valid, if it is validated, return the region index(1 through 5). If it is not validated, return -1
 Input: ["127.0.0.1", "192.168.1.1", "256.256.256.256", "244.178.44.111"]
 Output: [1, 3, -1, 4]
 */
public class IPRegionIdentifier {

    public static void main(String[] args) {
        List<String> ipAddresses = Arrays.asList(
                "127.10.20.30",
                "128.0.0.1",
                "192.168.1.1",
                "224.12.0.4",
                "250.10.5.6",
                "300.1.2.3",
                "10.10.10"
        );

        for (String ip : ipAddresses) {
            int region = getRegion(ip);
            System.out.println(ip + " -> Region: " + region);
        }
    }

    /**
     * Determines the region of a given IP address.
     * @param ipAddress The IP address in string form.
     * @return Region index (1–5) if valid, -1 if invalid.
     */
    public static int getRegion(String ipAddress) {
        if (!isValidIp(ipAddress)) {
            return -1;
        }

        int firstOctet = Integer.parseInt(ipAddress.split("\\.")[0]);

        if (firstOctet >= 0 && firstOctet <= 127) return 1;
        else if (firstOctet >= 128 && firstOctet <= 191) return 2;
        else if (firstOctet >= 192 && firstOctet <= 223) return 3;
        else if (firstOctet >= 224 && firstOctet <= 239) return 4;
        else if (firstOctet >= 240 && firstOctet <= 255) return 5;
        else return -1;
    }

    /**
     * Validates an IP address string.
     * @param ipAddress The IP address to validate.
     * @return true if valid, false otherwise.
     */
    private static boolean isValidIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) return false;

        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) return false;

        for (String part : parts) {
            if (part.isEmpty() || part.length() > 3) return false;
            if (!part.matches("\\d+")) return false; // ensure numeric
            int num = Integer.parseInt(part);
            if (num < 0 || num > 255) return false;
            if (part.startsWith("0") && part.length() > 1) return false; // avoid "01", "001"
        }
        return true;
    }
}
