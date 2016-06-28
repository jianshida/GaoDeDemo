package cn.cloudtop.gaodedemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

import cn.cloudtop.gaodedemo.R;
import cn.cloudtop.gaodedemo.adapter.PoiSearchTipAdapter;

/**
 * 手动输入地址搜索
 * @author james
 */
public class InputPositionActivity extends Activity implements TextWatcher, View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView back;
    private TextView titleText;
    private Button titleRightBtn;
    private ListView lvSearch;
    private EditText etCarAddress;
    private PoiSearchTipAdapter tipAdapter;
    private List<Tip> tipLists;
    private List<String> historyLists;
    private SharedPreferences spHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_position);
        findView();
        initData();
    }

    private void findView() {
        back = (ImageView) findViewById(R.id.back);
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText("输入地址");
        titleRightBtn = (Button) findViewById(R.id.title_right_btn);
        lvSearch = (ListView) findViewById(R.id.ip_lv_search);
        lvSearch.setOnItemClickListener(this);
        etCarAddress = (EditText) findViewById(R.id.ip_et_car_address);
    }

    // 初始化数据
    private void initData() {
        // TODO Auto-generated method stub
        spHistory = getSharedPreferences("history_strs", 0);
        tipLists = new ArrayList<>();
        etCarAddress.addTextChangedListener(this);
        tipAdapter = new PoiSearchTipAdapter(this, tipLists);
        lvSearch.setAdapter(tipAdapter);
        String save_Str = spHistory.getString("history", "");
        historyLists = new ArrayList<>();
        String[] hisArrays = save_Str.split(",");
        for (int i = 0; i < hisArrays.length; i++) {
            if (!TextUtils.isEmpty(hisArrays[i]))
                historyLists.add(hisArrays[i]);
        }
        lvSearch.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.ip_iv_clean:
                etCarAddress.setText("");
                break;
            case R.id.ip_tv_cancle:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String keyWord = tipAdapter.getResult().get(position).getName();
        Intent intent = new Intent();
        intent.putExtra("inputPossition", keyWord);
        setResult(100, intent);
        finish();
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        lvSearch.setVisibility(View.VISIBLE);
        String newText = s.toString().trim();
        Inputtips inputtips = new Inputtips(InputPositionActivity.this, new InputtipsListener() {

            @Override
            public void onGetInputtips(List<Tip> tipList, int rCode) {
                // TODO Auto-generated method stub
                if (rCode == 0) {// 正确返回
                    tipAdapter.update(tipList);
                }
            }
        });
        try {
            inputtips.requestInputtips(newText, "");
        } catch (AMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
