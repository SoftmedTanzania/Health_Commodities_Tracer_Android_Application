package com.timotiusoktorio.inventoryapp.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.timotiusoktorio.inventoryapp.R;
import com.timotiusoktorio.inventoryapp.activity.DetailActivity;
import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.CategoryBalance;
import com.timotiusoktorio.inventoryapp.dom.objects.Product;
import com.timotiusoktorio.inventoryapp.dom.objects.ProductBalance;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;
import com.timotiusoktorio.inventoryapp.viewmodels.CategoryBalanceViewModel;
import com.timotiusoktorio.inventoryapp.viewmodels.ProductBalanceViewModel;
import com.timotiusoktorio.inventoryapp.viewmodels.TransactionsListViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

public class DashboardFragment extends Fragment {

    private PieChart mChart1;
    private BarChart mChart2;
    private List<String> categoryNames = new ArrayList<>();
    private List<Integer> sizes = new ArrayList<>();
    private AppDatabase appDatabase;
    private List<Product> products;
    private LinearLayout productBalancesList;

    private CategoryBalanceViewModel categoryBalanceViewModel;
    private ProductBalanceViewModel productBalanceViewModel;
    private List<CategoryBalance> categoryBalances;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rowview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        appDatabase = AppDatabase.getDatabase(getActivity().getApplicationContext());

        productBalancesList = (LinearLayout) rowview.findViewById(R.id.product_balances_list);

        //Pie chart configurations
        mChart1 = (PieChart) rowview.findViewById(R.id.chart1);
        mChart1.setUsePercentValues(true);
        mChart1.getDescription().setEnabled(false);
        mChart1.setExtraOffsets(5, 10, 5, 5);

        mChart1.setDragDecelerationFrictionCoef(0.95f);

        mChart1.setCenterText("Inventory Summary");

        mChart1.setDrawHoleEnabled(true);
        mChart1.setHoleColor(Color.WHITE);

        mChart1.setTransparentCircleColor(Color.WHITE);
        mChart1.setTransparentCircleAlpha(110);

        mChart1.setHoleRadius(58f);
        mChart1.setTransparentCircleRadius(61f);

        mChart1.setDrawCenterText(true);

        mChart1.setRotationAngle(0);
        mChart1.setRotationEnabled(true);
        mChart1.setHighlightPerTapEnabled(true);

        mChart1.animateY(1400, Easing.EasingOption.EaseInOutQuad);


        Legend l = mChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(7f);

        // entry label styling
        mChart1.setEntryLabelColor(Color.BLACK);
        mChart1.setEntryLabelTextSize(12f);




        //Bar chart configurations
        mChart2 = (BarChart) rowview.findViewById(R.id.chart2);
        mChart2.setDrawBarShadow(false);
        mChart2.setDrawValueAboveBar(true);

        mChart2.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart2.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart2.setPinchZoom(false);

        mChart2.setDrawGridBackground(false);
        // mChart2.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new XAxisValueFormatter();

        XAxis xAxis = mChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);
