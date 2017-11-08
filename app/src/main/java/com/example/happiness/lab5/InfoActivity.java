package com.example.happiness.lab5;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class InfoActivity extends AppCompatActivity{

    private String selected_Name = null;
    private String selected_Price = null;
    private int selected_Image;
    private String data = null;
    private int position = 0;
    private boolean tag = false;
    private List<Map<String, Object>> infoList = new ArrayList<>();
    private List<Map<String, Object>> moreList = new ArrayList<>();
    private static final String DYNAMICACTION = "com.example.happiness.lab5.MyDynamicFilter";//动态广播action字符串
    Receiver dynamicReceiver = new Receiver();
    private int n = 10;//商品的个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goodsinfo);

        String[] names = new String[]{"Enchated Forest", "Arla Milk", "Devondale Milk", "Kindle Oasis", "waitrose 早餐麦片",
                "Mcvitie's 饼干", "Ferrero Rocher","Maltesers","Lindt","Borggreve"};
        String[] prices = new String[]{"¥ 5.00", "¥ 59.00", "¥ 79.00", "¥ 2399.00", "¥ 179.00",
                "¥ 14.90", "¥ 132.59","¥ 141.43","¥ 139.43","¥ 28.90"};
        final String[] infors = new String[]{"作者 Johanna Basford","产地 德国","产地 澳大利亚","版本 8GB",
                "重量 2Kg","产地 英国","重量 300g","重量 118g","重量 249g","重量 640g"};
        int[] imageId = {R.mipmap.enchatedforest, R.mipmap.arla, R.mipmap.devondale, R.mipmap.kindle,
                R.mipmap.waitrose, R.mipmap.mcvitie, R.mipmap.ferrero, R.mipmap.maltesers,R.mipmap.lindt,
                R.mipmap.borggreve};
        for(int i = 0; i < 10; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("name", names[i]);
            temp.put("price", prices[i]);
            temp.put("info", infors[i]);
            temp.put("imageId",imageId[i]);
            infoList.add(temp);
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            data = extras.getString("name");
            for(int i = 0; i < infoList.size(); i++) {
                if(infoList.get(i).get("name").toString().equals(data)) {
                    position = i;
                }
            }
        }
        TextView goodsName = (TextView) findViewById(R.id.goodsName);
        goodsName.setText(data);


        ImageView goodsImage = (ImageView) findViewById(R.id.goodsimage);
        int id = imageId[position];
        goodsImage.setImageResource(id);

        TextView goodsPrice = (TextView) findViewById(R.id.goodsPrice);
        goodsPrice.setText(infoList.get(position).get("price").toString());

        TextView goodsInfo = (TextView) findViewById(R.id.goodsInfo);
        goodsInfo.setText(infoList.get(position).get("info").toString());

        //返回操作
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name", selected_Name);
                bundle.putString("price", selected_Price);
                bundle.putInt("imageId", selected_Image);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //星星点击问题
        final Button star =(Button) findViewById(R.id.star);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("NewApi")//解决版本问题
            public void onClick(View view) {
                if(!tag){
                    star.setBackground(getDrawable(R.mipmap.full_star));
                    tag = true;
                }else{
                    star.setBackground(getDrawable(R.mipmap.empty_star));
                    tag = false;
                }
            }
        });

        //更多消息
        String[] operations = {"一键下单", "分享商品", "不感兴趣", "查看更多商品促销信息"};
        for(int i = 0; i < 4; i++) {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("function", operations[i]);
            moreList.add(temp);
        }
        ListView otherListView = (ListView)findViewById(R.id.otherListView);
        SimpleAdapter moreListAdapter = new SimpleAdapter(this, moreList, R.layout.morelist, new String[]{"function"}, new int[]{R.id.moreList});
        otherListView.setAdapter(moreListAdapter);

        registerBroadcast();//注册广播

        //动态广播---添加商品到购物车
        Button shop_car = (Button) findViewById(R.id.shop_car);
        shop_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InfoActivity.this, "商品已添加到购物车", Toast.LENGTH_SHORT).show();;
                dynamicbroadcast();//动态广播
            }
        });
    }

    void registerBroadcast()//注册广播
    {
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(DYNAMICACTION);
        registerReceiver(dynamicReceiver, dynamic_filter);
    }

    @Override
    protected void onDestroy(){//注销广播
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
    }


    public void dynamicbroadcast() //动态广播
    {
        Intent intentBroadcast = new Intent(DYNAMICACTION);//定义Intent
        selected_Name = infoList.get(position).get("name").toString();
        selected_Price = infoList.get(position).get("price").toString();
        selected_Image = (int) infoList.get(position).get("imageId");
        Bundle bundle = new Bundle();
        bundle.putString("name", selected_Name);
        bundle.putString("price", selected_Price);
        bundle.putInt("imageId", selected_Image);
        intentBroadcast.putExtras(bundle);
        sendBroadcast(intentBroadcast);
        EventBus.getDefault().post(bundle);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("name", selected_Name);
        bundle.putString("price", selected_Price);
        bundle.putInt("imageId", selected_Image);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}