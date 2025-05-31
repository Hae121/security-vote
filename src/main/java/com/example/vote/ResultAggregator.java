package com.example.vote;

import java.io.*;
import java.util.*;

public class ResultAggregator {

    private static final String VOTE_DIR = "votes";

    public static Map<String, Integer> countVotes() throws IOException {
        Map<String, Integer> results = new HashMap<>();

        File folder = new File(VOTE_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("투표 폴더가 존재하지 않음: " + VOTE_DIR);
            return results;
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String vote = reader.readLine();
                    if (vote != null && !vote.isEmpty()) {
                        results.put(vote, results.getOrDefault(vote, 0) + 1);
                    }
                }
            }
        }

        return results;
    }

    public static void printSummary() throws IOException {
        Map<String, Integer> voteCounts = countVotes();
        int total = voteCounts.values().stream().mapToInt(i -> i).sum();

        System.out.println("📊 투표 결과 요약:");
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            double percent = total > 0 ? (entry.getValue() * 100.0 / total) : 0.0;
            System.out.printf(" - %s: %d표 (%.1f%%)%n", entry.getKey(), entry.getValue(), percent);
        }
    }
}
