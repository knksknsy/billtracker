package de.hdm.project.billtracker.helpers;

import android.app.Activity;
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

    private File imageFile;
    private String imageName;
    private String imagePath;


    public ImageHelper() {

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

    public void createTempImageFile(Activity activity) {
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
        String outputPath = imageFile.toString().replace(imageName, "") + category;

        File outDir = new File(outputPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        InputStream in = null;
        OutputStream out = null;

        String outFile = null;

        try {
            in = new FileInputStream(imageFile.toString());
            outFile = outputPath + "/" + imageName;
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

}