//        xAxis.setLabelRotationAngle(15f);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart2.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart2.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l2 = mChart2.getLegend();
        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l2.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l2.setDrawInside(false);
        l2.setForm(Legend.LegendForm.SQUARE);
        l2.setFormSize(9f);
        l2.setTextSize(11f);
        l2.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });


        categoryBalanceViewModel = ViewModelProviders.of(this).get(CategoryBalanceViewModel.class);
        categoryBalanceViewModel.getCategoryBalances().observe(getActivity(), new Observer<List<CategoryBalance>>() {
            @Override
            public void onChanged(@Nullable List<CategoryBalance> categoryBalances) {
                DashboardFragment.this.categoryBalances = categoryBalances;
                setData();
                mChart1.highlightValues(null);
                mChart1.invalidate();
            }
        });

        productBalanceViewModel = ViewModelProviders.of(this).get(ProductBalanceViewModel.class);
        productBalanceViewModel.getProductBalances().observe(getActivity(), new Observer<List<ProductBalance>>() {
            @Override
            public void onChanged(@Nullable List<ProductBalance> productBalances) {
                try{
                    productBalancesList.removeAllViews();
                    int i=1;
                    for (ProductBalance productBalance : productBalances) {
                        View v = getLayoutInflater().inflate(R.layout.view_inventory_balance_item,null);
                        ((TextView)v.findViewById(R.id.sn)).setText(String.valueOf(i));
                        ((TextView) v.findViewById(R.id.product_name)).setText(productBalance.getSubCategoryName()+" - "+productBalance.getProductName());

                        String balance = String.valueOf(productBalance.getBalance());
                        if(i>1) {
                            balance+=" "+productBalance.getUnit();
                        }
                        ((TextView)v.findViewById(R.id.balance)).setText(balance);
                        i++;
                        productBalancesList.addView(v);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });




        return rowview;
    }

    class MyAxisValueFormatter implements IAxisValueFormatter
    {

        public MyAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int v = (int)value;
            return v+"";
        }
    }

    class XAxisValueFormatter implements IAxisValueFormatter
    {
        public XAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return products.get((int)value).getName();
        }
    }

    private void setData() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (CategoryBalance categoryBalance:categoryBalances) {
            entries.add(new PieEntry((float) (categoryBalance.getBalance()),
                    categoryBalance.getName(),
                    getResources().getDrawable(R.drawable.ic_content_paste_white_24dp)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Category");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart1.setData(data);

        // undo all highlights
        mChart1.highlightValues(null);

        mChart1.invalidate();
    }

//    @SuppressLint("StaticFieldLeak")
//    private void loadReportData(final long fromDateTimestamp, final long toDateTimestamp){
//
//        new AsyncTask<Void, String, String>(){
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                categoryNames.clear();
//                sizes.clear();
//
//
//                List<Category> categories = appDatabase.categoriesModel().getAllCategories();
//
//                for(Category category:categories){
//                    Cursor c = mDbHelper.query("SELECT * FROM "+TABLE_PRODUCT+
//                            " INNER JOIN "+TABLE_TYPE+" ON "+TABLE_PRODUCT+"."+COLUMN_TYPE_ID+" = "+TABLE_TYPE+"."+_ID+
//                            " INNER JOIN "+TABLE_CATEGORY_SUB_CATEGORY+" ON "+TABLE_TYPE+"."+COLUMN_CATEGORY_SUB_CATEGORY_ID+" = "+TABLE_CATEGORY_SUB_CATEGORY+"."+_ID +
//                            " WHERE "+COLUMN_CATEGORY_ID+" = "+model.getmId()
//                    );
//
//                    if(c.getCount()>0){
//                        categoryNames.add(model.getmName());
//                        sizes.add(c.getCount());
//                    }
//                }
//                products = mDbHelper.queryProducts();
//                return "";
//            }
//
//            @Override
//            protected void onPostExecute(String aVoid) {
//                super.onPostExecute(aVoid);
//
//                setData();
//                mChart1.highlightValues(null);
//                mChart1.invalidate();
//
//                try{
//                    productBalancesList.removeAllViews();
//                    int i=1;
//                    for (Product product : products) {
//                        View v = getLayoutInflater().inflate(R.layout.view_inventory_balance_item,null);
//                        ((TextView)v.findViewById(R.id.sn)).setText(String.valueOf(i));
//                        ((TextView) v.findViewById(R.id.product_name)).setText(product.getmName());
//
//                        String balance = String.valueOf(product.getmQuantity());
//
//                        if(i>1) {
//                            balance+=" Kgs";
//                        }
//                        ((TextView)v.findViewById(R.id.balance)).setText(balance);
//
//
//                        i++;
//                        productBalancesList.addView(v);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                try {
//                    ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
//                    int i=0;
//                    for (Product product : products) {
//                        yVals1.add(new BarEntry(i, product.getmQuantity()));
//                        i++;
//                    }
//
//                    BarDataSet set1 = new BarDataSet(yVals1, "Inventory Balances");
//
//                    set1.setDrawIcons(false);
//
//                    set1.setColors(ColorTemplate.MATERIAL_COLORS);
//
//                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//                    dataSets.add(set1);
//
//                    BarData data = new BarData(dataSets);
//                    data.setValueTextSize(10f);
//                    data.setBarWidth(0.9f);
//
//                    mChart2.setData(data);
//
//                    mChart2.highlightValues(null);
//                    mChart2.invalidate();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//    }


}
