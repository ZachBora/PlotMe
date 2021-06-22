package com.worldcretornica.plotme;

import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.ChatColor;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class PlotRunnableDeleteExpire implements Runnable {
    public void run() {
        if (PlotMe.worldcurrentlyprocessingexpired != null) {
            final World w = PlotMe.worldcurrentlyprocessingexpired;
            final List<Plot> expiredplots = new ArrayList<Plot>();
            HashMap<String, Plot> plots = PlotManager.getPlots(w);
            final String date = PlotMe.getDate();
            for (final String id : plots.keySet()) {
                final Plot plot = (Plot)plots.get(id);
                if (!plot.protect && !plot.finished && plot.expireddate != null && PlotMe.getDate(plot.expireddate).compareTo(date.toString()) < 0) {
                    expiredplots.add(plot);
                }
                if (expiredplots.size() == PlotMe.nbperdeletionprocessingexpired) {
                    break;
                }
            }
            if (expiredplots.size() == 0) {
                PlotMe.counterexpired = 0;
            }
            else {
                plots = null;
                Collections.sort(expiredplots);
                String ids = "";
                for (int ictr = 0; ictr < PlotMe.nbperdeletionprocessingexpired && expiredplots.size() > 0; ++ictr) {
                    final Plot expiredplot = (Plot)expiredplots.get(0);
                    expiredplots.remove(0);
                    PlotManager.clear(w, expiredplot);
                    final String id2 = expiredplot.id;
                    ids = ids + ChatColor.RED + id2 + ChatColor.RESET + ", ";
                    PlotManager.getPlots(w).remove(id2);
                    PlotManager.removeOwnerSign(w, id2);
                    PlotManager.removeSellSign(w, id2);
                    SqlManager.deletePlot(PlotManager.getIdX(id2), PlotManager.getIdZ(id2), w.getName().toLowerCase());
                }
                if (ids.substring(ids.length() - 2).equals(", ")) {
                    ids = ids.substring(0, ids.length() - 2);
                }
                PlotMe.cscurrentlyprocessingexpired.sendMessage("" + PlotMe.PREFIX + PlotMe.caption("MsgDeletedExpiredPlots") + " " + ids);
            }
            if (PlotMe.counterexpired == 0) {
                PlotMe.cscurrentlyprocessingexpired.sendMessage("" + PlotMe.PREFIX + PlotMe.caption("MsgDeleteSessionFinished"));
                PlotMe.worldcurrentlyprocessingexpired = null;
                PlotMe.cscurrentlyprocessingexpired = null;
            }
        }
    }
}
