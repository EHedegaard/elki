package de.lmu.ifi.dbs.elki.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.lmu.ifi.dbs.elki.data.ExternalObject;
import de.lmu.ifi.dbs.elki.distance.NumberDistance;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AttributeSettings;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Provides a parser for parsing one distance value per line.
 * <p/>
 * A line must have the following format: id1 id2 distanceValue, where id1 and
 * id2 are integers representing the two ids belonging to the distance value.
 * Lines starting with &quot;#&quot; will be ignored.
 * 
 * @author Elke Achtert
 * @param <D> distance type
 * @param <N> number type
 */
public class NumberDistanceParser<D extends NumberDistance<D, N>, N extends Number> extends AbstractParser<ExternalObject> implements DistanceParser<ExternalObject, D> {

  /**
   * OptionID for {@link #DISTANCE_FUNCTION_PARAM}
   */
  public static final OptionID DISTANCE_FUNCTION_ID = OptionID.getOrCreateOptionID("parser.distancefunction", "Distance function.");

  /**
   * Parameter for distance function.
   */
  ClassParameter<DistanceFunction<ExternalObject, D>> DISTANCE_FUNCTION_PARAM = new ClassParameter<DistanceFunction<ExternalObject, D>>(DISTANCE_FUNCTION_ID, DistanceFunction.class);

  /**
   * The distance function.
   */
  private DistanceFunction<ExternalObject, D> distanceFunction;

  /**
   * Provides a parser for parsing one double distance per line. A line must
   * have the following format: id1 id2 distanceValue, where id1 and id2 are
   * integers representing the two ids belonging to the distance value, the
   * distance value is a double value. Lines starting with &quot;#&quot; will be
   * ignored.
   */
  public NumberDistanceParser() {
    super();
    addOption(DISTANCE_FUNCTION_PARAM);
  }

  public DistanceParsingResult<ExternalObject, D> parse(InputStream in) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    int lineNumber = 0;
    List<Pair<ExternalObject, List<String>>> objectAndLabelsList = new ArrayList<Pair<ExternalObject, List<String>>>();

    Set<Integer> ids = new HashSet<Integer>();
    Map<Pair<Integer, Integer>, D> distanceCache = new HashMap<Pair<Integer, Integer>, D>();
    try {
      for(String line; (line = reader.readLine()) != null; lineNumber++) {
        if(lineNumber % 10000 == 0 && logger.isDebugging()) {
          logger.debugFine("parse " + lineNumber / 10000);
          // logger.fine("parse " + lineNumber / 10000);
        }
        if(!line.startsWith(COMMENT) && line.length() > 0) {
          String[] entries = WHITESPACE_PATTERN.split(line);
          if(entries.length != 3)
            throw new IllegalArgumentException("Line " + lineNumber + " does not have the " + "required input format: id1 id2 distanceValue! " + line);

          Integer id1, id2;
          try {
            id1 = Integer.parseInt(entries[0]);
          }
          catch(NumberFormatException e) {
            throw new IllegalArgumentException("Error in line " + lineNumber + ": id1 is no integer!");
          }

          try {
            id2 = Integer.parseInt(entries[1]);
          }
          catch(NumberFormatException e) {
            throw new IllegalArgumentException("Error in line " + lineNumber + ": id2 is no integer!");
          }

          try {
            D distance = distanceFunction.valueOf(entries[2]);
            put(id1, id2, distance, distanceCache);
            ids.add(id1);
            ids.add(id2);
          }
          catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Error in line " + lineNumber + ":" + e.getMessage());
          }
        }
      }
    }
    catch(IOException e) {
      throw new IllegalArgumentException("Error while parsing line " + lineNumber + ".");
    }

    if(logger.isDebugging()) {
      logger.debugFine("check");
    }

    // check if all distance values are specified
    for(Integer id1 : ids) {
      for(Integer id2 : ids) {
        if(id2 < id1)
          continue;
        if(!containsKey(id1, id2, distanceCache))
          throw new IllegalArgumentException("Distance value for " + id1 + " - " + id2 + " is missing!");
      }
    }

    if(logger.isDebugging()) {
      logger.debugFine("add to objectAndLabelsList");
    }
    for(Integer id : ids) {
      objectAndLabelsList.add(new Pair<ExternalObject, List<String>>(new ExternalObject(id), new ArrayList<String>()));
    }

    return new DistanceParsingResult<ExternalObject, D>(objectAndLabelsList, distanceCache);
  }

  /**
   * Returns the distance function of this parser.
   * 
   * @return the distance function of this parser
   */
  public DistanceFunction<ExternalObject, D> getDistanceFunction() {
    return distanceFunction;
  }

  @Override
  public String parameterDescription() {
    StringBuffer description = new StringBuffer();
    description.append(NumberDistanceParser.class.getName());
    description.append(" expects following format of parsed lines:\n");
    description.append("id1 id2 distanceValue, where id1 and is2 are integers representing " + "the two ids belonging to the distance value.\n" + " The ids and the distance value are separated by whitespace (");
    description.append(WHITESPACE_PATTERN.pattern());
    description.append("). Empty lines and lines beginning with \"");
    description.append(COMMENT);
    description.append("\" will be ignored.\n");

    return usage(description.toString());
  }

  @Override
  public String[] setParameters(String[] args) throws ParameterException {
    String[] remainingParameters = super.setParameters(args);

    distanceFunction = DISTANCE_FUNCTION_PARAM.instantiateClass();
    remainingParameters = distanceFunction.setParameters(remainingParameters);
    rememberParametersExcept(args, remainingParameters);

    return remainingParameters;
  }

  /**
   * Calls the super method and adds to the returned attribute settings the
   * attribute settings of the {@link #distanceFunction}.
   */
  @Override
  public List<AttributeSettings> getAttributeSettings() {
    List<AttributeSettings> attributeSettings = super.getAttributeSettings();
    attributeSettings.addAll(distanceFunction.getAttributeSettings());
    return attributeSettings;
  }

  /**
   * Puts the specified distance value for the given ids to the distance cache.
   * 
   * @param id1 the first id
   * @param id2 the second id
   * @param distance the distance value
   * @param cache the distance cache
   */
  private void put(Integer id1, Integer id2, D distance, Map<Pair<Integer, Integer>, D> cache) {
    // the smaller id is the first key
    if(id1 > id2) {
      put(id2, id1, distance, cache);
      return;
    }

    D oldDistance = cache.put(new Pair<Integer, Integer>(id1, id2), distance);

    if(oldDistance != null) {
      throw new IllegalArgumentException("Distance value for specified ids is already assigned!");
    }
  }

  /**
   * Returns <tt>true</tt> if the specified distance cache contains a distance
   * value for the specified ids.
   * 
   * @param id1 the first id
   * @param id2 the second id
   * @param cache the distance cache
   * @return <tt>true</tt> if this cache contains a distance value for the
   *         specified ids, false otherwise
   */
  public boolean containsKey(Integer id1, Integer id2, Map<Pair<Integer, Integer>, D> cache) {
    if(id1 > id2) {
      return containsKey(id2, id1, cache);
    }

    return cache.containsKey(new Pair<Integer, Integer>(id1, id2));
  }

}
