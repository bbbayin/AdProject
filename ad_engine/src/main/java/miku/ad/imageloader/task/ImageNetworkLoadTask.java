package miku.ad.imageloader.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import miku.ad.imageloader.ImageLoadTask;
import miku.ad.imageloader.NetworkFetchUtils;


public class ImageNetworkLoadTask extends ImageLoadTask {

	private Context context;
	
	public ImageNetworkLoadTask(Handler handler, String url, Context context) {
		super(handler, url);
		this.context = context;
	}

	@Override
	protected Bitmap load(String url) {
		Bitmap bitmap = NetworkFetchUtils.fetch(context, url);
		return bitmap;
	}

}
