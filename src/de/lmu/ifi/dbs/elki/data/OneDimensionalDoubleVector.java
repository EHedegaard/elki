package de.lmu.ifi.dbs.elki.data;

import java.util.List;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;

/**
 * Specialized class implementing a one-dimensional double vector without using
 * an array. Saves a little bit of memory, albeit we cannot avoid boxing as long as
 * we want to implement the interface.
 * 
 * @author Erich Schubert
 */
public class OneDimensionalDoubleVector extends AbstractNumberVector<OneDimensionalDoubleVector, Double> {
  /**
   * The actual data value
   */
  double val;

  /**
   * Constructor.
   * 
   * @param val Value
   */
  public OneDimensionalDoubleVector(double val) {
    this.val = val;
  }

  @Override
  public int getDimensionality() {
    return 1;
  }

  @Override
  public double doubleValue(int dimension) {
    assert (dimension == 1) : "Non-existant dimension accessed.";
    return val;
  }

  @Override
  public long longValue(int dimension) {
    assert (dimension == 1) : "Non-existant dimension accessed.";
    return (long) val;
  }

  @Override
  public Double getValue(int dimension) {
    assert (dimension == 1) : "Incorrect dimension requested for 1-dimensional vector.";
    return this.val;
  }

  @Override
  public Vector getColumnVector() {
    return new Vector(new double[] { val });
  }

  @Override
  public Matrix getRowVector() {
    return new Matrix(new double[][] { { val } });
  }

  @Override
  public OneDimensionalDoubleVector nullVector() {
    return new OneDimensionalDoubleVector(0.0);
  }

  @Override
  public OneDimensionalDoubleVector negativeVector() {
    return new OneDimensionalDoubleVector(-this.val);
  }

  @Override
  public OneDimensionalDoubleVector plus(OneDimensionalDoubleVector fv) {
    return new OneDimensionalDoubleVector(this.val + fv.val);
  }

  @Override
  public OneDimensionalDoubleVector minus(OneDimensionalDoubleVector fv) {
    return new OneDimensionalDoubleVector(this.val - fv.val);
  }

  @Override
  public Double scalarProduct(OneDimensionalDoubleVector fv) {
    return this.val * fv.val;
  }

  @Override
  public OneDimensionalDoubleVector multiplicate(double k) {
    return new OneDimensionalDoubleVector(this.val * k);
  }

  @Override
  public OneDimensionalDoubleVector newInstance(double[] values) {
    assert (values.length == 1) : "Incorrect dimensionality for 1-dimensional vector.";
    return new OneDimensionalDoubleVector(values[0]);
  }

  @Override
  public OneDimensionalDoubleVector newInstance(Vector values) {
    assert (values.getDimensionality() == 1) : "Incorrect dimensionality for 1-dimensional vector.";
    return new OneDimensionalDoubleVector(values.get(0));
  }

  @Override
  public OneDimensionalDoubleVector newInstance(Double[] values) {
    assert (values != null) : "newInstace(null) is not allowed.";
    assert (values.length == 1) : "Incorrect dimensionality for 1-dimensional vector.";
    return new OneDimensionalDoubleVector(values[0]);
  }

  @Override
  public OneDimensionalDoubleVector newInstance(List<Double> values) {
    assert (values != null) : "newInstace(null) is not allowed.";
    assert (values.size() == 1) : "Incorrect dimensionality for 1-dimensional vector.";
    return new OneDimensionalDoubleVector(values.get(0));
  }
}