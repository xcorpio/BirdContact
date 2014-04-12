package com.xcorpio.birdcontacts;

import java.util.ArrayList;
import java.util.HashMap;

import com.xcorpio.db.DBHelper;
import com.xcorpio.entity.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	GridView gv_buttom_menu;
	ListView lv_userList;
	//װ�������linearlayout,Ĭ�������visibility=gone
	LinearLayout searchLinearout;
	LinearLayout mainLinearLayout;
	//������
	EditText et_search;
	EditText et_enter_file_name;
	// �洢��ǵ���Ŀ
	int markedNum;
	// �洢�����Ŀ��_id��
	ArrayList<Integer> deleteId;
	//���˵��ĶԻ���
	AlertDialog mainMenuDialog;
	//���˵��Ĳ���
	GridView mainMenuGrid;
	//�˵���ͼ
	View mainMenuView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_main);
		
		loadUserList();
		loadButtomMenu();
	}

	/** �����û��б� */
	private void loadUserList(){
		lv_userList=(ListView) findViewById(R.id.lv_userlist);
		mainLinearLayout=(LinearLayout) findViewById(R.id.list);
		ArrayList<HashMap<String, Object>> data=DBHelper.getInstance(this).getUserList();
		SimpleAdapter adapter=new SimpleAdapter(this, data, R.layout.layout_list_item, new String[]{"imageid", "name","mobilephone"},new int[]{R.id.item_userimage,R.id.item_showname,R.id.item_showphone});
		lv_userList.setAdapter(adapter);
		lv_userList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(position);
				Intent intent=new Intent(MainActivity.this, DetailActivity.class);
				User user = new User();
				user._id = Integer.parseInt(String.valueOf(item.get("_id")));
				user.address = String.valueOf(item.get("address"));
				user.company = String.valueOf(item.get("company"));
				user.email = String.valueOf(item.get("email"));
				user.familyPhone = String.valueOf(item.get("familyphone"));
				user.mobilePhone = String.valueOf(item.get("mobilephone"));
				user.officePhone = String.valueOf(item.get("officephone"));
				user.otherContact = String.valueOf(item.get("othercontact"));
				user.position = String.valueOf(item.get("position"));
				user.remark = String.valueOf(item.get("remark"));
				user.name = String.valueOf(item.get("name"));
				user.zipCode = String.valueOf(item.get("zipcode"));
				user.imageID = Integer.parseInt(String.valueOf(item.get("imageid")));
				intent.putExtra("user", user);
				startActivityForResult(intent, 3); // ת����ϸ����
			}
		});
		lv_userList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(deleteId==null){
					deleteId=new ArrayList<Integer>();
				}
				HashMap<String, Object> map=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				Integer id=(Integer) map.get("_id");
				//Log.i("id", ""+id);
				RelativeLayout r=(RelativeLayout) arg1;
				ImageView markedView=(ImageView) r.getChildAt(2);
				if(markedView.getVisibility()==View.VISIBLE){
					markedView.setVisibility(View.GONE);
					deleteId.remove(id);
				}else{
					markedView.setVisibility(View.VISIBLE);
					deleteId.add(id);
				}
				return true;
			}
		});
		
		lv_userList.setDivider(this.getResources().getDrawable(R.drawable.listview_line));
		lv_userList.setDividerHeight(5);
		lv_userList.setSelector(R.drawable.listview_item_bg);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//Log.i("menu","key down");
