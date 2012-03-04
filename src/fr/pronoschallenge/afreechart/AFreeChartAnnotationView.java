/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------
 * AnnotationDemo01View.java
 * -----------------
 * (C) Copyright 2011, by ICOMSYSTECH Co.,Ltd.
 *
 * Original Author:  Yamakami Souichirou (for ICOMSYSTECH Co.,Ltd);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Oct-2011 : Added new sample code (SY);
 */

package fr.pronoschallenge.afreechart;

import java.util.List;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.XYPlot;
import org.afree.data.xy.XYDataset;
import org.afree.data.xy.XYSeries;
import org.afree.data.xy.XYSeriesCollection;
import org.afree.graphics.SolidColor;
import org.afree.ui.RectangleInsets;

import android.graphics.Color;

/**
 * AFreeChartAnnotationView
 */
public class AFreeChartAnnotationView extends AFreeChartView {

	static List<EvolutionClassementEntry> evolutionEntries;
	private static int mode;

	static final int MODE_PROFIL = 0;
	static final int MODE_CLUB = 1;	
	
    /**
     * constructor
     * @param context
     */
    public AFreeChartAnnotationView(AFreeChartAnnotationActivity activity, List<EvolutionClassementEntry> evolutionEntries, int mode) {
        super(activity);
        this.setMode(mode);
        AFreeChartAnnotationView.evolutionEntries = evolutionEntries;
        final AFreeChart chart = createChart(activity.getItemName());

        setChart(chart);
    }


	/**
     * Creates a dataset.
     * @return a dataset.
     */
    private static XYSeriesCollection createDataset() {

    	XYSeriesCollection xySC = new XYSeriesCollection();

    	for (EvolutionClassementEntry evolutionEntry : evolutionEntries) {
    		XYSeries xySerie = new XYSeries(evolutionEntry.getTypeChamp(), true, false);
    		for (EvolutionClassementDetailEntry detailEntry : evolutionEntry.getEvolutionDetail()) {
        		xySerie.add(detailEntry.getNumJournee(), detailEntry.getNumPlace());    			
    		}
    		xySC.addSeries(xySerie);
    	}

    	return xySC;
    }

    /**
     * Creates a sample chart.
     * @param dataset the dataset.
     * @return A sample chart.
     */
    private static AFreeChart createChart(String profilPseudo) {
        XYDataset dataset = createDataset();
        
        AFreeChart chart = ChartFactory.createXYLineChart(
        		profilPseudo, 			   // chart title
                "Journée",                 // domain axis label
                "Place",                   // range axis label
                dataset,                   // data
                PlotOrientation.VERTICAL,  // orientation
                true,                      // include legend
                true,                      // tooltips?
                false);                    // URLs?
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
/*        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0, 38);*/
        
        ValueAxis rangeAxis = plot.getRangeAxis();
        if (getMode() == MODE_PROFIL) {
        	rangeAxis.setRange(0, 150);
        } else {
        	rangeAxis.setRange(0, 20);
        }
        rangeAxis.setInverted(true);

        plot.setBackgroundPaintType(new SolidColor(Color.LTGRAY));
        plot.setDomainGridlinePaintType(new SolidColor(Color.WHITE));
        plot.setRangeGridlinePaintType(new SolidColor(Color.WHITE));
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

/*        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }        
*/
        return chart;
    }

	public static int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
   
}
