package hk.ypw.instabtbu;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

public class Guancang extends Activity {
	leftmenu leftmenu;
	SlidingMenu menu;
	Activity thisActivity = this;
	long uiId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guancang);

		leftmenu = new leftmenu(thisActivity, 6);
		menu = leftmenu.menu;
		MobclickAgent.updateOnlineConfig(this);
		uiId = Thread.currentThread().getId();
	}

	@Override
	public void onResume() {
		super.onResume();
		leftmenu.leftmenu_ui(6);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.btbu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Public_menu menu = new Public_menu();
		menu.thisActivity = thisActivity;
		menu.select(item);
		return false;
	}

	public void search(View view) {
		EditText kwEditText = (EditText) findViewById(R.id.guancang_edittext);
		String kw = kwEditText.getText().toString();
		if (kw.length() == 0) {
			Toast.makeText(thisActivity, "«Î ‰»Î≤È—Øƒ⁄»›", Toast.LENGTH_SHORT).show();
		} else {
			try {
				String searchUrl = "http://211.82.113.138:8080/search?xc=4&kw="
						+ URLEncoder.encode(kw, "UTF-8");
				Guancang_web.urlString = searchUrl;
				Intent intent = new Intent();
				intent.setClass(thisActivity, Guancang_web.class);
				thisActivity.startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