//		if(keyCode==KeyEvent.KEYCODE_MENU){		//���²˵���ť
//			//Log.i("menu","menu down");
//			if(gv_buttom_menu==null){
//				loadButtomMenu();
//			}
//			if(gv_buttom_menu.getVisibility()==View.GONE){
//				gv_buttom_menu.setVisibility(View.VISIBLE);
//			}else if(gv_buttom_menu.getVisibility()==View.VISIBLE){
//				gv_buttom_menu.setVisibility(View.GONE);
//			}
//		}
		return true;
	}

	/** ���صײ��˵� */
	private void loadButtomMenu(){
		gv_buttom_menu=(GridView) this.findViewById(R.id.gv_button_menu);
		gv_buttom_menu.setBackgroundResource(R.drawable.channelgallery_bg);
		gv_buttom_menu.setNumColumns(5);
		gv_buttom_menu.setGravity(Gravity.CENTER);
		gv_buttom_menu.setVerticalSpacing(10);
		gv_buttom_menu.setHorizontalSpacing(10);
		
		ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("itemImage", R.drawable.menu_new_user);
		map.put("itemText", "����");
		data.add(map);
		
		map=new HashMap<String, Object>();
		map.put("itemImage", R.drawable.menu_search);
		map.put("itemText", "����");
		data.add(map);

		map=new HashMap<String, Object>();
		map.put("itemImage", R.drawable.menu_delete);
		map.put("itemText", "ɾ��");
		data.add(map);

		map=new HashMap<String, Object>();
		map.put("itemImage", R.drawable.controlbar_showtype_list);
		map.put("itemText", "�˵�");
		data.add(map);

		map=new HashMap<String, Object>();
		map.put("itemImage", R.drawable.menu_quit);
		map.put("itemText", "�˳�");
		data.add(map);
		
		SimpleAdapter adapter=new SimpleAdapter(this, data, R.layout.layout_item_menu, new String[]{"itemImage","itemText"}, new int[]{R.id.item_image,R.id.item_text});
		gv_buttom_menu.setAdapter(adapter);
		gv_buttom_menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				switch(arg2){
				case 0:		//���Ӱ�ť
					if(searchLinearout != null && searchLinearout.getVisibility()==View.VISIBLE) {
						searchLinearout.setVisibility(View.GONE);
					}
					//if(gv_buttom_menu.getVisibility() == View.VISIBLE) {
					//	gv_buttom_menu.setVisibility(View.GONE);
					//}
					Intent intent=new Intent(MainActivity.this, AddNewActivity.class);
					// 0��ӽ��� 
					startActivityForResult(intent, 0);
					break;
				case 1:		//������ť
					loadSearchLinearout();
					if(searchLinearout.getVisibility()==View.VISIBLE) {
						searchLinearout.setVisibility(View.GONE);
						updateList();
					} else {
						searchLinearout.setVisibility(View.VISIBLE);
						et_search.requestFocus();
						et_search.selectAll();
						//��ʾ�����
						InputMethodManager manager=(InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
						manager.showSoftInput(et_search, 0);
					}
					break;
				case 2:		//ɾ����ť
					if(searchLinearout != null && searchLinearout.getVisibility()==View.VISIBLE) {
						searchLinearout.setVisibility(View.GONE);
					}
					if(deleteId == null || deleteId.size() == 0) {
						Toast.makeText(MainActivity.this, "    û�б���κμ�¼\n����һ����¼���ɱ��", Toast.LENGTH_LONG).show();
					} else {
						new AlertDialog.Builder(MainActivity.this)
						.setTitle("ȷ��Ҫɾ����ǵ�"+deleteId.size()+"����¼��?")
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
						        DBHelper.getInstance(MainActivity.this).deleteMarked(deleteId);
						        //������ͼ
						        updateList();
						        deleteId.clear();
							}
						})
						.setNegativeButton("ȡ��", null)
						.show()	;
					}
					break;
				case 3:		//�˵���ť
					if(searchLinearout != null && searchLinearout.getVisibility()==View.VISIBLE) {
						searchLinearout.setVisibility(View.GONE);
					}
					loadMainMenuDialog();
					mainMenuDialog.show();
					break;
				case 4:		//�˳�
					System.exit(0);
					break;
				}
			}
		});
	}

	//���ز˵�
	private void loadMainMenuDialog(){
		if(mainMenuDialog==null){
			LayoutInflater li=getLayoutInflater();
			mainMenuView=li.inflate(R.layout.layout_main_menu_grid, null);
			mainMenuDialog=new AlertDialog.Builder(this).setView(mainMenuView).create();
			mainMenuGrid=(GridView) mainMenuView.findViewById(R.id.gridview);
			mainMenuGrid.setAdapter(getMenuAdapter());
			//����¼�
			mainMenuGrid.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					switch (arg2) {
					case 0:		//��ʾ����
						updateList();
						mainMenuDialog.dismiss();
						break;
					case 1:		//ɾ������
						AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
						builder.setTitle("�Ƿ�ɾ������");
						builder.setPositiveButton("ȷ��", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(MainActivity.this, "��Ҫɾ����", Toast.LENGTH_SHORT).show();
								mainMenuDialog.dismiss();
							}
						});
						builder.setNegativeButton("ȡ��", null);
						builder.show();
						break;
					case 2:		//��������
						
						break;
					case 3:		//��ԭ����
						break;
					case 4:		//���£�����˽�˿ռ�
						break;
					case 5:		//����
						mainMenuDialog.dismiss();
						break;
					default:
						break;
					}
				}
			});
		}
	}
	
	/** ���ͼƬGridView��������  */
	private SimpleAdapter getMenuAdapter(){
		// �˵����� 
		String[] main_menu_itemName = { "��ʾ����", "ɾ������", "��������", "��ԭ����", "����", "����"};
		//���˵�ͼƬ
		int[] main_menu_itemSource = {
								   R.drawable.showall,
								   R.drawable.menu_delete,
								   R.drawable.menu_backup,
								   R.drawable.menu_restore,
								   R.drawable.menu_fresh,
								   R.drawable.menu_return};
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < main_menu_itemName.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", main_menu_itemSource[i]);
			map.put("itemText", main_menu_itemName[i]);
			data.add(map);
		}
		return new SimpleAdapter(this, data, R.layout.layout_item_menu, new String[]{"itemImage","itemText"}, new int[]{R.id.item_image,R.id.item_text});
	}
	//����������
	private void loadSearchLinearout() {
		if (searchLinearout == null) {
			searchLinearout = (LinearLayout) findViewById(R.id.ll_search);
			et_search = (EditText) findViewById(R.id.et_search);
			et_search.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					//Log.i("text", s+" start:"+start+"  brfore:"+before+"  count:"+count);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,int after) {
					//Log.i("text", s+" start:"+start+"  count:"+count+" after:"+ after);
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					//Log.i("text", s.toString());
					String condition = s.toString();
					if (condition.equals("")) {
						mainLinearLayout.setBackgroundDrawable(null);
						updateList();;
					}else{
						updateSearchList(condition);
					}
				}
			});
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(deleteId!=null){
			deleteId.clear();
		}
		if(requestCode==0 || requestCode==3){//��ӽ���
			if(resultCode==0||resultCode==4){		//��ӳɹ�,ˢ���б�
				updateList();
			}
		}
	}
	
	/** �����б�����   */
	void updateList(){
		ArrayList<HashMap<String, Object>> data1=DBHelper.getInstance(this).getUserList();
		SimpleAdapter adapter=new SimpleAdapter(this, data1, R.layout.layout_list_item, new String[]{"imageid", "name","mobilephone"},new int[]{R.id.item_userimage,R.id.item_showname,R.id.item_showphone});
		lv_userList.setAdapter(adapter);
	}
	
	/** ���²����б�  */
	void updateSearchList(String condition){
		ArrayList<HashMap<String, Object>> data1=DBHelper.getInstance(this).getUsers(condition);
		SimpleAdapter adapter=new SimpleAdapter(this, data1, R.layout.layout_list_item, new String[]{"imageid", "name","mobilephone"},new int[]{R.id.item_userimage,R.id.item_showname,R.id.item_showphone});
		lv_userList.setAdapter(adapter); // �����Ϻõ�adapter����listview����ʾ���û���
		if (data1.size() == 0) {
			Drawable nodata_bg = getResources().getDrawable(R.drawable.nodata_bg);
			mainLinearLayout.setBackgroundDrawable(nodata_bg);
		} else {
			setTitle("���鵽 " + data1.size() + " ����¼");
			mainLinearLayout.setBackgroundDrawable(null);
		}
	}
}
