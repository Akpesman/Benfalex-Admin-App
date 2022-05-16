package com.cleantec.benfalexadmin.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cleantec.benfalexadmin.Constant;
import com.cleantec.benfalexadmin.DataProviders.FcmNotificationsSender;
import com.cleantec.benfalexadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class UpdateOrderStatus extends AppCompatActivity {

    ImageView imgView;
    Button btnSelectImage, btnUploadImage;
    public static final int RESULT_LOAD_IMG = 121;
    StorageReference mStorageReference;
    Uri imageUri = null;
    ProgressDialog progressDialog;
    DatabaseReference orderRef,clientRef;
    String customerToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order_status);
        getSupportActionBar().setTitle("Upload Image");
        views();
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (imageUri == null) {
                    Toast.makeText(UpdateOrderStatus.this, "Select Image First", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    mStorageReference.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            if(Constant.scheduleAServiceDP.getOrderStatus().equalsIgnoreCase("pending"))
                                            {
                                                orderRef.child("orderStatus").setValue("pickedup");
                                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(customerToken, "Benfalex Package Delivery",
                                                        "Dear "+ Constant.scheduleAServiceDP.getFirstName() +" "+Constant.scheduleAServiceDP.getLastName()+" Order PickedUp Successfully with Order ID: " + Constant.scheduleAServiceDP.getOrderKey(),
                                                        getApplicationContext(), UpdateOrderStatus.this);
                                                notificationsSender.SendNotifications();
                                            }

                                            else if(Constant.scheduleAServiceDP.getOrderStatus().equalsIgnoreCase("pickedup"))
                                            {
                                                orderRef.child("orderStatus").setValue("delivered");
                                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(customerToken, "Benfalex Package Delivery",
                                                        "Dear "+ Constant.scheduleAServiceDP.getFirstName() +" "+Constant.scheduleAServiceDP.getLastName()+" Order Delivered Successfully with Order ID: " + Constant.scheduleAServiceDP.getOrderKey(),
                                                        getApplicationContext(), UpdateOrderStatus.this);
                                                notificationsSender.SendNotifications();
                                            }


                                            orderRef.child("pictureLink").setValue(uri+"").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UpdateOrderStatus.this, "An Error occured", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateOrderStatus.this, "Upload Again Please", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }

                            });
                }
            }
        });
    }

    private void views() {
        imgView = findViewById(R.id.image);
        btnSelectImage = findViewById(R.id.btn_select);
        btnUploadImage = findViewById(R.id.btn_upload);
        mStorageReference = FirebaseStorage.getInstance().getReference(System.currentTimeMillis()+"pic.jpg");
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        orderRef=FirebaseDatabase.getInstance().getReference("ServiceOrders").child(Constant.scheduleAServiceDP.getOrderKey());
        clientRef=FirebaseDatabase.getInstance().getReference("customers").child(Constant.scheduleAServiceDP.getUserId()).child("token");
        clientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerToken=snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgView.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(UpdateOrderStatus.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(UpdateOrderStatus.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}