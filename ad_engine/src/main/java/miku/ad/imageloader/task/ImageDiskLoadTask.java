
package miku.ad.imageloader.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import miku.ad.imageloader.DiskCacheUtils;
import miku.ad.imageloader.ImageLoadTask;
import miku.ad.imageloader.ProgressRecorder;


public class ImageDiskLoadTask extends ImageLoadTask {

    private Context context;

    public ImageDiskLoadTask(Handler handler, String url, Context context) {
        super(handler, url);
        this.context = context;
    }

    @Override
    protected Bitmap load(String url) {
        ProgressRecorder.getInstance().setProgress(url, 1.0f);
        return DiskCacheUtils.getBitmap(context, url);
    }

}
