package net.xrrocha.java.spellbound.norvig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class Main {

  public static void main(String[] args) {

    if (args.length < 2) {
      onError("Usage: " + Main.class.getName() + " <dictionaryFilename> [ file1 file2 ... ]");
      throw new IllegalStateException("Return from System.exit(), df?");
    }

    final String dictionaryFilename = args[0];
    final Stream<String> inputLines =
        Arrays.stream(args, 1, args.length)
            .map(Main::lines)
            .reduce(Stream::concat)
            .orElseGet(() -> new BufferedReader(new InputStreamReader(System.in)).lines());

    try {

      final Map<String, Integer> dictionary = loadDictionary(dictionaryFilename);
      SpellingCorrector spellingCorrector = new SpellingCorrector(dictionary);

      inputLines
          .flatMap(line -> Arrays.stream(line.split("\\s+")))
          .filter(SpellingCorrector::isAlphabetic)
          .distinct()
          .map(word -> {
            String corrections =
                spellingCorrector
                    .getCorrections(word).stream()
                    .flatMap(Collection::stream)
                    .collect(joining(","));
            return new SimpleImmutableEntry<>(word, corrections);
          })
          .filter(entry -> !entry.getValue().isEmpty())
          .forEach(entry -> System.out.println(entry.getKey() + "\t" + entry.getValue()));

    } catch (Exception e) {
      onError("Unexpected error: " + e.toString());
    }
  }

  static Map<String, Integer> loadDictionary(String dictionaryFilename) {
    return lines(dictionaryFilename)
        .map(line -> {
          String[] fields = line.split("\\t", 2);
          String word = fields[0];
          int rank = Integer.parseInt(fields[1]);
          return new SimpleImmutableEntry<>(word, rank);
        })
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  static Stream<String> lines(String filename) {
    try {
      return Files.lines(path(filename));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  static Path path(String filename) {
    return FileSystems.getDefault().getPath(filename);
  }

  static void onError(String message) {
    System.err.println(message);
    System.exit(1);
  }
}
