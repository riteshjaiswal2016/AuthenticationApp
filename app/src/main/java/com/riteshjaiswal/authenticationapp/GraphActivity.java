package com.riteshjaiswal.authenticationapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {
    private LineChart mChart;
    private Context mContext;
    private int installment, growth, years;
    //private static ArrayList<Entry> YAxisBankEntries = new ArrayList<>();
    //private static ArrayList<Entry> YAxisRegularEntries = new ArrayList<>();
    private static ArrayList<Entry> YAxisDirectEntries = new ArrayList<>();
    private static ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    private static LineDataSet lineDataSetDirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mContext = this;
        mChart = (LineChart)findViewById(R.id.mChart);

        SeekBar changeInstallment = findViewById(R.id.changeInstallment);
        SeekBar changeGrowth = findViewById(R.id.changeGrowth);
        SeekBar changeYear = findViewById(R.id.changeYear);
        changeInstallment.setProgress(50);
        changeYear.setProgress(1);

        installment=getMappedInstallment(changeInstallment.getProgress());
        growth=getMappedGrowth(changeGrowth.getProgress());
        years=getMappedYears(changeYear.getProgress());
        Log.i("TAG","Before updategraph");
        YAxisDirectEntries = updateGraphData(installment,growth,years);
        Log.i("TAG", "After updategraph");

        lineDataSetDirect = new LineDataSet(YAxisDirectEntries, "DIRECT");
        //lineDataSetDirect.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //lineDataSetDirect.setCubicIntensity(0.3f);

        lineDataSetDirect.setDrawFilled(true);
        Log.i("TAG","Before lineardatasets");

        lineDataSetDirect.setColor(Color.GREEN);
        lineDataSetDirect.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSetDirect.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        lineDataSets.add(lineDataSetDirect);

        Log.i("TAG","Before setting data");
        mChart.setData(new LineData(lineDataSets));
        Log.i("TAG","Data set");
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);


        changeInstallment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Log.i("TAG","Progress "+i);
                installment = getMappedInstallment(i);
                Log.i("TAG", "INST. PROG "+installment);
                YAxisDirectEntries.clear();
                YAxisDirectEntries = updateGraphData(installment,growth,years);
                lineDataSetDirect = updateLineDataSetDirect(YAxisDirectEntries);

                lineDataSets = updateLineDataSets(lineDataSetDirect);

                updateChart(mChart);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        changeGrowth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                growth= getMappedGrowth(i);
                Log.i("TAG", "GROWT PROG "+growth);
                YAxisDirectEntries.clear();
                YAxisDirectEntries = updateGraphData(installment,growth,years);
                lineDataSetDirect = updateLineDataSetDirect(YAxisDirectEntries);

                lineDataSets = updateLineDataSets(lineDataSetDirect);

                updateChart(mChart);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        changeYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                years = getMappedYears(i);
                Log.i("TAG","YEAR PROG "+years);
                YAxisDirectEntries.clear();
                YAxisDirectEntries = updateGraphData(installment,growth,years);
                lineDataSetDirect = updateLineDataSetDirect(YAxisDirectEntries);

                lineDataSets = updateLineDataSets(lineDataSetDirect);

                updateChart(mChart);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private LineDataSet updateLineDataSetDirect(ArrayList<Entry> YAxisDirectEntries){
        lineDataSetDirect = new LineDataSet(YAxisDirectEntries, "DIRECT");
        //lineDataSetDirect.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //lineDataSetDirect.setCubicIntensity(0.3f);
        lineDataSetDirect.setDrawFilled(true);
        lineDataSetDirect.setColor(Color.GREEN);
        lineDataSetDirect.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return lineDataSetDirect;
    }

    private ArrayList<ILineDataSet> updateLineDataSets(LineDataSet lineDataSet){
        lineDataSets.clear();
        lineDataSets.add(lineDataSetDirect);
        return lineDataSets;
    }

    private LineChart updateChart(LineChart mChart){
        mChart.setData(new LineData(lineDataSets));
        Log.i("TAG","Data set");
        mChart.notifyDataSetChanged();
        mChart.invalidate();
        return mChart;
    }

    private long getMaturityAmount(int i, int g, int y){
        int numberOfInstallments =  (12*y);
        int returnsAssumed = g/12;
        long total=0;

        for(int j=1; j<=numberOfInstallments;j++){
            total+= i;
            total*= 1+returnsAssumed;
        }
        Log.i("TAG","TOTAL : "+total);
        return total;
    }

    private int getMappedInstallment(int i){
        return (i*1000+1000);
    }

    private int getMappedGrowth(int g){
        return (4+g);
    }

    private int getMappedYears(int y){
        return (y+1);
    }

    private ArrayList<Entry> updateGraphData(int i, int g, int y){
        ArrayList<Entry> arrayList = new ArrayList<>();
        long maturityAmount;
        for(int j=0;j<=y;j++){
            //Log.i("TAG","Insideupdategraph"+j);
            maturityAmount = getMaturityAmount(i,g,j);
            arrayList.add(new Entry(j,maturityAmount));
        }

        return arrayList;
    }



}
