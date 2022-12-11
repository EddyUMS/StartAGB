package com.startagb.startagb;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class CompressImg {
    public static byte[] getCompressedBitmapData(Bitmap bitmap, double maxFileSize, int maxDimensions) {
        Bitmap resizedBitmap;
        if (bitmap.getWidth() > maxDimensions || bitmap.getHeight() > maxDimensions) {
            resizedBitmap = getResizedBitmap(bitmap,
                    maxDimensions);
        } else {
            resizedBitmap = bitmap;
        }

        byte[] bitmapData = getByteArray(resizedBitmap);

        while (bitmapData.length > maxFileSize) {
            bitmapData = getByteArray(resizedBitmap);
        }
        return bitmapData;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,
                width,
                height,
                true);
    }

    private static byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,
                80,
                bos);

        return bos.toByteArray();
    }

}
