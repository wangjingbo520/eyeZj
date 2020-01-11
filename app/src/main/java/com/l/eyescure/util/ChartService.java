package com.l.eyescure.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.l.eyescure.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

/**
 * Created by lenovo on 2017/4/24.
 */
public class ChartService {
    private GraphicalView mGraphicalView;
    private XYMultipleSeriesDataset multipleSeriesDataset;// 数据集容器
    private XYMultipleSeriesRenderer multipleSeriesRenderer;// 渲染器容器
    private XYSeries mSeries;// 单条曲线数据集
    private XYSeriesRenderer mRenderer;// 单条曲线渲染器
    private Context context;
    private boolean isCustomX = true;//是否自定义x轴table
    private String[] xLabel = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

    public ChartService(Context context) {
        this.context = context;
    }

    /**
     * 获取图表
     *
     * @return
     */
    public GraphicalView getGraphicalView() {
        mGraphicalView = ChartFactory.getCubeLineChartView(context,
                multipleSeriesDataset, multipleSeriesRenderer, 0.1f);
        return mGraphicalView;
    }

    public void setCustomX(boolean isCustomX) {
        this.isCustomX = isCustomX;
    }

    /**
     * 获取数据集，及xy坐标的集合
     *
     * @param curveTitle
     */
    public void setXYMultipleSeriesDataset(String curveTitle) {
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        mSeries = new XYSeries(curveTitle);
        multipleSeriesDataset.addSeries(mSeries);
    }

    /**
     * 设置x轴显示标签
     */
    public void setXTextLabels(String[] xTextLabels) {
        this.xLabel = xTextLabels;
    }

    /**
     * 获取渲染器
     *
     * @param maxX       x轴最大值
     * @param maxY       y轴最大值
     * @param chartTitle 曲线的标题
     * @param xTitle     x轴标题
     * @param yTitle     y轴标题
     * @param axeColor   坐标轴颜色
     * @param labelColor 标题颜色
     * @param curveColor 曲线颜色
     * @param gridColor  网格颜色
     */
    public void setXYMultipleSeriesRenderer2(double maxX, double maxY,
                                             String chartTitle, String xTitle, String yTitle, int axeColor,
                                             int labelColor, int curveColor, int gridColor, int type) {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        if (chartTitle != null) {
            multipleSeriesRenderer.setChartTitle(chartTitle);
        }
        multipleSeriesRenderer.setXTitle(xTitle);
        multipleSeriesRenderer.setYTitle(yTitle);
        multipleSeriesRenderer.setRange(new double[]{0, maxX, 0, maxY});//xy轴的范围
        multipleSeriesRenderer.setLabelsColor(labelColor);
        multipleSeriesRenderer.setXLabels(1);//显示标签
        multipleSeriesRenderer.setYLabelsPadding(5);
        if (type == 1) {
            multipleSeriesRenderer.setYLabels(5);
            multipleSeriesRenderer.setLabelsTextSize(12);
        } else if (type == 2) {
            multipleSeriesRenderer.setYLabels(7);
            multipleSeriesRenderer.setLabelsTextSize(14);
        } else if (type == 3) {
            multipleSeriesRenderer.setYLabels(7);
            multipleSeriesRenderer.setLabelsTextSize(12);
        }
        multipleSeriesRenderer.setXLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setAxisTitleTextSize(16);
        multipleSeriesRenderer.setChartTitleTextSize(10);
        multipleSeriesRenderer.setLegendTextSize(15);
        multipleSeriesRenderer.setPointSize(0);//曲线描点尺寸
        multipleSeriesRenderer.setFitLegend(true);
//        multipleSeriesRenderer.setMargins(new int[]{20, 30, 15, 20});
        multipleSeriesRenderer.setShowGrid(false);//是否显示网格
//        multipleSeriesRenderer.setShowLabels(false);//是否显示刻度
        multipleSeriesRenderer.setZoomEnabled(false, false); // 是否可缩放
        multipleSeriesRenderer.setPanEnabled(false, false); // 是否可拖动
        multipleSeriesRenderer.setAxesColor(axeColor);
        multipleSeriesRenderer.setXAxisColor(R.color.tv_green);//坐标轴文字颜色
        multipleSeriesRenderer.setYAxisColor(R.color.tv_green);
        multipleSeriesRenderer.setGridColor(gridColor);
        multipleSeriesRenderer.setBackgroundColor(Color.WHITE);//背景色
        multipleSeriesRenderer.setMargins(new int[]{20, 25, -30, 15});//设置空白区大小 距离左边右边
        multipleSeriesRenderer.setMarginsColor(Color.TRANSPARENT);//设置空白区颜色
        multipleSeriesRenderer.setShowLegend(false);//隐藏图例
        multipleSeriesRenderer.setMarginsColor(Color.WHITE);//边距背景色，默认背景色为黑色，这里修改为白色
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(curveColor);
        mRenderer.setPointStyle(PointStyle.CIRCLE);//描点风格，可以为圆点，方形点等等
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }


