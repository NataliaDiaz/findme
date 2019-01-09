/**
 * 
 */
package com.wbhackathon.friendfinder;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author makoskin
 *
 */
public class FriendFinderCommunicator {



	private static final String BASE_URL = "http://albin.abo.fi:4242/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
		Log.v("FriendFinderCommunication.java", "Hello, I'm sending a message");
	}


	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;

	}

}
