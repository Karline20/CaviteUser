package coding.legaspi.caviteuser.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

public class ImageViewDataBindingAdapters {

    @BindingAdapter(value = {"bind:imageUrl", "bind:scale", "bind:withColorPalette"}, requireAll = false)
    public static void loadImage(ImageView view, String imageUrl, int scale, boolean withColorPalette) {
        if (TextUtils.isEmpty(imageUrl)) return;
        Picasso.get()
                .load(imageUrl)
                .fit().centerCrop()
                .into(view);
    }

    @BindingAdapter(value = {"bind:fileUrl", "bind:scale", "bind:withColorPalette"}, requireAll = false)
    public static void loadFile(ImageView view, File fileUrl, int scale, boolean withColorPalette) {
        if (TextUtils.isEmpty(fileUrl.getPath())) return;
        Activity activity = getActivityFromView(view);
        RequestCreator request = Picasso.get().load(fileUrl);

        if (!activity.isFinishing()) {
            ScaleType scaleType = ScaleType.fromOrdinal(scale);
            if (scaleType != null) {
                switch (scaleType) {
                    case FIT_CENTER:
                        request.fit();
                        break;
                    case CENTER_CROP:
                    default:
                        request.fit().centerCrop();
                        break;
                }
            }
            request.into(view);
        }
    }

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, String imageUri) {
        if (imageUri == null) {
            view.setImageURI(null);
        } else {
            view.setImageURI(Uri.parse(imageUri));
        }
    }

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, Uri imageUri) {
        view.setImageURI(imageUri);
    }

    @BindingAdapter("android:src")
    public static void setImageDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }

    @BindingAdapter({"bind:animation", "bind:animate"})
    public static void animate(ImageView view, Animation animation, boolean animate) {
        if (animate) {
            view.startAnimation(animation);
        } else {
            view.clearAnimation();
        }
    }

    public static AppCompatActivity getActivityFromView(View view) throws ClassCastException {
        Context context = view.getContext();

        if(context instanceof AppCompatActivity) return (AppCompatActivity) context;

        while(context instanceof ContextWrapper) {
            if(context instanceof AppCompatActivity) return (AppCompatActivity) context;
            context = ((ContextWrapper) context).getBaseContext();
        }

        throw new ClassCastException("Can't find Activity pickUpAddress view");
    }


}