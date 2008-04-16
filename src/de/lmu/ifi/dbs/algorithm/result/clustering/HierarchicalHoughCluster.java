package de.lmu.ifi.dbs.algorithm.result.clustering;

import de.lmu.ifi.dbs.algorithm.clustering.cash.HoughInterval;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a hierarchical correlation in an arbitrary subspace
 * which is determined by an hough based algorithm
 * that holds the interval of angles, the ids of the objects
 * belonging to this cluster and the children and parents of this cluster.
 *
 * @author Elke Achtert
 */
public class HierarchicalHoughCluster<ParameterizationFunction> extends HierarchicalCluster<HierarchicalHoughCluster<ParameterizationFunction>> {
  /**
   * The interval of this cluster.
   */
  private final HoughInterval interval;

  /**
   * The correlation dimensionality of this cluster.
   */
  private final int corrdim;

  /**
   * Provides a new hierarchical correlation cluster with the
   * specified parameters.
   *
   * @param interval   the interval of this cluster
   * @param corrDim    the correlation dimensionality of this cluster
   * @param ids        the ids of the objects belonging to this cluster
   * @param label      the label of this cluster
   * @param level      the level of this cluster in the graph
   * @param levelIndex the index of this cluster within the level
   */
  public HierarchicalHoughCluster(HoughInterval interval,
                                  int corrDim,
                                  Set<Integer> ids,
                                  String label, int level, int levelIndex) {
    this(interval, corrDim, ids,
        new ArrayList<HierarchicalHoughCluster<ParameterizationFunction>>(),
        new ArrayList<HierarchicalHoughCluster<ParameterizationFunction>>(),
        label, level, levelIndex);
  }

  /**
   * Provides a hierarchical correlation cluster in an arbitrary subspace
   * that holds the basis vectors of this cluster, the similarity matrix for
   * distance computations, the ids of the objects
   * belonging to this cluster and the children and parents of this cluster.
   *
   * @param interval   the interval of this cluster
   * @param corrDim    the correlation dimensionality of this cluster
   * @param ids        the ids of the objects belonging to this cluster
   * @param children   the list of children of this cluster
   * @param parents    the list of parents of this cluster
   * @param label      the label of this cluster
   * @param level      the level of this cluster in the graph
   * @param levelIndex the index of this cluster within the level
   */
  public HierarchicalHoughCluster(HoughInterval interval,
                                  int corrDim,
                                  Set<Integer> ids,
                                  List<HierarchicalHoughCluster<ParameterizationFunction>> children,
                                  List<HierarchicalHoughCluster<ParameterizationFunction>> parents,
                                  String label, int level, int levelIndex) {
    super(ids, children, parents, label, level, levelIndex);
    this.interval = interval;
    this.corrdim = corrDim;
  }

  /**
   * Returns the interval of this cluster.
   *
   * @return the interval of this cluster
   */
  public HoughInterval getInterval() {
    return interval;
  }

  /**
   * Returns the correlation dimensionality  of this cluster.
   *
   * @return the correlation dimensionality of this cluster
   */
  public int getCorrdim() {
    return corrdim;
  }

  /**
   * Returns a hash code value for this cluster.
   *
   * @return a hash code value for this cluster
   */
  @Override
  public int hashCode() {
    return interval.hashCode();
  }
}
