package com.riteshjaiswal.authenticationapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import org.w3c.dom.Text;

import java.text.Format;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {
    private static LineChart mChart;
    private Context mContext;
    private Format format;
    private TextView installmentText, growthText, yearText;
    private static TextView maturityAmountText;
    private static int installment, growth, years;
    private static ArrayList<Entry> YAxisBankEntries = new ArrayList<>();
    private static ArrayList<Entry> YAxisRegularEntries = new ArrayList<>();
    private static ArrayList<Entry> YAxisDirectEntries = new ArrayList<>();
    private static ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    private static LineDataSet lineDataSetDirect;
    private static LineDataSet lineDataSetRegular;
    private static LineDataSet lineDataSetBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        installmentText=findViewById(R.id.installmentText);
        growthText= findViewById(R.id.growthText);
        yearText=findViewById(R.id.yearText);
        format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en","in"));
        maturityAmountText=findViewById(R.id.maturityAmountValueText);

        mContext = this;
        mChart = (LineChart)findViewById(R.id.mChart);

        SeekBar changeInstallment = findViewById(R.id.changeInstallment);
        SeekBar changeGrowth = findViewById(R.id.changeGrowth);
        SeekBar changeYear = findViewById(R.id.changeYear);



        installment=getMappedInstallment(changeInstallment.getProgress());
        growth=getMappedGrowth(changeGrowth.getProgress());
        years=getMappedYears(changeYear.getProgress());


        installment=5000;
        growth=10;
        years=5;

        updateText();

        Log.i("TAG","Before updategraph");

        updateLineDataSets();

        lineDataSets.add(lineDataSetDirect);
        lineDataSets.add(lineDataSetRegular);
        lineDataSets.add(lineDataSetBank);

        maturityAmountText.setText(getMaturityAmount(installment,growth,years).get(0).toString());
        Log.i("TAG","Before update chart");
        updateChart();


        changeInstallment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                GraphActivity.installment = getMappedInstallment(i);
                updateText();
                lineDataSets.clear();
                GraphActivity.updateLineDataSets();
                lineDataSets.add(lineDataSetDirect);
                lineDataSets.add(lineDataSetRegular);
                lineDataSets.add(lineDataSetBank);
                maturityAmountText.setText( getMaturityAmount(installment,growth,years).get(0).toString());
                GraphActivity.updateChart();
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
                GraphActivity.growth = getMappedGrowth(i);
                updateText();
                lineDataSets.clear();
                GraphActivity.updateLineDataSets();
                lineDataSets.add(lineDataSetDirect);
                lineDataSets.add(lineDataSetRegular);
                lineDataSets.add(lineDataSetBank);
                maturityAmountText.setText( getMaturityAmount(installment,growth,years).get(0).toString());
                GraphActivity.updateChart();

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
                GraphActivity.years = getMappedYears(i);
                updateText();
                lineDataSets.clear();
                GraphActivity.updateLineDataSets();
                lineDataSets.add(lineDataSetDirect);
                lineDataSets.add(lineDataSetRegular);
                lineDataSets.add(lineDataSetBank);
                maturityAmountText.setText( getMaturityAmount(installment,growth,years).get(0).toString());
                GraphActivity.updateChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateText(){
        installmentText.setText(format.format(installment));
        growthText.setText(growth+"%");
        if(years!=1)
            yearText.setText(years+" Years");
        else
            yearText.setText(years+" Year");

    }

    static void setChartProps(){
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisRight().setAxisMinimum(0f);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Description description = new Description();
        description.setText("Years");
        mChart.setDescription(description);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisRight().setDrawTopYLabelEntry(true);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setEnabled(false);
    }

    static void setLineDatasetProps(LineDataSet lineDataSet,int color){
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.3f);

        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.CYAN);

        lineDataSet.setColor(color);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
    }

    static void updateLineDataSets(){
        ArrayList<ArrayList<Entry>> arrayLists= updateGraphData();
        YAxisDirectEntries = arrayLists.get(0);
        YAxisRegularEntries = arrayLists.get(1);
        YAxisBankEntries = arrayLists.get(2);

        lineDataSetDirect = new LineDataSet(YAxisDirectEntries, "Direct Mutual Funds");
        setLineDatasetProps(lineDataSetDirect,Color.GREEN);
        lineDataSetRegular= new LineDataSet(YAxisRegularEntries, "Regular Mutual Funds");
        setLineDatasetProps(lineDataSetRegular,Color.BLUE);
        lineDataSetBank= new LineDataSet(YAxisBankEntries, "Bank Deposits");
        setLineDatasetProps(lineDataSetBank,Color.MAGENTA);
        Log.i("TAG","updatelinedatset DOne");
    }

    static void updateChart(){

        setChartProps();
        mChart.getAxisRight().setDrawTopYLabelEntry(true);
        LineData lineData=new LineData(lineDataSets);
        mChart.setData(lineData);
        mChart.getAxisRight().setValueFormatter(new IndianCurrencyFormatter());

        //mChart.highlightValue(years,0);
        //Log.i("TAG","chart dat set");
        mChart.notifyDataSetChanged();
        mChart.invalidate();
        Log.i("TAG","updateChart Done");
    }

    static ArrayList<Double> getMaturityAmount(int i, double g, int y){
        int numberOfInstallments =  (12*y);

        double directReturnsAssumed = (g/100)/12;
        double regularReturnsAssumed = ((g-1)/100)/12;
        double bankReturnsAssumed = 0.04/12;
        double directTotal=0;
        double bankTotal=0;
        double regularTotal=0;

        for(int j=1; j<=numberOfInstallments;j++){
            directTotal+= i;
            directTotal*= 1+directReturnsAssumed;
            regularTotal+= i;
            regularTotal*= 1+regularReturnsAssumed;
            bankTotal+=i;
            bankTotal*= 1+bankReturnsAssumed;
        }


        ArrayList<Double> maturityAmounts = new ArrayList<>();
        maturityAmounts.add(directTotal);
        maturityAmounts.add(regularTotal);
        maturityAmounts.add(bankTotal);

        Log.i("TAG","DIRECT, REGULAR, BANK : "+directTotal+", "+regularTotal+", "+bankTotal);
        return maturityAmounts;
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


    static ArrayList<ArrayList<Entry>> updateGraphData(){
        double maturityAmount;
        ArrayList<ArrayList<Entry>> directRegularBankDataLists = new ArrayList<ArrayList<Entry>>();
        ArrayList<Entry> directDataList= new ArrayList<>();
        ArrayList<Entry> regularDataList= new ArrayList<>();
        ArrayList<Entry> bankDataList= new ArrayList<>();
        ArrayList<Double> arrayList= new ArrayList<>();

        for(int j=0;j<=years;j++) {
            arrayList= getMaturityAmount(installment,growth,j);
            maturityAmount=arrayList.get(0);
            directDataList.add(new Entry(j, (long)maturityAmount));
            maturityAmount=arrayList.get(1);
            regularDataList.add(new Entry(j, (long)maturityAmount));
            maturityAmount=arrayList.get(2);
            bankDataList.add(new Entry(j, (long)maturityAmount));
        }

        directRegularBankDataLists.add(directDataList);
        directRegularBankDataLists.add(regularDataList);
        directRegularBankDataLists.add(bankDataList);

        Log.i("TAG", "updateGraphData Done");
        return directRegularBankDataLists;


    }



}
