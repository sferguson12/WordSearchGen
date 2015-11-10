package com.fergusware.wsg;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stf
 */
public class Grid {
  private static final Logger LOGGER = Logger.getLogger(Grid.class.toString());
  private static final Random RANDOM = new Random();
  private static final char NULLC = '\u0000';
  // Derived from https://en.wikipedia.org/wiki/Letter_frequency
  private static final char LETTER_FREQ[] = {
    'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
    'b', 'b',
    'c', 'c', 'c',
    'd', 'd', 'd', 'd',
    'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e',
    'f', 'f',
    'g', 'g',
    'h', 'h', 'h', 'h', 'h', 'h',
    'i', 'i', 'i', 'i', 'i', 'i', 'i',
    'j',
    'k',
    'l', 'l', 'l', 'l',
    'm', 'm',
    'n', 'n', 'n', 'n', 'n', 'n', 'n',
    'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
    'p', 'p',
    'q',
    'r', 'r', 'r', 'r', 'r', 'r',
    's', 's', 's', 's', 's', 's',
    't', 't', 't', 't', 't', 't', 't', 't', 't',
    'u', 'u', 'u',
    'v',
    'w', 'w',
    'x',
    'y', 'y',
    'z'
  };

  private List<String> words;
  private final char[][] matrix;
  private final int size;

  public Grid(int size) {
    this.size = size;
    this.matrix = new char[size][size];
    words = new ArrayList<>();
  }

  public boolean addWord(String word) {
    boolean placed = false;

    // Skip words we cannot fit
    if (word.length() > size) return false;

    word = word.toLowerCase();

    // Pick a random starting point in the matrix to start testing
    Vector origin = new Vector(size, word.length());
    Vector current = new Vector(origin);

    while (!placed) {
      LOGGER.log(Level.FINE, "{0}\t[{1}]", new Object[]{current, origin});

      if (current.isInGrid() && doesWordFit(word, current)) {
        placed = true;
      } else {
        current.next();
        if (current.equals(origin)) break;
      }
    }

    if (placed)
      storeWord(word, current);

    return placed;
  }

  private void storeWord(String word, Vector current) {
    int x = current.x, y = current.y;

    words.add(word);
    for (char c : word.toCharArray()) {
      matrix[x][y] = c;
      x = x + current.dir.getScaleX();
      y = y + current.dir.getScaleY();
    }
  }

  private boolean doesWordFit(String word, Vector current) {
    boolean result = true;
    int x = current.x, y = current.y;

    for (char c : word.toCharArray()) {
      if (NULLC == matrix[x][y] || c == matrix[x][y]) {
        x = x + current.dir.getScaleX();
        y = y + current.dir.getScaleY();
        continue;
      }

      result = false;
      break;
    }

    return result;
  }

  // Fill with letters randomly chosen, based on English letter frequency
  public void fill() {
    for (int y = 0; y < size; y++)
      for (int x = 0; x < size; x++)
        if (NULLC == matrix[x][y])
          matrix[x][y] = LETTER_FREQ[RANDOM.nextInt(LETTER_FREQ.length)];
  }

  public void print(PrintStream out) {
    for (int y = size - 1; y >= 0; y--) {
      for (int x = 0; x < size; x++) {
        out.print(matrix[x][y] + " ");
      }
      out.print("\n\n");
    }

    out.println("\nWords:\n");
    words.stream().forEach((word) -> {
      out.println(word);
    });

    out.println("\n");
  }

  private class Vector {
    private final int max; // Not technically a property of the vector, but useful to know
    private final int size;
    private final Direction dirInit;

    public int x;
    public int y;
    private Direction dir;

    public Vector(int max, int size) {
      this.max = max;
      this.size = size;
      this.x = RANDOM.nextInt(max);
      this.y = RANDOM.nextInt(max);
      this.dir = Direction.random();
      this.dirInit = dir;
    }

    public Vector(Vector v) {
      this.max = v.max;
      this.size = v.size;
      this.x = v.x;
      this.y = v.y;
      this.dir = v.dir;
      this.dirInit = v.dirInit;
    }

    public void next() {
      dir = dir.next();
      if (dir != dirInit) return;

      x++;
      if (x < max) return;

      x = 0;
      y++;
      if (y < max) return;

      y = 0;
    }

    public boolean isInGrid() {
      boolean result = true;
      int endX = x + dir.getScaleX() * size;
      int endY = y + dir.getScaleY() * size;

      if (endX < 0 || endX > max) result = false;
      else if (endY < 0 || endY > max) result = false;

      return result;
    }

    public boolean equals(Vector v) {
      return (v.x == x && v.y == y && v.dir == dir);
    }

    @Override
    public String toString() {
      return x + ", " + y + " @ " + dir;
    }
  }

  private enum Direction {
    N, NE, E, SE, S, SW, W, NW;

    public static final int size = Direction.values().length;

    public static Direction random() {
      return Direction.values()[RANDOM.nextInt(8)];
    }

    public Direction next() {
      return Direction.values()[(ordinal() + 1) % 8];
    }

    public int getScaleX() {
      if (N == this || S == this) return 0;
      else if (NE == this || E == this || SE == this) return 1;
      else return -1;
    }

    public int getScaleY() {
      if (W == this || E == this) return 0;
      else if (NW == this || N == this || NE == this) return 1;
      else return -1;
    }
  }
}
