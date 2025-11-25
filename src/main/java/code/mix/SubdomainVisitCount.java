package code.mix;

import java.util.*;

public class SubdomainVisitCount {
    public static List<String> subdomainVisits(String[] cpdomains) {
        // HashMap to store subdomain -> total visit count
        Map<String, Integer> countMap = new HashMap<>();

        // Iterate over each input string like "900 google.mail.com"
        for (String cp : cpdomains) {
            // Split into two parts: [0] = count, [1] = domain
            String[] parts = cp.split(" ");
            int count = Integer.parseInt(parts[0]);  // convert string "900" → integer 900
            String domain = parts[1];               // e.g., "google.mail.com"

            // Split domain into fragments using "."
            // "google.mail.com" → ["google", "mail", "com"]
            String[] frags = domain.split("\\.");

            // Build subdomains starting from the right-most part
            String curr = "";
            for (int i = frags.length - 1; i >= 0; i--) {
                // First iteration: curr = "com"
                // Second: curr = "mail.com"
                // Third: curr = "google.mail.com"
                if (curr.isEmpty()) {
                    curr = frags[i];
                } else {
                    curr = frags[i] + "." + curr;
                }

                // Add visit count for this subdomain into HashMap
                // If already exists, add to previous count
                countMap.put(curr, countMap.getOrDefault(curr, 0) + count);
            }
        }

        // Convert map entries into result list in required format
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            // Each entry → "count subdomain"
            result.add(entry.getValue() + " " + entry.getKey());
        }

        return result;  // return the final result list
    }

    // Main function to test our solution
    public static void main(String[] args) {
        // Input domains with counts
        String[] input = {"900 google.mail.com", "50 yahoo.com", "1 intel.mail.com", "5 wiki.org"};

        // Call function
        List<String> output = subdomainVisits(input);

        // Print result list
        System.out.println(output);
    }
}