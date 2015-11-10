package com.fergusware.wsg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stf
 */
public class WordSearchGen {
  private static final Logger LOGGER = Logger.getLogger(WordSearchGen.class.toString());
  private static final String PROP_FILE = "config.properties";

  private Properties getProperties() throws IOException {
      Properties prop = null;
      InputStream inputStream = null;

      try {
          prop = new Properties();
          inputStream = new FileInputStream(PROP_FILE);
          prop.load(inputStream);
      } catch (Exception ex) {
          LOGGER.log(Level.SEVERE, "Failed to load properties file", ex);
      } finally {
          if (null != inputStream) inputStream.close();
      }

      return prop;
  }

  private void buildPuzzle() throws IOException {
    Properties prop = getProperties();

    int minWordLength = Integer.parseInt(prop.getProperty("min_word_length"));
    int maxWordLength = Integer.parseInt(prop.getProperty("max_word_length"));

    String dictFileName = prop.getProperty("dictionary");
    ClassLoader classLoader = getClass().getClassLoader();
    String dictFileRes = classLoader.getResource(dictFileName).getFile();
    String dictFilePath = new File(dictFileRes).getAbsolutePath();

    // Get Dictionary
    Dictionary myDict = new Dictionary(dictFilePath, minWordLength, maxWordLength);
    myDict.init();

    int gridSize = Integer.parseInt(prop.getProperty("grid_size"));
    int numWords = Integer.parseInt(prop.getProperty("num_words"));

    Grid myGrid = new Grid(gridSize);
    myDict.getWordSet(numWords).stream().forEach((word) -> {
      myGrid.addWord(word);
    });

    PrintStream out = System.out;
    out.println("Key:\n");
    myGrid.print(out);

    myGrid.fill();

    out.println("Puzzle:\n");
    myGrid.print(out);
  }

  /**
   * @param args the command line arguments
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    WordSearchGen wsg = new WordSearchGen();
    wsg.buildPuzzle();
  }
}
