package com.xcorpio.birdcontacts;

import com.xcorpio.db.DBHelper;
import com.xcorpio.entity.User;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class AddNewActivity extends Activity {

	ImageButton btn_img;	//头像
	int img_index;				//头像
	AlertDialog imageChooseDialog;	//选择头像对话框
	ImageSwitcher is;	
	Gallery gallery;
	private int[] img_touxiang={	//头像图片id
			R.drawable.tou_xiang1,
			R.drawable.tou_xiang2,
			R.drawable.tou_xiang3,
			R.drawable.tou_xiang4,
			R.drawable.tou_xiang5,
			R.drawable.tou_xiang6
	};
	
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
	Button btn_save;
	Button btn_return;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_add_new);
		
		initWidgets();
		
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
		
		btn_save=(Button) findViewById(R.id.btn_save);
		btn_return=(Button)findViewById(R.id.btn_return);
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name=et_name.getText().toString();
				if(name.equals("")){	//判断名字是否为空
					Toast.makeText(AddNewActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				User u=new User();
				u.name=name;
				u.address=et_address.getText().toString();
				u.email=et_email.getText().toString();
				u.familyPhone=et_familyPhone.getText().toString();
				u.imageID=img_touxiang[img_index];
				u.mobilePhone=et_mobilePhone.getText().toString();
				u.officePhone=et_officePhone.getText().toString();
				u.otherContact=et_otherContact.getText().toString();
				u.position=et_position.getText().toString();
				u.remark=et_remark.getText().toString();
				u.zipCode=et_zipCode.getText().toString();
				u.company=et_company.getText().toString();
				//保存 user 到数据库
				if(DBHelper.getInstance(AddNewActivity.this).save(u)){
					Toast.makeText(AddNewActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
					//AddNewActivity.this.finish();
					setResult(0);
					AddNewActivity.this.finish();
				}else{
					Toast.makeText(AddNewActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		btn_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddNewActivity.this.finish();
			}
		});
		//头像按钮
		btn_img=(ImageButton) findViewById(R.id.btn_img);
		btn_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initImageChooseDialog();
				imageChooseDialog.show();
			}
		});
	}
	/**
	 * 初始化选择头像对话框
	 */
	private void initImageChooseDialog(){
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("请选择头像");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				btn_img.setImageResource(img_touxiang[img_index]);
			}
		});
		builder.setNegativeButton("取消", null);
		LayoutInflater inflater=LayoutInflater.from(this);
		View view=inflater.inflate(R.layout.layout_image_choose, null);
		gallery=(Gallery) view.findViewById(R.id.img_gallery);
		is=(ImageSwitcher) view.findViewById(R.id.img_switcher);
		is.setFactory(new MyViewFactory(this));
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setSpacing(20);
		gallery.setSelection(img_touxiang.length/2);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				is.setImageResource(img_touxiang[arg2%img_touxiang.length]);
				img_index=arg2%img_touxiang.length;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.setView(view);
		imageChooseDialog=builder.create();
	}
	
	/**
	 * 自定义ViewFactory
	 * @author Dong
	 *
	 */
	class MyViewFactory implements ViewFactory{
		private Context context;
		public MyViewFactory(Context context){
			this.context=context;
		}
		@Override
		public View makeView() {
			// TODO Auto-generated method stub
			ImageView iv=new ImageView(context);
			iv.setLayoutParams(new ImageSwitcher.LayoutParams(100,100));;
			return iv;
		}
		
	}
	
	/**
	 *自定义适配器 
	 * @author Dong
	 */
	class ImageAdapter extends BaseAdapter{

		private Context context;
		
		public ImageAdapter(Context context){
			this.context=context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return img_touxiang[position%img_touxiang.length];
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null){
				ImageView iv=new ImageView(context);
				iv.setImageResource(img_touxiang[position%img_touxiang.length]);
				iv.setLayoutParams(new Gallery.LayoutParams(50,50));
				convertView=iv;
				Log.i("adapter", "convertView==null");
			}else{
				Log.i("adapter", "convertView!=null");
			}
			return convertView;
		}
		
	}

	/** 回收资源  */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(is != null) {
			is = null;
		}
		if(gallery != null) {
			gallery = null;
		}
		if(imageChooseDialog != null) {
			imageChooseDialog = null;
		}
		super.onDestroy();
	}
	
	
}
