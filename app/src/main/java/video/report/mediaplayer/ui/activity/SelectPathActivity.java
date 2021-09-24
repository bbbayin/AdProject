package video.report.mediaplayer.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import video.report.mediaplayer.MyApplication;
import video.report.mediaplayer.R;
import video.report.mediaplayer.preference.UserPreferences;
import video.report.mediaplayer.ui.dialog.DialogHelper;
import video.report.mediaplayer.util.DownloadException;
import video.report.mediaplayer.util.DownloadFileUtils;
import video.report.mediaplayer.util.DownloadUtil;


public class SelectPathActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView mListView;
    private ListAdapter mListAdapter;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mTitleBarHolder;
    private Button mOkButton;
    private Button mCancelButton;

    private String mCurrentFolder;

    final private HashSet<String> mHiddenDirectories = new HashSet<>(Arrays.asList(
            ".", "..", "lost+found"));

    @Inject
    UserPreferences UserSp;

    FileFilter mDirFilterFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            return (pathname.isDirectory() &&
                    (!name.startsWith(".") && !mHiddenDirectories.contains(name)));
        }
    };

    private Handler mHandler = new Handler();

    class ListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        Context mContext;
        List<File> mFolderList;

        class ViewHolder {
            TextView nameView;
            TextView msgView;
        }

        ListAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);

            mFolderList = new ArrayList<>();
        }

        public void update(List<File> folders) {
            mFolderList.clear();
            mFolderList.addAll(folders);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFolderList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFolderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            final File folder = mFolderList.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.location_selection_list_item, parent, false);
                vh = new ViewHolder();
                vh.nameView = (TextView) convertView.findViewById(R.id.nameTextView);
                vh.msgView = (TextView) convertView.findViewById(R.id.msgTextView);
                convertView.setTag(vh);
            }
            vh = (ViewHolder) convertView.getTag();
            vh.nameView.setText(folder.getName());
            final TextView msgView = vh.msgView;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    int childCount = 0;
                    if (folder.isDirectory()) {
                        File[] listFiles = folder.listFiles(mDirFilterFilter);
                        if (listFiles != null) {
                            childCount = listFiles.length;
                        }
                    }
                    msgView.setText(String.format(mContext.getResources().getString(R.string.folder_msg_fmt),
                            DateFormat.format("yyyy.MM.dd", folder.lastModified()), childCount));
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCurrentFolder(folder.getPath());
                }
            });
            return convertView;
        }
    }

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

        setContentView(R.layout.activity_location_selection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_download_location));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mListView = (ListView) findViewById(R.id.folder_list_view);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.folder_title_bar);
        mTitleBarHolder = (LinearLayout) findViewById(R.id.title_bar_holder);
        mOkButton = (Button) findViewById(R.id.ok_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);

        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        mListAdapter = new ListAdapter(this);
        mListView.setAdapter(mListAdapter);

        if (isExternalSDCard(UserSp.getDownloadDirectory()))
            setCurrentFolder(DownloadUtil.getSDCardPath(this, true));
        else
            setCurrentFolder(UserSp.getDownloadDirectory());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onClick(View v) {
        if (v == mOkButton) {
            try {
                if (DownloadFileUtils.isDirPathCanWrite(mCurrentFolder, null, true)) {
                    setCurrentDirAsDefaultDownloadPath();
                    finish();
                    overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
                    return;
                }
            } catch (DownloadException e) {
                // case 目录路径被文件占坑，这里是先见再选，理论上不可能出现
                e.printStackTrace();
            }
            showUserCurrentDirCantWrite();
            return;

        } else if (v == mCancelButton) {
            finish();
        }
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
    }

    private boolean isExternalSDCard(String path) {
//        String externPath = DownloadUtil.getSDCardPath(KBrowserEngine.getApplicationContext(), false);
//        return (path != null && !externPath.equals("") && path.startsWith(externPath));
        // external SDCard may not exist, use internal storage instead
        String internalPath = DownloadUtil.getSDCardPath(this, true);
        return (path != null && !path.startsWith(internalPath));
    }

    private void setCurrentDirAsDefaultDownloadPath() {
        UserSp.setDownloadDirectory(mCurrentFolder);
    }

    private void showUserCurrentDirCantWrite() {
        DialogHelper.showDialog(this, 0, R.string.download_save_folder_miss_permission,
                R.string.action_return, 0, null);
    }

    private void setCurrentFolder(String folder) {
        mCurrentFolder = folder;
        File file = new File(folder);
        File[] listFiles = file.listFiles(mDirFilterFilter);
        List<File> files;
        if (listFiles != null) {
            Arrays.sort(listFiles);
            files = Arrays.asList(listFiles);
        } else {
            files = new ArrayList<File>();
        }
        mListAdapter.update(files);
        updateTitleBar(file);
    }

    private void updateTitleBar(File file) {
        List<View> viewList = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        String internalSDcard = DownloadUtil.getSDCardPath(this, true);

        while (file != null) {
            View view = inflater.inflate(R.layout.location_selection_title_item, null);
            TextView tv = (TextView) view.findViewById(R.id.name_tv);
            boolean isInternalSDcard = file.getPath().equals(internalSDcard);
            if (file.getParentFile() == null || isInternalSDcard)
                tv.setText(getResources().getText(R.string.root_folder));
            else
                tv.setText(file.getName());
            view.setTag(file);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = (File) v.getTag();
                    setCurrentFolder(file.getPath());
                }
            });
            viewList.add(0, view);

            if (isInternalSDcard)
                break;

            file = file.getParentFile();
        }

        mTitleBarHolder.removeAllViews();
        for (View v : viewList)
            mTitleBarHolder.addView(v);
        mHorizontalScrollView.postDelayed(new Runnable() {
            public void run() {
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
    }
}
