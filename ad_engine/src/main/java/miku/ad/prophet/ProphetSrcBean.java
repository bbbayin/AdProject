/**
 * Copyright 2020 bejson.com
 */
package miku.ad.prophet;

import android.content.res.AssetManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import miku.ad.adapters.FuseAdLoader;


public class ProphetSrcBean implements Serializable {
    final String assetPath = "file:///android_asset/";

    @SerializedName("pkg")
    private String pkg;
    @SerializedName("title")
    private String title;
    @SerializedName("desprion")
    private String desprion;
    @SerializedName("image")
    private String image;
    @SerializedName("icon")
    private String icon;
    @SerializedName("type")
    private String type;
    @SerializedName("link")
    private String link;
    @SerializedName("button")
    private String button;

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getPkg() {
        return pkg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDesprion(String desprion) {
        this.desprion = desprion;
    }

    public String getDesprion() {
        return desprion;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public void showInImageView(ImageView imageView, String imagePath) {
        if (isFileExists(imagePath)) {
            String realPath = assetPath + imagePath;
            Glide.with(imageView.getContext())
                    .load(realPath)
                    .into(imageView);
        }
//        else {
//            String realPath = baseUrlNormal + imagePath;
//            Glide.with(imageView.getContext())
//                    .load(realPath)
//                    .into(imageView);
//        }
    }

    private boolean isFileExists(String filename) {
        AssetManager assetManager = FuseAdLoader.getContext().getAssets();
        InputStream is = null;
        try {
            try {
                is = assetManager.open(filename);
                return true;
            } catch (IOException ex) {
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        catch (Exception e){
        }
        return false;
    }

    public void preload(String imagePath) {
        if (isFileExists(imagePath)) {
            return;
        } else {
//            String realPath = baseUrlNormal + imagePath;
//            Glide.with(FuseAdLoader.getContext())
//                    .load(realPath)
//                    .preload();
        }
    }
}