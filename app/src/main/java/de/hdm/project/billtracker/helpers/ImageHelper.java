package de.hdm.project.billtracker.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHelper {

    private Activity activity;
    private File imageFile;
    private String imageName;
    private String imagePath;
    private String thumbnailPath;

    public ImageHelper(Activity activity) {
        this.activity = activity;
    }

    public void createTempImageFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String suffix = ".jpg";
            File image = File.createTempFile(
                    imageFileName,   // prefix
                    suffix,          // suffix
                    storageDir       // directory
            );

            // Save a file: path for use with ACTION_VIEW intents
            imageName = image.getAbsolutePath().replace(image.getParent() + "/", "");
            imageFile = image;
        } catch (IOException e) {
            e.printStackTrace();
            imageFile = null;
        }
    }

    public void saveImageOnDevice(String category) {
        String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Billtracker/" + category;

        File outDir = new File(outputPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        InputStream in = null;
        OutputStream out = null;

        String outFile = null;

        try {
            in = new FileInputStream(imageFile);
            outFile = outputPath + "/" + imageName;
            System.out.println("OUTPUT FILE: " + outFile);
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(imageFile.toString()).delete();

            imagePath = outFile;
            this.saveThumbnail();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public String imageToBase64() {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap Base64ToImage(String encoding) {
        byte[] decodedString = Base64.decode(encoding, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void saveThumbnail() {
        Bitmap thumbnail;
        FileOutputStream out = null;
        FileInputStream fis = null;
        try {
            final int THUMBNAIL_SIZE = pxFromDp(115);
            fis = new FileInputStream(imagePath);
            thumbnail = BitmapFactory.decodeStream(fis);

            thumbnail = Bitmap.createScaledBitmap(thumbnail,
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytearroutstream);

            File tmpDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/tmpImages");
            thumbnailPath = tmpDir + "/" + imageName;
            out = new FileOutputStream(tmpDir + "/" + imageName);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private int pxFromDp(float dp) {
        return Math.round(dp * activity.getBaseContext().getResources().getDisplayMetrics().density);
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

}
