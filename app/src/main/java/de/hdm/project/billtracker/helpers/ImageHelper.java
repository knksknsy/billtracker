package de.hdm.project.billtracker.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdm.project.billtracker.models.Bill;

/**
 * Helper class for creating images and manipulate its location.
 */
public class ImageHelper {

    private Activity activity;

    private File imageFile;
    private String imageName;
    private String imagePath;
    private String thumbnailPath;

    public ImageHelper(Activity activity) {
        this.activity = activity;
    }

    public ImageHelper(Activity activity, Bill bill) {
        this.activity = activity;
        this.imageFile = new File(bill.getImagePath());
        this.imagePath = bill.getImagePath();
        this.thumbnailPath = bill.getThumbnailPath();
        this.imageName = imagePath.replace(imageFile.getParent() + "/", "");
    }

    /**
     * Temporarily save captured image by ImageReader on device
     *
     * @param reader
     */
    public void createImage(ImageReader reader) {
        createImageFile();
        Image image = null;
        Bitmap bitmap = null;
        OutputStream output = null;
        try {
            image = reader.acquireLatestImage();

            bitmap = imageToBitmap(image);
            bitmap = rotateBitmap(bitmap, 90);

            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                byte[] bytes = stream.toByteArray();

                output = new FileOutputStream(imageFile);
                output.write(bytes);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (image != null) {
                image.close();
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * Create a temporary file without content
     */
    private void createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/tmpImages");
            String suffix = ".jpg";
            File image = File.createTempFile(
                    imageFileName,   // prefix
                    suffix,          // suffix
                    storageDir       // directory
            );

            imageName = image.getAbsolutePath().replace(image.getParent() + "/", "");
            imageFile = image;
        } catch (IOException e) {
            e.printStackTrace();
            imageFile = null;
        }
    }

    /**
     * Convert an Image object to a Bitmap object
     *
     * @param image
     * @return
     */
    private Bitmap imageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }

    /**
     * Rotate a Bitmap by an angle
     *
     * @param source
     * @param angle
     * @return
     */
    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Move an image into its specified category directory
     *
     * @param category
     */
    public void moveToPicturesDir(String category) {
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
            this.createThumbnail();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /**
     * Create a thumbnail image
     */
    public void createThumbnail() {
        Bitmap thumbnail;
        FileOutputStream out = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagePath);
            thumbnail = BitmapFactory.decodeStream(fis);

            if (thumbnail != null) {
                final int[] thumbnailDimensions = getThumbnailDimensions(thumbnail.getWidth(), thumbnail.getHeight());

                thumbnail = Bitmap.createScaledBitmap(thumbnail,
                        thumbnailDimensions[0], thumbnailDimensions[1], false);

                ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytearroutstream);

                File tmpDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/thumbnails");
                thumbnailPath = tmpDir + "/" + imageName;
                out = new FileOutputStream(tmpDir + "/" + imageName);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
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

    /**
     * Check if a given file is existent in the provided path
     *
     * @param path
     * @return
     */
    public static boolean fileExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Delete an image
     *
     * @param path
     */
    public static void deleteFromPicturesDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            dir.delete();
        }
    }

    /**
     * Create a category folder in pictures directory holding images of a specific category
     *
     * @param path
     */
    public static void createCategoryDir(String path) {
        File file = new File(path);
        File categoryDir = new File(file.getParent());

        if (!categoryDir.exists()) {
            categoryDir.mkdirs();
        }
    }

    /**
     * Delete a category folder with all its files
     *
     * @param category
     */
    public static void deleteCategoryDir(String category) {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Billtracker/" + category);

        if (path.isDirectory()) {
            String[] children = path.list();
            for (int i = 0; i < children.length; i++) {
                new File(path, children[i]).delete();
            }
            path.delete();
        }
    }

    /**
     * Delete temporarily created images which were not saved
     */
    public void deleteTmpImage() {
        File path = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/tmpImages");

        if (path.isDirectory()) {
            String[] children = path.list();
            for (int i = 0; i < children.length; i++) {
                new File(path, children[i]).delete();
            }
            path.delete();
        }
    }

    /**
     * Converts dp into pixel value
     *
     * @param dp
     * @return
     */
    private int pxFromDp(float dp) {
        return Math.round(dp * activity.getBaseContext().getResources().getDisplayMetrics().density);
    }

    /**
     * Calculate proper dimension for a thumbnail
     *
     * @param width
     * @param height
     * @return
     */
    private int[] getThumbnailDimensions(int width, int height) {
        int[] dimension = new int[2];
        int maxDimension = pxFromDp(115);
        if (width > height) {
            // landscape
            float ratio = (float) width / maxDimension;
            width = maxDimension;
            height = (int)(height / ratio);
            dimension[0] = width;
            dimension[1] = height;
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxDimension;
            height = maxDimension;
            width = (int)(width / ratio);
            dimension[0] = width;
            dimension[1] = height;
        } else {
            // square
            height = maxDimension;
            width = maxDimension;
            dimension[0] = width;
            dimension[1] = height;
        }
        return dimension;
    }

    public static Uri getImagePathAsUri(String path) {
        return Uri.fromFile(new File(path));
    }

    public static File getImageFile(String path) {
        return new File(path);
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
