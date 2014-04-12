package com.xcorpio.birdcontacts;

import java.util.ArrayList;
import java.util.HashMap;

import com.xcorpio.db.DBHelper;
import com.xcorpio.entity.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class DetailActivity extends Activity {

	ImageButton btn_img;	//头像
	EditText et_name;
	EditText et_mobilePhone;
	EditText et_officePhone;
	EditText et_familyPhone;
	EditText et_position;
	EditText et_company;
	EditText et_address;
	EditText et_zipCode;
	EditText et_email;
	EditText et_otherContact;
	EditText et_remark;
	
	Button btn_modify;
	Button btn_return;
	Button btn_delete;
	
	String[] callData;
	//表示状态：打电话，发短信，发邮件
	String status;
	//拥有一个user实例，这个对象由Intent传过来
	User user;
	View numChooseView;
	View imageChooseView;
	
	//号码选择的对话框
	AlertDialog numChooseDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_detail);
		
		Intent intent=getIntent();
		user=(User) intent.getSerializableExtra("user");
		initWidgets();
		setEditTextEnable(false);
	}
	
	/** 设置编辑框可编辑   */
	private void setEditTextEnable(boolean b){
		et_name.setEnabled(b);
		et_mobilePhone.setEnabled(b);
		et_officePhone.setEnabled(b);
		et_familyPhone.setEnabled(b);
		et_position.setEnabled(b);
		et_company.setEnabled(b);
		et_address.setEnabled(b);
		et_zipCode.setEnabled(b);
		et_email.setEnabled(b);
		et_otherContact.setEnabled(b);
		et_remark.setEnabled(b);
		btn_img.setEnabled(b);
	}

	/** 初始化控件 */
	private void initWidgets(){
		et_name=(EditText) findViewById(R.id.et_name);
		et_mobilePhone=(EditText) findViewById(R.id.et_mobilephonenum);
		et_officePhone=(EditText) findViewById(R.id.et_officephonenum);
		et_familyPhone=(EditText) findViewById(R.id.et_familyphonenum);
		et_position=(EditText) findViewById(R.id.et_position);
		et_company=(EditText) findViewById(R.id.et_company);
		et_address=(EditText) findViewById(R.id.et_address);
		et_zipCode=(EditText) findViewById(R.id.et_zipcode);
		et_email=(EditText) findViewById(R.id.et_email);
		et_otherContact=(EditText) findViewById(R.id.et_other);
		et_remark=(EditText) findViewById(R.id.et_remark);
		
		//头像按钮
		btn_img=(ImageButton) findViewById(R.id.btn_img);
		
		et_name.setText(user.name);
		et_mobilePhone.setText(user.mobilePhone);
		et_familyPhone.setText(user.familyPhone);
		et_officePhone.setText(user.officePhone);
		et_company.setText(user.company);
		et_address.setText(user.address);
		et_zipCode.setText(user.zipCode);
		et_otherContact.setText(user.otherContact);
		et_email.setText(user.email);
		et_remark.setText(user.remark);
		et_position.setText(user.position);
		btn_img.setImageResource(user.imageID);
		
		btn_modify=(Button) findViewById(R.id.btn_modify);
		btn_return=(Button)findViewById(R.id.btn_return);
		btn_delete=(Button) findViewById(R.id.btn_delete);
		
		btn_modify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(btn_modify.getText().equals("修改")){
					btn_modify.setText("保存");
					setEditTextEnable(true);
				}else if(btn_modify.equals("保存")){
					btn_modify.setText("修改");
					setEditTextEnable(false);
				}
			}
		});
		btn_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DetailActivity.this.finish();
			}
		});
		
		btn_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(DetailActivity.this).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						delete(user._id);
						setResult(4);
						finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).setTitle("是否要删除").show();
			}
		});
	}
	
	/** 删除该用户  */ 
	private void delete(int _id){
		DBHelper dbHelper=DBHelper.getInstance(DetailActivity.this);
		dbHelper.delete(_id);
	}
	
	/**
	 * 为Menu添加几个选项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.addSubMenu(0, Menu.FIRST, 1, "打电话");
		menu.addSubMenu(0, Menu.FIRST+1, 2, "发短信");
		menu.addSubMenu(0, Menu.FIRST+2, 3, "发邮件");
		
		//为每一个Item设置图标
		MenuItem item = menu.getItem(Menu.FIRST-1);
		item.setIcon(R.drawable.dial);
		MenuItem item1 = menu.getItem(Menu.FIRST);
		item1.setIcon(R.drawable.send_sms);
		MenuItem item2 = menu.getItem(Menu.FIRST+1);
		item2.setIcon(R.drawable.mail);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 为每一个MenuItem添加事件
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch(item.getItemId()){
			case Menu.FIRST: {
				//将状态设置为打电话
				status = Intent.ACTION_CALL;
				if(callData == null) {
					//加载可用的号码
					loadAvailableCallData();
				}
				
				if(callData.length == 0) {
					//提示没有可用的号码
					Toast.makeText(this, "没有可用的号码！", Toast.LENGTH_LONG).show();
				} else if(callData.length == 1) {
					//如果之有一个可用的号码，这直接使用这个号码拨出
					Intent intent = 
						new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + callData[0]));
					startActivity(intent);
				} else {
					//如果有2个或者2个以上号码，弹出号码选择对话框
					initNumChooseDialog();
				}
				break;
			}
			case Menu.FIRST+1: {
				status = Intent.ACTION_SENDTO;
				if(callData == null) {
					loadAvailableCallData();
				}
				if(callData.length == 0) {
					//提示没有可用的号码
					Toast.makeText(this, "没有可用的号码！", Toast.LENGTH_LONG).show();
				} else if(callData.length == 1) {
					//如果之后又一个可用的号码，这直接使用这个号码拨出
					Intent intent = 
						new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto://" + callData[0]));
					startActivity(intent);
				} else {
					initNumChooseDialog();
				}
				break;
			}
			case Menu.FIRST+2: {
				
				if(user.email.equals("")) {
					Toast.makeText(this, "没有可用的邮箱！", Toast.LENGTH_LONG).show();
				} else {
					Uri emailUri = Uri.parse("mailto:" + user.email);
					Intent intent = new Intent(Intent.ACTION_SENDTO, emailUri);
					startActivity(intent);
				}
				break;
			}
		
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void initNumChooseDialog() {
		if(numChooseDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = LayoutInflater.from(this);
			numChooseView = inflater.inflate(R.layout.layout_numchoose, null);
			ListView lv = (ListView)numChooseView.findViewById(R.id.num_list);
		    ArrayAdapter<String> array = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,callData);
		    lv.setAdapter(array);
		    lv.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					String num = String.valueOf(arg0.getItemAtPosition(arg2));
					Intent intent = null;
					if(status.equals(Intent.ACTION_CALL)) {
						intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + num));
					} else {
						intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto://" + num));
					}
					
					startActivity(intent);
					//对话框消失
					numChooseDialog.dismiss();
				}});
		    
		    
			builder.setView(numChooseView);
			numChooseDialog = builder.create();
			
		}
		numChooseDialog.show();
	}
	
	/**
	 * 装载可用的号码
	 */
	public void loadAvailableCallData() {
		ArrayList<String> callNums = new ArrayList<String>();
		if(!user.mobilePhone.equals("")) {
			callNums.add(user.mobilePhone);
		}
		if(!user.familyPhone.equals("")) {
			callNums.add(user.familyPhone);
		}
		
		if(!user.officePhone.equals("")) {
			callNums.add(user.officePhone);
		}
		
		
		callData = new String[callNums.size()];
		
		for(int i=0;i<callNums.size();i++) {
			callData[i] = callNums.get(i);
		}
		
		
	}
}
