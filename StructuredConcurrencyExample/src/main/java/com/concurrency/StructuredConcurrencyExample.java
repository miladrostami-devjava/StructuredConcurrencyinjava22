package com.concurrency;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class StructuredConcurrencyExample {

    public static void main(String[] args) {
        // List of URLs to download
        List<String> urls = List.of(
                "https://example.com",
                "https://example.org",
                "https://example.net"
        );

        // Create an ExecutorService with a fixed thread pool of 3 threads
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
            // Download data concurrently
            List<CompletableFuture<String>> futures = urls.stream()
                    .map(url -> CompletableFuture.supplyAsync(() -> downloadContent(url), executorService))
                    .collect(Collectors.toList());

            // Gather results
            List<String> results = futures.stream()
                    .map(CompletableFuture::join) // Wait for completion and get the result
                    .collect(Collectors.toList());

            // Display results
            results.forEach(System.out::println);
        } finally {
            // Shut down the ExecutorService
            executorService.shutdown();
        }
    }

    private static String downloadContent(String urlString) {
        try {
            // Download data from the URL
            URL url = new URL(urlString);
            try (var in = url.openStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to download content from " + urlString, e);
        }
    }
}
