package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExpandableSampleActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter<IItem> fastItemAdapter;
    private ExpandableExtension<IItem> expandableExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //as we use an icon from Android-Iconics via xml we add the IconicsLayoutInflater
        //https://github.com/mikepenz/Android-Iconics
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_collapsible);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter
        fastItemAdapter = new FastItemAdapter<>();
        fastItemAdapter.withSelectable(true);
        expandableExtension = new ExpandableExtension<>();
        //expandableExtension.withOnlyOneExpandedItem(true);
        //MYNOTE: 2019/05/23 在最外层的adapter上添加拓展
        fastItemAdapter.addExtension(expandableExtension);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        // MYWHY: 2019/05/27 了解这里的动画效果
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(fastItemAdapter);

        //fill with some sample data
        List<IItem> items = new ArrayList<>();
        AtomicInteger identifier = new AtomicInteger(1);
        for (int i = 1; i <= 100; i++) {
            if (i % 3 != 0) {
                //MYNOTE: 2019/05/23 添加item（不可拓展）----SimpleSubItem
                items.add(new SimpleSubItem().withName("Test " + i).withIdentifier(identifier.getAndIncrement()));
                continue;
            }
            //MYNOTE: 2019/05/23 创建父item----SimpleSubExpandableItem
            SimpleSubExpandableItem parent = new SimpleSubExpandableItem();
            parent.withName("Test " + i).withIdentifier(identifier.getAndIncrement());

            //MYNOTE: 2019/05/23 创建一级子item列表
            List<IItem> subItems = new LinkedList<>();
            for (int ii = 1; ii <= 5; ii++) {
                //MYNOTE: 2019/05/23 创建一级子item----SimpleSubExpandableItem
                SimpleSubExpandableItem subItem = new SimpleSubExpandableItem();
                subItem.withName("-- SubTest " + ii).withIdentifier(identifier.getAndIncrement());

                if (ii % 2 == 0) {
                    continue;
                }
                //MYNOTE: 2019/05/23 创建二级子item列表
                List<IItem> subSubItems = new LinkedList<>();
                for (int iii = 1; iii <= 3; iii++) {
                    //MYNOTE: 2019/05/23 创建二级子item----SimpleSubExpandableItem
                    SimpleSubExpandableItem subSubItem = new SimpleSubExpandableItem();
                    subSubItem.withName("---- SubSubTest " + iii).withIdentifier(identifier.getAndIncrement());

                    //MYNOTE: 2019/05/23 创建三级子item列表
                    List<IItem> subSubSubItems = new LinkedList<>();
                    for (int iiii = 1; iiii <= 4; iiii++) {
                        //MYNOTE: 2019/05/23 创建三级子item（不拓展）----SimpleSubExpandableItem
                        SimpleSubExpandableItem subSubSubItem = new SimpleSubExpandableItem();
                        subSubSubItem.withName("---- SubSubSubTest " + iiii).withIdentifier(identifier.getAndIncrement());
                        subSubSubItems.add(subSubSubItem);
                    }
                    //MYNOTE: 2019/05/23 将三级子列表设置给二级子item
                    subSubItem.withSubItems(subSubSubItems);
                    subSubItems.add(subSubItem);
                }
                //MYNOTE: 2019/05/23 将二级子列表设置给一级子item
                subItem.withSubItems(subSubItems);
                subItems.add(subItem);
            }
            //MYNOTE: 2019/05/23 将一级子列表设置给一级item
            parent.withSubItems(subItems);
            items.add(parent);
        }
        fastItemAdapter.add(items);

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        //MYNOTE: 2019/05/23 导出onState，保存adapter状态（借鉴用于MD中）
        outState = fastItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
