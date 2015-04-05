package hk.ypw.instabtbu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint({ "SetJavaScriptEnabled", "HandlerLeak" })
public class Guancang_web extends SwipeBackActivity {
	static String urlString = "";
	Activity thisActivity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guancang_web);

		WebView webview = (WebView) findViewById(R.id.guancang_webview);
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new MyWebViewClient());
		webview.loadUrl(urlString);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		WebView webview = (WebView) findViewById(R.id.guancang_webview);
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			loadurlString = url;
			executorService.submit(addimgRunnable);
			return true;
		}

	}

	String loadurlString = "";
	String jsString = "";
	Runnable addimgRunnable = new Runnable() {
		@Override
		public void run() {
			String result = "";
			result = GET(loadurlString);
			try {
				String isbnString = zhongjian(result, "ISBN/价格：</strong>", ":");
				System.out.println("isbn=" + isbnString);
				result = httpsGET("https://api.douban.com/v2/book/isbn/"
						+ isbnString);

				String srcString = zhongjian(result, "image\":\"", "\"");
				srcString = srcString.replace("\\", "");
				String javascriptString = "javascript:var aEle=document.body.getElementsByTagName('strong');"
						+ "var strong=aEle[aEle.length-1];"
						+ "var s=document.createElement('img');"
						+ "var br=document.createElement('br');"
						+ "s.src='"
						+ srcString
						+ "';"
						+ "strong.appendChild(br);"
						+ "strong.appendChild(s);";
				jsString = javascriptString;

				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			} catch (Exception e) {

			}
		}
	};

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				if (msg.what == 1) {
					WebView webview = (WebView) findViewById(R.id.guancang_webview);
					webview.loadUrl(jsString);
				}
			} catch (Exception e) {

			}
		}
	};

	public String zhongjian(String text, String textl, String textr) {
		return zhongjian(text, textl, textr, 0);
	}

	public String zhongjian(String text, String textl, String textr, int start) {
		try {
			int left = text.indexOf(textl, start);
			int right = text.indexOf(textr, left + textl.length());
			return text.substring(left + textl.length(), right);
		} catch (Exception e) {
			System.out.println("zhongjian:error:" + e);
			return "";
		}
	}

	private ExecutorService executorService = Executors.newCachedThreadPool();// 线程池
	static HttpClient myClient = new DefaultHttpClient();

	public String GET(String url) {
		String result = "";
		System.out.println(url);
		HttpGet hGet = new HttpGet(url);
		try {
			myClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			// 请求超时
			myClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					30000);
			// 读取超时
			HttpResponse hResponse;
			hResponse = myClient.execute(hGet);
			if (hResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(hResponse.getEntity());
				// result = new String(result.getBytes("ISO_8859_1"), "gbk");
				// 转码
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return (result);
	}

	public String httpsGET(String url) {
		String result = "";
		System.out.println(url);
		HttpGet hGet = new HttpGet(url);
		try {
			HttpClient client = SSLSocketFactoryEx.getNewHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			// 请求超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					30000);
			// 读取超时
			HttpResponse hResponse;
			hResponse = client.execute(hGet);
			if (hResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(hResponse.getEntity());
				// result = new String(result.getBytes("ISO_8859_1"), "gbk");
				// 转码
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return (result);
	}

}
