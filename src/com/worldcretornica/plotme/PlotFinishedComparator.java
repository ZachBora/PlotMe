package com.worldcretornica.plotme;

import java.util.Comparator;

public class PlotFinishedComparator implements Comparator<Plot> {
    public int compare(final Plot plot1, final Plot plot2) {
        if (plot1.finisheddate.compareTo(plot2.finisheddate) == 0) {
            return plot1.owner.compareTo(plot2.owner);
        }
        return plot1.finisheddate.compareTo(plot2.finisheddate);
    }
}
