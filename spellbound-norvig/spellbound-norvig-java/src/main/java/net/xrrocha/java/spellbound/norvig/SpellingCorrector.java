package net.xrrocha.java.spellbound.norvig;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

public class SpellingCorrector {

  private final Map<String, Integer> dictionary;

  static final Pattern ALPHABETIC = Pattern.compile("^[a-z]+$");

  static final String[] LETTERS = "abcdefghijklmnopqrstuvwxyz".split("");

  static final List<Function<List<Split>, List<String>>> edits = List.of(
      SpellingCorrector::deletes,
      SpellingCorrector::transposes,
      SpellingCorrector::replaces,
      SpellingCorrector::inserts
  );

  public SpellingCorrector(Map<String, Integer> dictionary) {
    checkNotNull(dictionary);
    checkArgument(!dictionary.isEmpty());
    this.dictionary = dictionary;
  }

  public Optional<List<String>> getCorrections(String word) {

    String normalizedWord = normalize(word);

    if (dictionary.containsKey(normalizedWord)) {
      return Optional.empty();
    }

    final Optional<List<String>> corrections;

    List<String> corrections1 = edits1(normalizedWord);

    if (corrections1.isEmpty()) {
      List<String> corrections2 = edits2(normalizedWord);
      if (corrections2.isEmpty()) {
        corrections = Optional.of(emptyList());
      } else {
        corrections = Optional.of(corrections2);
      }
    } else {
      corrections = Optional.of(corrections1);
    }

    return corrections;
  }

  List<String> edits1(String word) {

    List<Split> wordSplits = splits(word);

    return pack(edits.stream()
        .flatMap(edit -> edit.apply(wordSplits).stream()));
  }

  List<String> edits2(String word) {
    return
        pack(edits1(word).stream()
            .flatMap(w -> edits1(w).stream()));
  }

  List<String> pack(Stream<String> stream) {
    return stream
        .distinct()
        .filter(dictionary::containsKey)
        .sorted(Comparator.comparing(dictionary::get))
        .collect(Collectors.toList());
  }

  static List<Split> splits(String word) {
    return IntStream
        .rangeClosed(0, word.length()).boxed()
        .map(i -> {
          String left = word.substring(0, i);
          String right = word.substring(i);
          return new Split(left, right);
        })
        .collect(Collectors.toList());
  }

  static List<String> deletes(List<Split> splits) {
    return splits.stream()
        .filter(split -> !split.right.isEmpty())
        .map(split -> split.left + split.right.substring(1))
        .collect(Collectors.toList());
  }

  static List<String> transposes(List<Split> splits) {
    return splits.stream()
        .filter(split -> split.right.length() > 1)
        .map(split -> split.left + split.right.substring(1, 2) + split.right.substring(0, 1) + split.right.substring(2))
        .collect(Collectors.toList());
  }

  static List<String> replaces(List<Split> splits) {
    return splits.stream()
        .filter(split -> !split.right.isEmpty())
        .flatMap(split ->
            Arrays.stream(LETTERS).map(letter ->
                split.left + letter + split.right.substring(1)
            )
        )
        .collect(Collectors.toList());
  }

  static List<String> inserts(List<Split> splits) {
    return splits.stream()
        .flatMap(split ->
            Arrays.stream(LETTERS).map(letter ->
                split.left + letter + split.right
            )
        )
        .collect(Collectors.toList());
  }

  static String normalize(String word) {
    checkNotNull(word);
    String normalizedWord = word.trim().toLowerCase();
    checkArgument(isAlphabetic(normalizedWord));
    return normalizedWord;
  }

  static boolean isAlphabetic(String word) {
    checkNotNull(word);
    return ALPHABETIC.matcher(word).matches();
  }

  static class Split {
    final String left;
    final String right;

    public Split(String left, String right) {
      checkNotNull(left);
      checkNotNull(right);
      this.left = left;
      this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof Split)) {
        return false;
      }
      Split that = (Split) obj;
      return this.left.equals(that.left) && this.right.equals(that.right);
    }
  }
}
