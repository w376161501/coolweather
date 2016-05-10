package com.android.wang.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wang.coolweather.R;
import com.android.wang.coolweather.model.City;
import com.android.wang.coolweather.model.CoolWeatherDB;
import com.android.wang.coolweather.model.County;
import com.android.wang.coolweather.model.Province;
import com.android.wang.coolweather.util.HttpCallbackListener;
import com.android.wang.coolweather.util.HttpUtil;
import com.android.wang.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/10.
 */
public class ChooseAreaActivity extends Activity {
    public  static final int Level_Province=0;
    public  static final int Level_City=1;
    public  static final int Level_County=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<String>();
    private List<Province> privinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("city_selected",false))
        {
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.choose_area);
        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==Level_Province)
                {
                    selectedProvince=privinceList.get(position);
                    queryCity();
                }
                else if(currentLevel==Level_City)
                {
                    selectedCity=cityList.get(position);
                    queryCounty();
                }
                else if(currentLevel==Level_County)
                {
                    String countryCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("country_code",countryCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();
    }
    private void queryProvince()
    {
        privinceList=coolWeatherDB.loadProvince();
        if(privinceList.size()>0)
        {
            dataList.clear();
            for (Province province:privinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=Level_Province;
        }
        else
        {
            queryFromServe(null,"province");
        }
    }
    private void queryCounty()
    {
        cityList=coolWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0)
        {
            dataList.clear();
            for (City city:cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=Level_City;
        }
        else
        {
            queryFromServe(selectedProvince.getProvinceCode(),"province");
        }
    }
    private void queryCity()
    {
        countyList=coolWeatherDB.loadCounty(selectedCity.getId());
        if(countyList.size()>0)
        {
            dataList.clear();
            for (County county:countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=Level_County;
        }
        else
        {
            queryFromServe(selectedCity.getCityCode(),"province");
        }
    }
    private void queryFromServe(final String  code,final String type)
    {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }
        else
        {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String t) {
                boolean result=false;
                if("province".equals(type))
                {
                    result= Utility.handleProvinceResponse(coolWeatherDB,t);
                }
                else if("city".equals(type))
                {
                    result= Utility.handleCityResponse(coolWeatherDB,t,ChooseAreaActivity.this.selectedProvince.getId());
                }
                else if("county".equals(type))
                {
                    result= Utility.handleCountyResponse(coolWeatherDB,t,ChooseAreaActivity.this.selectedCity.getId());
                }
                if(result)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CloseProgressDialog();
                            if("province".equals(type))
                            {
                               queryProvince();
                            }
                            else if("city".equals(type))
                            {
                                queryCity();
                            }
                            else if("county".equals(type))
                            {
                               queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           CloseProgressDialog();
                           Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG).show();
                       }
                   });
            }
        });
    }
    private void showProgressDialog()
    {
        if(progressDialog==null)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载。。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void CloseProgressDialog()
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
    }
    @Override
    public void onBackPressed()
    {
        if(currentLevel==Level_County)
        {
            queryCity();
        }
        else if(currentLevel==Level_City)
        {
            queryProvince();
        }
        else {
            finish();
        }
    }
}
