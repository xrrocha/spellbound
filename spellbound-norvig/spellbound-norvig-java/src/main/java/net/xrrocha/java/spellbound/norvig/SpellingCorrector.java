package net.xrrocha.java.spellbound.norvig;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class SpellingCorrector {

  private final Map<String, Integer> dictionary;

  public SpellingCorrector(Map<String, Integer> dictionary) {
    checkNotNull(dictionary);
    checkArgument(!dictionary.isEmpty());
    this.dictionary = dictionary;
  }

  static final Pattern ALPHABETIC = Pattern.compile("^[\\p{Alpha}]+$");

  public Optional<List<String>> getCorrections(String word) {
    String normalizedWord = normalize(word);

    if (dictionary.containsKey(normalizedWord)) {
      return Optional.empty();
    }

    throw new UnsupportedOperationException("Unimplemented: getCorrections()");
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
}
