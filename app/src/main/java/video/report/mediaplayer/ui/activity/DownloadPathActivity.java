package video.report.mediaplayer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import video.report.mediaplayer.MyApplication;
import video.report.mediaplayer.R;
import video.report.mediaplayer.firebase.Events;
import video.report.mediaplayer.firebase.FireBaseEventUtils;
import video.report.mediaplayer.preference.UserPreferences;
import video.report.mediaplayer.ui.dialog.DialogHelper;
import video.report.mediaplayer.util.DownloadEnvironment;
import video.report.mediaplayer.util.DownloadException;
import video.report.mediaplayer.util.DownloadFileUtils;
import video.report.mediaplayer.util.DownloadUtil;
import video.report.mediaplayer.util.PathResolver;
import video.report.mediaplayer.util.StorageUtils;


public class DownloadPathActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private StorageAdapter mStorageAdapter;
    private List<StorageUtils.SDCard> mMountedList;
    private TextView downloadFullPathView;

    @Inject
    UserPreferences UserSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //Then call setStatusBarColor.
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.color_tool_bar));
//        }

        MyApplication.getAppComponent().inject(this);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);



        setContentView(R.layout.activity_storage_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_download_location));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mListView = (ListView) findViewById(R.id.setting_lv);
        List<StorageUtils.SDCard> list = StorageUtils.getAllSdcardState(this);
        mMountedList = new ArrayList<StorageUtils.SDCard>();
        for (StorageUtils.SDCard sd : list) {
            if (sd.isMount()) {
                // 只显示已装载的SD卡
                mMountedList.add(sd);
            }
        }
        mStorageAdapter = new StorageAdapter(this, mMountedList);
        mListView.setAdapter(mStorageAdapter);
        mListView.setOnItemClickListener(this);

        downloadFullPathView = (TextView)findViewById(R.id.download_full_path);
        initData();
        FireBaseEventUtils.getInstance().report(Events.SETTINGS_LOCATION_PAGE_SHOW);
    }

    void initData() {
        String save_path = this.getString(R.string.download_default_save_path);
        save_path = String.format(save_path, getSettingDefaultSavePath());
        downloadFullPathView.setText(save_path);
    }

    @Override
    public void onResume() {
        super.onResume();
        String text = UserSp.getDownloadDirectory();
        if (text == null){
            return;
        }
        updateDownloadFullPathView(text);
        mStorageAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
    }

    //获取设置默认下载路径
    private Object getSettingDefaultSavePath() {
        String filePath = UserSp.getDownloadDirectory();
        filePath = TextUtils.isEmpty(filePath)
                ? PathResolver.getDownloadFileDir(UserSp.getDownloadDirectory()) : filePath;
        return filePath;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mMountedList != null && mMountedList.size() > 0) {
            for (int i = 0; i < mMountedList.size(); i++) {
                if (i != position)
                    continue;
                // 我们既然放出了黑科技，暂时只能顺着原产品思路走了。这里假定所有mounted的都是
                // on-board storage，所有removable的都属于external storage的范围
                // 实际上on-board storage中external storage的case只能靠后面的处理检测了
                StorageUtils.SDCard sd = mMountedList.get(position);
                if (sd != null)
                    if (DownloadEnvironment.ANDROID_4_4_2 <= DownloadEnvironment.getSdkVersion()
                            && sd.isRemoveable())
                        processExternalDir(sd);
                    else
                        showUserChooseSaveDownloadActivity(sd);

                break;
            }
        }
    }

    private void showUserChooseSaveDownloadActivity(StorageUtils.SDCard sd) {
        Intent intent = new Intent(this, SelectPathActivity.class);
        this.startActivity(intent);
//        intent.putExtra(DownloadSaveStorageView.KEY_CHOOSE_DOWNLOAD_PATH, sd.getPath());
//        ((Activity)this).setIntent(intent);
//        ((Activity)this).setContentView(R.layout.check_download_path_list);
    }

    // 4.4之后external storage定义产生变化，这里不应使用枚举方式
    // Context.getExternalFilesDirs。现在只能hardcode了，但风险是目录的权限及可用性是不能
    // 保证的。
    private void processExternalDir(StorageUtils.SDCard sd) {
        String dirString =
                DownloadFileUtils.getSaveDirPathBySDCardPath(sd.getPath(),
                        this.getApplicationContext());
        // 权限嗅探
        try {
            if (DownloadFileUtils.isDirPathCanWrite(dirString, null,
                    true)) {
                showUserTheOnlyChoice(dirString);
                return;
            }
        } catch (DownloadException e) {
            // case 目录路径被文件占坑，这里除非我们自己这么干或者root用户做对抗理论上不可能出现
            e.printStackTrace();
        }
        showUserCurrentSDCardCantWrite(dirString);
    }

    private void showUserTheOnlyChoice(final String dirString) {
        Context context = this;
        DialogHelper.showDialog(context, null, String.format(
                context.getString(R.string.download_4_4_sdcard_save_folder), dirString),
                context.getString(R.string.action_confirm), context.getString(R.string.action_cancel),
                new DialogHelper.Listener() {
                    @Override
                    public void onDialogClosed(int which) {
                        if (which == DialogHelper.POSITIVE_BUTTON) {
                            UserSp.setDownloadDirectory(dirString);
                            updateDownloadFullPathView(dirString);
                            mStorageAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void showUserCurrentSDCardCantWrite(final String dirString) {
        DialogHelper.showDialog(this, 0, R.string.download_sdcard_unavailable,
                R.string.action_confirm, R.string.action_cancel, null);
    }

    private void updateDownloadFullPathView(String pathString) {
        if(!TextUtils.isEmpty(pathString)){
            String save_path = this.getString(
                    R.string.download_default_save_path);
            save_path = String.format(save_path, pathString);
            downloadFullPathView.setText(save_path);
        }
    }

    private class StorageAdapter extends BaseAdapter {
        private Context mContext;
        private List<StorageUtils.SDCard> mItems;
        private LayoutInflater inflater;
        public StorageAdapter(Context ctx, List<StorageUtils.SDCard> items) {
            mContext = ctx;
            mItems = items;
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            if(mItems!=null){
                return mItems.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(mItems!=null){
                return mItems.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mItems == null || mItems.size() <= position)
                return null;

            if (convertView == null)
                convertView = inflater.inflate(R.layout.setting_storage_item, null);

            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.relative_lyt);
            TextView storageType = (TextView) convertView.findViewById(R.id.storage_type);
            ImageView storageTypeImage = (ImageView) convertView.findViewById(R.id.storage_type_image);
            ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.storage_pb);
            TextView space = (TextView) convertView.findViewById(R.id.storage_space);
            ImageView storageTypeSelected = (ImageView) convertView.findViewById(R.id.storage_type_selected);

//            StateListDrawable stateListDrawable;
//            if (position == 0) {
//                if (getCount() == 1) {
//                    stateListDrawable = (StateListDrawable) mContext.getResources().getDrawable(
//                            R.drawable.setting_item_whole_selector);
//                } else {
//                    stateListDrawable = (StateListDrawable) mContext.getResources().getDrawable(
//                            R.drawable.setting_item_top_selector);
//                }
//            } else if (position == mItems.size() - 1) {
//                stateListDrawable = (StateListDrawable) mContext.getResources().getDrawable(
//                        R.drawable.setting_item_bottom_selector);
//            } else {
//                stateListDrawable = (StateListDrawable) mContext.getResources().getDrawable(
//                        R.drawable.setting_item_middle_selector);
//            }
//            ViewCompat.setBackground(linearLayout, stateListDrawable);

            StorageUtils.SDCard sd = mItems.get(position);
            if (sd != null) {
                String path = sd.getPath();
                String text_available = mContext.getString(R.string.s_download_text_availableSpace);
                String available = DownloadUtil.showSize(DownloadUtil.getAvailableSize(path));
                String text_total = mContext.getString(R.string.s_total_available);
                String total = DownloadUtil.showSize(DownloadUtil.getTotalSize(path));
                int per = DownloadUtil.getUsedSizePer(path);
                per = per < 0 ? 0 : (per > 100 ? 100 : per);

                Drawable drawable;
                if(sd.getName(mContext).equals(mContext.getString(R.string.s_download_innerSDCard))){
                    drawable = mContext.getResources().getDrawable( R.drawable.download_internal_storage1);
                }else{
                    drawable = mContext.getResources().getDrawable( R.drawable.download_extra_storage1);
                }
                storageTypeImage.setImageDrawable(drawable);

                storageType.setText(sd.getName(mContext));
                progressBar.setProgress(per);
                space.setText(String.format("%s %s , %s %s", text_available, available, text_total, total));
                Boolean value = path.equals(PathResolver.getSDCardPathByFile(getSettingDefaultSavePath().toString()));
                if (value) {
                    storageTypeSelected.setVisibility(View.VISIBLE);
                } else {
                    storageTypeSelected.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

    }
}
