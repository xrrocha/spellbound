package net.xrrocha.spellbound.java.intro;

import info.debatty.java.stringsimilarity.Damerau;
import info.debatty.java.stringsimilarity.interfaces.MetricStringDistance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BruteForceSpellChecker {
  final static Set<String> dictionary =
      loadDictionary("spellbound-ngram/data/dictionary.tsv");

  static final double maxDistance = 0.275;
  static final MetricStringDistance metricStringDistance = new Damerau();

  public static void main(String[] args) {

    final List<String> words;
    if (args.length > 0) {
      words = Arrays.stream(args)
          .map(word -> word.trim().toLowerCase())
          .collect(Collectors.toList());
    } else {
      words = Arrays.asList("speling", "sleping", "ricsha", "xfvbq", "spelling");
    }

    for (String word : words) {
      System.out.println(word + ": " + getSuggestions(word));
    }
  }

  public static Optional<List<String>> getSuggestions(String word) {
    if (dictionary.contains(word)) {
      // Word occurs in dictionary; suggestions not applicable
      return Optional.empty();
    }

    // Examine every dictionary word (ugh!)
    List<Entry<String, Double>> similarEntries =
        dictionary.stream()
            .map(dictionaryWord -> {
              // Compute distance between typo and dictionary word
              double distance = getDistance(word, dictionaryWord);
              return new SimpleEntry<>(dictionaryWord, distance);
            })
            // Omit words too far apart from typo
            .filter(entry -> entry.getValue() <= maxDistance)
            // Order suggested words so closer matches show first
            .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
            .collect(Collectors.toList());

    List<String> similarWords =
        similarEntries.stream()
            .map(Entry::getKey) // Keep words only eschewing scores
            .collect(Collectors.toList());

    // Return (possibly empty) list of dictionary words similar to typo
    return Optional.of(similarWords);
  }

  static double getDistance(String s1, String s2) {
    int maxLength = Math.max(s1.length(), s2.length());
    return 1.0 - (maxLength - metricStringDistance.distance(s1, s2)) / maxLength;
  }

  static Set<String> loadDictionary(String dictionaryFilename) {
    Path path = FileSystems.getDefault().getPath(dictionaryFilename);
    try {
      Stream<String> lines =
          Files.lines(path, StandardCharsets.UTF_8)
              .map(line -> line.split("\\t")[0]);
      return lines.collect(Collectors.toSet());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
