package com.example.happiness.lab5;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class MainActivity extends AppCompatActivity {

    private static final String STATICACTION = "com.example.happiness.lab5.MyStaticFilter";//静态广播action字符串
    private ListView shopListView;
    private RecyclerView mRecyclerView;
    private SimpleAdapter simpleAdapter;
    private CommonAdapter commonAdapter;
    private FloatingActionButton switchButton;
    private List<Map<String, Object>> goodsList = new ArrayList<>();
    private List<Map<String, Object>> shopList = new ArrayList<>();
    private int REQUEST_CODE = 1;

    private int n = 10;//商品的个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化商品列表
        String[] goods_name = new String[]{"Enchated Forest", "Arla Milk", "Devondale Milk", "Kindle Oasis",
                "waitrose 早餐麦片", "Mcvitie's 饼干", "Ferrero Rocher", "Maltesers", "Lindt", "Borggreve"};
        String[] goods_price = new String[]{"¥ 5.00", "¥ 59.00", "¥ 79.00", "¥ 2399.00", "¥ 179.00",
                "¥ 14.90", "¥ 132.59","¥ 141.43","¥ 139.43","¥ 28.90"};
        int[] imageId = {R.mipmap.enchatedforest, R.mipmap.arla, R.mipmap.devondale, R.mipmap.kindle,
                R.mipmap.waitrose, R.mipmap.mcvitie, R.mipmap.ferrero, R.mipmap.maltesers,R.mipmap.lindt,
                R.mipmap.borggreve};
        for (int i = 0; i < 10; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("firstletter", goods_name[i].substring(0,1));
            temp.put("name", goods_name[i]);
            temp.put("price", goods_price[i]);
            temp.put("imageId", imageId[i]);
            goodsList.add(temp);
        }

        //初始化购物车列表
        Map<String, Object> temp = new LinkedHashMap<>();
        temp.put("firstletter", "*");
        temp.put("name", "购物车");
        temp.put("price", "价格");
        shopList.add(temp);

        mRecyclerView = (RecyclerView) findViewById(R.id.goodslist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter = new CommonAdapter(this, R.layout.item, goodsList) {
            @Override
            protected void convert(ViewHolder holder, Map<String, Object> map) {
                TextView firstletter = holder.getView(R.id.letter);
                firstletter.setText(map.get("firstletter").toString());
                TextView name = holder.getView(R.id.name);
                name.setText(map.get("name").toString());
            }
        };

        //为mRecyclerView添加动画
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(commonAdapter);
        animationAdapter.setDuration(1000);
        mRecyclerView.setAdapter(animationAdapter);
        mRecyclerView.setItemAnimator(new OvershootInLeftAnimator());

        //商品界面的点击事项
        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {//单击
                String goodsName = goodsList.get(position).get("name").toString();
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("name", goodsName);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void onLongClick(int position) {//长按
                String index = Integer.toString(position);
                goodsList.remove(position);
                commonAdapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this, "移除第" + index + "个商品", Toast.LENGTH_SHORT).show();
                //return true;
            }
        });

        //界面切换
        switchButton = (FloatingActionButton)findViewById(R.id.fab);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRecyclerView.getVisibility() == view.VISIBLE) {
                    mRecyclerView.setVisibility(view.GONE);
                    shopListView.setVisibility(View.VISIBLE);
                    switchButton.setImageResource(R.mipmap.mainpage);
                }
                else if(shopListView.getVisibility() == view.VISIBLE) {
                    mRecyclerView.setVisibility(view.VISIBLE);
                    shopListView.setVisibility(view.GONE);
                    switchButton.setImageResource(R.mipmap.shoplist);
                }
            }
        });

        shopListView = (ListView) findViewById(R.id.shoppinglist);
        simpleAdapter = new SimpleAdapter(this, shopList, R.layout.shoplist,
                new String[]{"firstletter", "name", "price"}, new int[]{R.id.letter, R.id.name, R.id.price});
        shopListView.setAdapter(simpleAdapter);
        shopListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long l) {
                //长按事件处理
                AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
                if(position!=0)
                {
                    message.setTitle("移除商品");
                    message.setMessage("从购物车移除"+shopList.get(position).get("name")+"?");
                    message.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            shopList.remove(position);
                            simpleAdapter.notifyDataSetChanged();
                        }
                    });
                    message.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                }
                message.create().show();
                return true;
            }
        });

        //购物车界面单击事件
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                if(position != 0) {//注意点击购物车标题的反应
                    String goodsName = shopList.get(position).get("name").toString();
                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    intent.putExtra("name", goodsName);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
        staticbroadcast();
    }

    void staticbroadcast()//静态广播
    {
        Random random = new Random();
        int i = random.nextInt(n);//返回一个0到n-1的正数
        Intent intentBroadcast = new Intent(STATICACTION );//定义Intent
        Bundle bundle = new Bundle();
        bundle.putString("name", goodsList.get(i).get("name").toString());
        bundle.putString("price",goodsList.get(i).get("price").toString());
        bundle.putInt("imageId",(int) goodsList.get(i).get("imageId") );
        intentBroadcast.putExtras( bundle);
        sendBroadcast(intentBroadcast);
        EventBus.getDefault().register(this);//注册订阅者
        simpleAdapter.notifyDataSetChanged();//用于进行静态广播后的数据更新
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  onMessageEvent(Bundle bundle){
        String name2 = bundle.getString("name");
        String price2 = bundle.getString("price");
        if(name2!=null && price2!=null){
            Map<String,Object> temp2 = new LinkedHashMap<>();
            temp2.put("firstletter", name2.substring(0,1));
            temp2.put("name", name2);
            temp2.put("price", price2);
            shopList.add(temp2);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        mRecyclerView.setVisibility(View.GONE);
        shopListView.setVisibility(View.VISIBLE);
        switchButton.setImageResource(R.mipmap.mainpage);
    }
}
