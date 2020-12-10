package com.example.leochris.launcher.weather;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.view.LineChartView;
import com.example.leochris.launcher.R;
import com.example.leochris.launcher.weather.CardController;

import java.util.Date;



public class LineChart extends CardController {

    private final LineChartView mChart;

    private final Context mContext;

    private Tooltip mTip;

    private String[] mLabels = {"Today", "Mon", "Tue", "Wed", "Thu", "Fri"};

    private float[][] mValues = {{30f, 25f, 27f, 28f, 26f, 29f},
            {24f, 24f, 25f, 26f, 24f, 27f}};

    private Runnable mBaseAction;

    public LineChart(CardView card, Context context) {

        super(card);

        mContext = context;
        mChart = (LineChartView) card.findViewById(R.id.line_chart);
    }


    @Override
    public void show(Runnable action) {

        super.show(action);

        mTip = new Tooltip(mContext, R.layout.tooltip, R.id.value);

        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(58), (int) Tools.fromDpToPx(25));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

            mTip.setPivotX(Tools.fromDpToPx(65) / 2);
            mTip.setPivotY(Tools.fromDpToPx(25));
        }
        mChart.setTooltips(mTip);

        Date mDate = new Date();
        System.out.println(mDate.getDay());
        switch(mDate.getDay()) {
            case 1:
                mLabels = new String[]{"Today", "Tue", "Wed", "Thu", "Fri", "Sat"};
                break;
            case 2:
                mLabels = new String[]{"Today", "Wed", "Thu", "Fri", "Sat", "Sun"};
                break;
            case 3:
                mLabels = new String[]{"Today", "Thu", "Fri", "Sat", "Sun", "Mon"};
                break;
            case 4:
                mLabels = new String[]{"Today", "Fri", "Sat", "Sun", "Mon", "Tue"};
                break;
            case 5:
                mLabels = new String[]{"Today", "Sat", "Sun", "Mon", "Tue", "Wed"};
                break;
            case 6:
                mLabels = new String[]{"Today", "Sun", "Mon", "Tue", "Wed", "Thu"};
                break;
            case 7:
                mLabels = new String[]{"Today", "Mon", "Tue", "Wed", "Thu", "Fri"};
                break;
        }

        // Data
        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#add8e6"))
                .setFill(Color.TRANSPARENT)
                .setDotsColor(Color.WHITE)
                .setThickness(3);
        mChart.addData(dataset);

        dataset = new LineSet(mLabels, mValues[1]);
        dataset.setColor(Color.parseColor("#DCDCDC"))
                .setFill(Color.TRANSPARENT)
                .setDotsColor(Color.WHITE)
                .setThickness(3);
        mChart.addData(dataset);

        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(20, 40)
                .setYLabels(AxisRenderer.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(false)
                .setYAxis(false);

        mChart.setLabelsColor(Color.WHITE);

        //display temperature if clicked
        mBaseAction = action;
        Runnable chartAction = new Runnable() {
            @Override
            public void run() {
                mTip.prepare(mChart.getEntriesArea(0).get(3), mValues[0][3]);
                mChart.showTooltip(mTip, true);
                mBaseAction.run();
            }
        };

        Animation anim = new Animation().setEasing(new BounceInterpolator()).setEndAction(chartAction);

        mChart.show(anim);
    }


    @Override
    public void update() {

        super.update();

        mChart.dismissAllTooltips();

        if (firstStage) {
            mChart.updateValues(0, mValues[0]);
            mChart.updateValues(1, mValues[1]);
        } else {
            mChart.updateValues(0, mValues[0]);
            mChart.updateValues(1, mValues[1]);
        }

        mChart.getChartAnimation().setEndAction(mBaseAction);
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {

        super.dismiss(action);

        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation().setEasing(new BounceInterpolator()).setEndAction(action));
    }

    public void setmValues(float[][] mValues) {
        this.mValues = mValues;
    }

    public void setRange(int lowLim, int upLim) {
        mChart.setAxisBorderValues(lowLim, upLim, 1);
    }
}
