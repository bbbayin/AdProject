package miku.storage;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import miku.ad.adapters.FuseAdLoader;
import miku.ad.adapters.IAdAdapter;
import miku.ad.prophet.AdConfigBean;
import miku.ad.prophet.ProphetSrcBean;
import miku.firebase.BaseDataReportUtils;

import static miku.ad.adapters.FuseAdLoader.checkShouldBanSource;
import static miku.ad.adapters.FuseAdLoader.isAdmob;
import static miku.ad.adapters.FuseAdLoader.isMopub;

public class LocalDataSourceImpl {
    private volatile static LocalDataSourceImpl INSTANCE = null;

    public static LocalDataSourceImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSourceImpl();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private LocalDataSourceImpl() {
        //数据库Helper构建
    }

    public void saveUserName(String userName) {
        SPUtils.getInstance().put("UserName", userName);
    }

    public void saveAdConfigEntity(AdConfigBean config) {
        String entityJson = new Gson().toJson(config);
        SPUtils.getInstance().put("AdConfig", entityJson);
    }

    public void saveAdConfigTime(long time) {
        SPUtils.getInstance().put("AdConfigTime", time);
    }

    public long getAdConfigTime() {
        return SPUtils.getInstance().getLong("AdConfigTime", 0);
    }

    public AdConfigBean getAdConfigEntity() {
        AdConfigBean config;
        String entityJson = SPUtils.getInstance().getString("AdConfig");
        config = new Gson().fromJson(entityJson, AdConfigBean.class);
        return config;
    }

    public void saveProphetSrcEntity(List<ProphetSrcBean> srcList) {
        String entityJson = new Gson().toJson(srcList);
        SPUtils.getInstance().put("ProphetSrcList", entityJson);
    }

    public List<ProphetSrcBean> getProphetSrcEntity() {
        List<ProphetSrcBean> srcList;
        String entityJson = SPUtils.getInstance().getString("ProphetSrcList");
        srcList = new Gson().fromJson(entityJson, new TypeToken<List<ProphetSrcBean>>() {
        }.getType());
        return srcList;
    }

    public void saveDisableProphetAll(boolean config) {
        SPUtils.getInstance().put("ProphetAll", config);
    }

    public boolean getDisableProphetAll() {
        return SPUtils.getInstance().getBoolean("ProphetAll", false);
    }

    public void saveProphetPullTime(long config) {
        SPUtils.getInstance().put("ProphetPullTime", config);
    }

    public long getProphetPullTime() {
        return SPUtils.getInstance().getLong("ProphetPullTime", -1);
    }

    public String getProphetConfig() {
        return SPUtils.getInstance().getString("ProphetConfig", "");
    }

    public void saveProphetConfig(String config) {
        SPUtils.getInstance().put("ProphetConfig", config);
    }

    public void saveSlotAdRefreshTime(String slot, long time) {
        SPUtils.getInstance().put(slot + "SlotAdRefresh", time);
    }

    public long getSlotAdRefreshTime(String slot) {
        return SPUtils.getInstance().getLong(slot + "SlotAdRefresh", 0);
    }

    public void saveSlotProphetSrcEntity(String slot, List<ProphetSrcBean> srcList) {
        String entityJson = new Gson().toJson(srcList);
        SPUtils.getInstance().put(slot + "SlotProphetSrcList", entityJson);
    }

    public List<ProphetSrcBean> getSlotProphetSrcEntity(String slot) {
        List<ProphetSrcBean> srcList;
        String entityJson = SPUtils.getInstance().getString(slot + "SlotProphetSrcList");
        srcList = new Gson().fromJson(entityJson, new TypeToken<List<ProphetSrcBean>>() {
        }.getType());
        return srcList;
    }

    public void removeSlotProphetSrcEntity(String slot, ProphetSrcBean removeBean) {
        List<ProphetSrcBean> list = getSlotProphetSrcEntity(slot);
        for (ProphetSrcBean bean : list) {
            if (removeBean.getPkg().equals(bean.getPkg())) {
                list.remove(bean);
                saveSlotProphetSrcEntity(slot, list);
                return;
            }
        }
    }

    public String getAdClickNumKey(IAdAdapter ad) {
        String key = "";
        if (isAdmob(ad)) {
            key = "admob_click_num";
        } else if (isMopub(ad)) {
            key = "mopub_click_num";
        }
        return key;
    }

    public void addAdClickNum(IAdAdapter ad) {
        String key = getAdClickNumKey(ad);
        long num = getAdNumByKey(ad);
        num++;
        SPUtils.getInstance().put(key, num);
        if (FuseAdLoader.isAdmob(ad) && num >= 5) {
            BaseDataReportUtils.getInstance().reportExceedAdClick(ad);
            FuseAdLoader.setAdmobFree(true);
        }  else if (FuseAdLoader.isMopub(ad) && num >= 10) {
            BaseDataReportUtils.getInstance().reportExceedAdClick(ad);
        }
        checkShouldBanSource();
    }

    public Long getAdNumByKey(IAdAdapter ad) {
        String key = getAdClickNumKey(ad);
        if (TextUtils.isEmpty(key)) {
            return 0L;
        }
        return SPUtils.getInstance().getLong(key, 0);
    }

    public Long getAdNumByKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0L;
        }
        return SPUtils.getInstance().getLong(key, 0);
    }

    public void setAdNumByKey(String key, Long num) {
        SPUtils.getInstance().put(key, num);
    }

    public void setAdClickNum(IAdAdapter ad, int num) {
        String key = getAdClickNumKey(ad);
        SPUtils.getInstance().put(key, num);
    }

    public String getAdShowNumKey(IAdAdapter ad) {
        String key = "";
        if (isAdmob(ad)) {
            key = "admob_show_num";
        } else if (isMopub(ad)) {
            key = "mopub_show_num";
        }
        return key;
    }

    public void addAdShowNum(IAdAdapter ad) {
        String key = getAdShowNumKey(ad);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        long num = getAdShowNum(ad);
        num++;
        SPUtils.getInstance().put(key, num);
    }

    public Long getAdShowNum(IAdAdapter ad) {
        String key = getAdShowNumKey(ad);
        if (TextUtils.isEmpty(key)) {
            return 0L;
        }
        return SPUtils.getInstance().getLong(key, 0);
    }

    public void setAdShowNum(IAdAdapter ad, int num) {
        String key = getAdShowNumKey(ad);
        SPUtils.getInstance().put(key, num);
    }

    public String getAdReportDate() {
        return SPUtils.getInstance().getString("ad_report_date");
    }

    public void setAdReportDate(String date) {
        SPUtils.getInstance().put("ad_report_date", date);
    }
}
