package com.example.q_thjen.mmessage.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.q_thjen.mmessage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference dataRef;
    private StorageReference storageRef;

    private CircleImageView circleImage;
    private TextView tvName, tvStatus;
    private Button btChangeImage, btChangeStatus;

    private String status;

    private static final int GALLERY_PICKER = 1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        FindView();

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();

        String uid = user.getUid();
        dataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        dataRef.keepSynced(true); // TODO: tự động tải dữ liệu lưu vào ổ cứng đt khi có kết nối mạng

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                tvName.setText(name);
                tvStatus.setText(status);

                if ( !image.equals("user image") ) { // user đã được set image

                    Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user).into(circleImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.user).into(circleImage);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingActivity.this, StatusActivity.class);
                intent.putExtra("mstatus", status);
                startActivity(intent);

            }
        });

        btChangeImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {

                /** start take image activity **/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICKER);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** crop image **/
        if ( requestCode == GALLERY_PICKER && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);

        }

        /** add image into storage firebase **/
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please wait while we upload and progress the image.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                final File filePath = new File(resultUri.getPath());

                try {
                    /** TODO giảm size ảnh để add vào thumb với compressor library **/
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(filePath);

                    /** convert bitmap to byte array **/
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                final String muid = user.getUid();
                /** add storage **/
                StorageReference fileImage = storageRef.child("profile_images").child(muid + ".jpg");
                final StorageReference thumb_filepath = storageRef.child("profile_images").child("thumbs").child(muid + ".jpg");

                /** TODO: add image into storage **/
                fileImage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if ( task.isSuccessful() ) {

                            final String linkImage = task.getResult().getDownloadUrl().toString();

                            /** TODO: add thumb into storage **/
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    String thumb_downloadUrl = task.getResult().getDownloadUrl().toString();

                                    if ( task.isSuccessful() ) {

                                        /** TODO: Add url thumb and image into database **/
                                        Map update = new HashMap();
                                        update.put("image", linkImage);
                                        update.put("thumb_image", thumb_downloadUrl);

                                        dataRef.updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if ( task.isSuccessful() ) {
                                                    progressDialog.dismiss();
                                                    TastyToast.makeText(SettingActivity.this, "Success loading", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                                }

                                            }
                                        });

                                    } else {
                                        TastyToast.makeText(SettingActivity.this, "Error in loading", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                        progressDialog.dismiss();
                                    }

                                }
                            });

                        } else {
                            TastyToast.makeText(SettingActivity.this, "Error in loading", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            progressDialog.dismiss();
                        }

                    }
                });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }

    }

    private void FindView() {

        circleImage = findViewById(R.id.st_image);
        tvName = findViewById(R.id.st_name);
        tvStatus = findViewById(R.id.st_status);
        btChangeImage = findViewById(R.id.st_btchangeimage);
        btChangeStatus = findViewById(R.id.st_btchangestatus);

    }

    public static String ramdomString() {

        Random random = new Random();
        StringBuilder randomString = new StringBuilder();
        int randomLength = random.nextInt(10);
        char tempChar;
        for ( int i = 0; i < randomLength; i++ ) {
            tempChar = (char) (random.nextInt(96) + 32);
            randomString.append(tempChar);
        }

        return randomString.toString();
    }

    private String getRes(int numberString) {

        return getResources().getString(numberString);
    }

}
