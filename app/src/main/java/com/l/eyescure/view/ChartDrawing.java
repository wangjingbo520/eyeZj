package com.l.eyescure.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Arrays;

@SuppressLint("NewApi")
/**
 * 在android中画折线图、柱状图、饼图等统计图，可以用achartengine这个工具，可通过下载achartengine.jar
 * 主要通过设置几个对象
 * 1、XYSeries对象：用于存储一条线的数据信息；
 * 2、XYMultipleSeriesDataset对象：即数据集，可添加多个XYSeries对象，因为一个折线图中可能有多条线。
 * 3、XYSeriesRenderer对象：主要是用来设置一条线条的风格，颜色啊，粗细之类的。
 * 4、XYMultipleSeriesRenderer对象：主要用来定义一个图的整体风格，设置xTitle,yTitle,chartName等等整体性的风格，
 *    可添加多个XYSeriesRenderer对象，因为一个图中可以有多条折线。
 * 设置完那些对象之后，可通过 org.achartengine.ChartFactory调用数据集XYMultipleSeriesDataset对象
 * 与XYMultipleSeriesRenderer对象来画图并将图加载到GraphicalView中，
 * ChartFactory有多种api，通过这些api调用来决定是画折线图还是柱状图。
 * */
public class ChartDrawing {

    private String xTitle, yTitle, chartTitle;
    private String xLabel[];
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer multiRenderer;

    public XYMultipleSeriesRenderer getMultiRenderer() {
        return multiRenderer;
    }

    public XYMultipleSeriesDataset getDataset() {
        return dataset;
    }

    public ChartDrawing(String xTitle, String yTitle, String chartTitle,
                        String xLabel[]) {
        this.xTitle = xTitle;
        this.yTitle = yTitle;
        this.xLabel = Arrays.copyOf(xLabel, xLabel.length);
        this.chartTitle = chartTitle;
        this.multiRenderer = new XYMultipleSeriesRenderer();
        this.dataset = new XYMultipleSeriesDataset();
    }

    /**
     * 给XYSeries对象复制。并将其加到数据集 XYMultipleSeriesDataset对象中去
     * */
    public void set_XYSeries(int value[], String lineName) {
        XYSeries oneSeries = new XYSeries(lineName);
        for (int i = 0; i < value.length; i++) {
            oneSeries.add(i + 1, value[i]);
        }
        this.dataset.addSeries(oneSeries);
    }

    // public XYSeriesRenderer set_XYSeriesRender_Style(int color) {
    // XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
    // seriesRenderer.setColor(color);
    // seriesRenderer.setFillPoints(true);
    // seriesRenderer.setLineWidth(2);
    // seriesRenderer.setDisplayChartValues(false);
    //
    // return seriesRenderer;
    // }

    public void addXYSeriesRenderer(int[] color) {
        for (int i = 0; i < color.length; i++) {
            XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
            seriesRenderer.setColor(color[i]);
            seriesRenderer.setFillPoints(true);
            seriesRenderer.setLineWidth(2);
            seriesRenderer.setDisplayChartValues(false);
            this.multiRenderer.addSeriesRenderer(seriesRenderer);
        }
    }

    public void initXYMultipleSeriesRenderer(double xMAX, double yMax) {
        // 设置 X 轴不显示数字(改用我们手动添加的文字标签)
        this.multiRenderer.setXLabels(0);
        // 设置Y轴的结点数
        this.multiRenderer.setYLabels(8);
        // 设置X轴的代表的名称
        this.multiRenderer.setXTitle(xTitle);
        // 设置Y轴的代表的名称
        this.multiRenderer.setYTitle(yTitle);
        // 是否显网格
        this.multiRenderer.setShowGrid(true);
        // 是否显示坐标
        this.multiRenderer.setShowLabels(true);
        // 是否显示下面描述
        this.multiRenderer.setShowLegend(false);
        // 是否可缩放
        this.multiRenderer.setZoomEnabled(false, false);
        // 是否可拖动
        this.multiRenderer.setPanEnabled(true, false);
        this.multiRenderer.setXLabelsColor(Color.WHITE);
        this.multiRenderer.setYLabelsColor(0, Color.WHITE);
        this.multiRenderer.setXLabelsAlign(Paint.Align.RIGHT);
        this.multiRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        this.multiRenderer.setLabelsTextSize(15);
        this.multiRenderer.setAxisTitleTextSize(18);
        this.multiRenderer.setApplyBackgroundColor(true);
        //this.multiRenderer.setMarginsColor(Color.WHITE);
        this.multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        this.multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        this.multiRenderer.setRange(new double[] { 0 + 0.75, xMAX, 0, yMax });
        // this.multiRenderer.setPanLimits(new double[] {0, 10, 0, 10 });
        // this.multiRenderer.setMargins(new int[] { 50, 50, 50, 10 });
        for (int i = 0; i < xLabel.length; i++) {
            this.multiRenderer.addXTextLabel(i + 1, this.xLabel[i]);
        }
    }

    public void setPanEnabled(boolean x, boolean y) {
        this.multiRenderer.setPanEnabled(x, y);
    }
}