    public void setXYMultipleSeriesRenderer(double maxX, double maxY,
                                            String chartTitle, String xTitle, String yTitle, int axeColor,
                                            int labelColor, int curveColor, int gridColor, int type) {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        if (chartTitle != null) {
            multipleSeriesRenderer.setChartTitle(chartTitle);
        }
        multipleSeriesRenderer.setXTitle(xTitle);
        multipleSeriesRenderer.setYTitle(yTitle);
        multipleSeriesRenderer.setRange(new double[]{0, maxX, 0, maxY});//xy轴的范围
        multipleSeriesRenderer.setLabelsColor(labelColor);
        multipleSeriesRenderer.setXLabels(1);//不显示标签
        multipleSeriesRenderer.setYLabelsPadding(5);
        if (type == 1) {
            multipleSeriesRenderer.setYLabels(5);
            multipleSeriesRenderer.setLabelsTextSize(12);
        } else if (type == 2) {
            multipleSeriesRenderer.setYLabels(7);
            multipleSeriesRenderer.setLabelsTextSize(14);
        } else if (type == 3) {
            multipleSeriesRenderer.setYLabels(7);
            multipleSeriesRenderer.setLabelsTextSize(12);
        }
        multipleSeriesRenderer.setXLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setAxisTitleTextSize(16);
        multipleSeriesRenderer.setChartTitleTextSize(10);
        multipleSeriesRenderer.setLegendTextSize(15);
        multipleSeriesRenderer.setPointSize(0);//曲线描点尺寸
        multipleSeriesRenderer.setFitLegend(true);
//        multipleSeriesRenderer.setMargins(new int[]{20, 30, 15, 20});
        multipleSeriesRenderer.setShowGrid(false);//是否显示网格
//        multipleSeriesRenderer.setShowLabels(false);//是否显示刻度
        multipleSeriesRenderer.setZoomEnabled(false, false); // 是否可缩放
        multipleSeriesRenderer.setPanEnabled(false, false); // 是否可拖动
        multipleSeriesRenderer.setAxesColor(axeColor);
        multipleSeriesRenderer.setXAxisColor(R.color.tv_green);//坐标轴文字颜色
        multipleSeriesRenderer.setYAxisColor(R.color.tv_green);
        multipleSeriesRenderer.setGridColor(gridColor);
        multipleSeriesRenderer.setBackgroundColor(Color.WHITE);//背景色
        multipleSeriesRenderer.setMargins(new int[]{20, 25, -30, 15});//设置空白区大小 距离左边右边
        multipleSeriesRenderer.setMarginsColor(Color.TRANSPARENT);//设置空白区颜色
        multipleSeriesRenderer.setShowLegend(false);//隐藏图例
        multipleSeriesRenderer.setMarginsColor(Color.WHITE);//边距背景色，默认背景色为黑色，这里修改为白色
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(curveColor);
        mRenderer.setPointStyle(PointStyle.CIRCLE);//描点风格，可以为圆点，方形点等等
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }

    /**
     * 根据新加的数据，更新曲线，只能运行在主线程
     *
     * @param x 新加点的x坐标
     * @param y 新加点的y坐标
     */
    public void updateChart(double x, double y) {
        mSeries.add(x, y);
        mGraphicalView.invalidate();//此处也可以调用invalidate()
    }

    /**
     * 添加新的数据，多组，更新曲线，只能运行在主线程
     *
     * @param xList
     * @param yList
     */
    public void updateChart(ArrayList<Integer> xList, ArrayList<Double> yList) {
        for (int i = 0; i < xList.size(); i++) {
            mSeries.add(xList.get(i), yList.get(i));
        }
        mGraphicalView.invalidate();//此处也可以调用invalidate()
        mGraphicalView.destroyDrawingCache();
    }

    public void clearChart() {
        mSeries.clearSeriesValues();
        mGraphicalView.invalidate();//此处也可以调用invalidate()
    }

}
