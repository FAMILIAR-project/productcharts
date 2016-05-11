package org.opencompare;

import org.opencompare.api.java.Cell;
import org.opencompare.api.java.Value;
import org.opencompare.api.java.value.IntegerValue;
import org.opencompare.api.java.value.RealValue;

import java.util.Comparator;


/**
 *  // TODO: incomplete and unsound
 * Created by macher1 on 11/05/2016.
 */
public class CellValueComparator implements Comparator<Cell> {


    @Override
    public int compare(Cell o1, Cell o2) {
        Value i1 = o1.getInterpretation();
        Value i2 = o2.getInterpretation();

        if (i1 instanceof IntegerValue && i2 instanceof IntegerValue) {
            int v1 = ((IntegerValue) i1).getValue();
            int v2 = ((IntegerValue) i2).getValue();
            return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
        }
        else if (i1 instanceof RealValue && i2 instanceof RealValue) {
            double v1 = ((RealValue) i1).getValue();
            double v2 = ((RealValue) i2).getValue();
            return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
        }
        else
            return o1.getContent().compareTo(o2.getContent());
    }

}
