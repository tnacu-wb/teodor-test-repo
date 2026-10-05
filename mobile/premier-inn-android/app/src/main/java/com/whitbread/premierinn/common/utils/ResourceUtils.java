package com.whitbread.premierinn.common.utils;


import static android.content.pm.PackageManager.GET_SIGNATURES;
import static android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES;
import static android.content.pm.PackageManager.NameNotFoundException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.text.TextPaint;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.service.LogService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okio.ByteString;

public final class ResourceUtils {

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    public static Bitmap getBitmapWithText(Context context, @DrawableRes int drawableResId, @NonNull String text) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId).copy(Bitmap.Config.ARGB_8888, true);

        drawTextOnBitmap(context, bitmap, text, 0.5f, 0.22f,
                R.color.grey_dark,
                R.dimen.map_indicator_text_size,
                R.font.proxima_nova_semibold);

        return bitmap;
    }

    public static Bitmap getHotelPinBitmap(Context context, @DrawableRes int drawableResId, @NonNull String text) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId).copy(Bitmap.Config.ARGB_8888, true);
        drawTextOnBitmap(context, bitmap, text, 0.5f, 0.5f,
                R.color.white,
                R.dimen.map_prices_text_size,
                R.font.proxima_nova_semibold);
        return bitmap;
    }

    private static void drawTextOnBitmap(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull String text,
                                         float textCenterRatioX, float textCenterRatioY,
                                         @ColorRes int color, @DimenRes int size, @FontRes int typeface) {
        Canvas canvas = new Canvas(bitmap);
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(context, color));
        if (typeface != 0) {
            textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.proxima_nova_semibold));
        }
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(size));
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        float centerX = Math.round(bitmap.getWidth() * textCenterRatioX);
        float centerY = Math.round(bitmap.getHeight() * textCenterRatioY);

        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        float textOffsetX = bounds.width() * 0.5f;
        float textOffsetY = bounds.height() * 0.5f;


        canvas.drawText(text, centerX - textOffsetX, centerY + textOffsetY, textPaint);
    }

    @NonNull
    @SuppressLint("PackageManagerGetSignatures")
    public static String getSignature(@NonNull Context context, @NonNull LogService logService) {
        try {
            Signature signatures[] = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), GET_SIGNING_CERTIFICATES);
                signatures = packageInfo.signingInfo.getApkContentsSigners();
            } else {
                PackageInfo  packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), GET_SIGNATURES);
                signatures = packageInfo.signatures;
            }

            final MessageDigest md = MessageDigest.getInstance("SHA");
            if (signatures != null && signatures.length != 0 && signatures[0] != null) {
                md.update(signatures[0].toByteArray());
                return ByteString.of(md.digest()).hex();
            } else {
                logService.logInfo(new GetSignatureFailed(), "PackageManagerGetSignatures", "App signature issue");
            }
        } catch (NameNotFoundException | NoSuchAlgorithmException e) {
            logService.logInfo(e, "PackageManagerGetSignatures", "App signature issue");
        }
        return "";
    }

    private static class GetSignatureFailed extends Throwable { }
}
