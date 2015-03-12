package com.example.iguest.myapplication;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.Date;

        import android.app.Activity;
        import android.content.Context;
        import android.content.ContextWrapper;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;


public class MainActivity extends Activity {

    TextView textTargetUri;
    ImageView targetImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri pathy;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    Bitmap nextswag;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonLoadImage = (Button)findViewById(R.id.loadimage);
        textTargetUri = (TextView)findViewById(R.id.targeturi);
        targetImage = (ImageView)findViewById(R.id.targetimage);

        //gallery
        buttonLoadImage.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }});

        //camera
        Button takeimage = (Button)findViewById(R.id.takeimage);
        takeimage.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0){
                dispatchTakePictureIntent();
            }
        });

        //next from intent
        Button next = (Button)findViewById(R.id.nextimage);

        next.setOnClickListener(new Button.OnClickListener(){


            @Override
            public void onClick(View arg0){
                Intent nextActivity = new Intent(MainActivity.this, Next.class);
                // add data to be passed to next activity

                nextActivity.putExtra("path", pathy);
                if (nextActivity.resolveActivity(getPackageManager()) != null) {
                    startActivity(nextActivity);
                }

            }
        });


        //next from internal directory
        Button next2 = (Button)findViewById(R.id.next2);

        next2.setOnClickListener(new Button.OnClickListener(){


            @Override
            public void onClick(View arg0){
                Intent nextActivity = new Intent(MainActivity.this, next2.class);
                saveToInternalStorage(nextswag);

                if (nextActivity.resolveActivity(getPackageManager()) != null) {
                    startActivity(nextActivity);
                }

            }
        });
    }

     //for gallery
   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            pathy = targetUri;
            textTargetUri.setText(targetUri.toString());
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                nextswag = bitmap;
                targetImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            targetImage.setImageBitmap(imageBitmap);
        }
    } */

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //works
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //idk why this doesn't work :(
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }*/
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            Log.v("asdf", "it ran");
            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 1, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}