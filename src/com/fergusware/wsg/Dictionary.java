package com.fergusware.wsg;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stf
 */
public class Dictionary {
  private static final Logger LOGGER = Logger.getLogger(Dictionary.class.toString());
  private static final Random RANDOM = new Random();
  private static final Charset CHARSET = Charset.forName("ISO-8859-1");

  private final Path path;
  private final int minSize;
  private final int maxSize;
  private final List<String> words;

  public Dictionary(String file, int minSize, int maxSize) {
    this.path = Paths.get(file);
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.words = new ArrayList<>();
  }

  public void init() {
    try {
      Files.readAllLines(path, CHARSET)
        .stream()
        .filter((word) -> (word.length() >= minSize && word.length() <= maxSize))
        .filter((word) -> (word.matches("^[a-z]+$")))
        .forEach((word) -> { words.add(word); });
    } catch (IOException ioe) {
      LOGGER.log(Level.SEVERE, "Failed to load word list", ioe);
    }
  }

  public Set<String> getWordSet(int qty) {
    Set<String> wordSet = new HashSet<>();

    while (wordSet.size() < qty) {
      int index = RANDOM.nextInt(words.size());
      String test = words.get(index);
      if (test.length() >= minSize && test.length() <= maxSize)
        wordSet.add(test);
    }

    return wordSet;
  }
}
