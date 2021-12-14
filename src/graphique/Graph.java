package graphique;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

public class Graph {
	/**
	 * Permet de créer un graphe à partir d'une liste donnée en entrée
	 * Utilisé pour faire le graphe du temps d'exécution en fonction de l'itération
	 */
	
	private long[] list;
	public double[] listdbl;
	private double[] xlist; 

	public Graph(long[] list) {
		this.list = list;
		longtodouble();
	}

	private void longtodouble() {
		listdbl = new double[list.length];
		xlist = new double[list.length];
		for (int i = 0; i < list.length; i++) {
			listdbl[i] = (double)list[i]/1000000;

			xlist[i] = i+1;
		}
	}

	public XYChart chart(String title) {

		XYChart chart = new XYChartBuilder().width(800).height(600).build();
	    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
	    chart.getStyler().setChartTitleVisible(false);
	    chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
	    chart.getStyler().setMarkerSize(8);
	    chart.getStyler().setZoomEnabled(true);
	    chart.addSeries(title, xlist, listdbl);

	    return chart;



	}
}
