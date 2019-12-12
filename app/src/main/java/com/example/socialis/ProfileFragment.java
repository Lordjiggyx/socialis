package com.example.socialis;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

FirebaseAuth firebaseAuth ;
FirebaseUser user;
FirebaseDatabase firebaseDatabase;
DatabaseReference reference;

//Storage
    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_Imgs/";

    ImageView avater ,coverIV;
    TextView nameTv , EmailTv , PhoneTv;
    FloatingActionButton fab;

    ProgressDialog pd;

    private  static final int CAMERA_REQUEST_CODE = 100;
    private  static final int STORAGE_REQUEST_CODE = 200;
    private  static final int IMAGE_PICK_CAMERA_CODE = 300;
    private  static final int IMAGE_PICK_GALLERY_CODE = 400;

    String cameraPermission[];

    String storagePermission[];

    Uri image_url;

    //for checking profile or cover photo
    String profileOrCover;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();

        cameraPermission = new String[]
                {
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
        storagePermission = new String[]
                {
                      Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

        avater = view.findViewById(R.id.avatar);
        nameTv = view.findViewById(R.id.NameTv);
        EmailTv =view.findViewById(R.id.emailTv);
        PhoneTv = view.findViewById(R.id.PhoneTv);
        fab = view.findViewById(R.id.fab);
        coverIV = view.findViewById(R.id.coverIv);
        pd = new ProgressDialog(getActivity());
        Query query = reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    nameTv.setText(name);
                    EmailTv.setText(email);
                    PhoneTv.setText(phone);
                   try
                   {
                       Picasso.get().load(image).into(avater);
                   }
                   catch (Exception e)
                   {
                       Picasso.get().load(R.drawable.ic_user_black).into(avater);
                   }
                    try
                    {
                        Picasso.get().load(cover).into(coverIV);
                    }
                    catch (Exception e)
                    {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditDialog();
        }
    });

        return view;
    }

    private boolean checkstoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStorgagePermission()
    {
        requestPermissions( storagePermission , STORAGE_REQUEST_CODE);
    }

    private boolean checkcameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.CAMERA)== (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestcameraPermission()
    {
       requestPermissions( cameraPermission , CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
            {
                if(grantResults.length> 0)
                {
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean WriteStorageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && WriteStorageAccepted)
                    {
                        pickFromCamera();
                    }
                    else
                    {
                        Toast.makeText(getActivity() , "Pleae enable Permissions for camera and storage" , Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
            case  STORAGE_REQUEST_CODE:
            {
                if(grantResults.length> 0)
                {
                    boolean WriteStorageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if(WriteStorageAccepted)
                    {
                        pickFromGallery();
                    }
                    else
                    {
                        Toast.makeText(getActivity() , "Pleae enable Permissions for gallery and storage" , Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                //image taken from gallery
                //get uri of image
                image_url = data.getData();
                
                uploadProfileCover(image_url);
            }
            if(requestCode== IMAGE_PICK_CAMERA_CODE)
            {
                uploadProfileCover(image_url);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCover(Uri url) {

        pd.show();

        String filepathAndName = storagePath + ""+ profileOrCover + "_" + user.getUid();

        StorageReference storageReference1= storageReference.child((filepathAndName));
        storageReference1.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while(!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                //check if image was uploaded
                if(uriTask.isSuccessful())
                {
                    HashMap<String , Object> result = new HashMap<>();
                    result.put(profileOrCover , downloadUri.toString());

                    reference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity() , "Image Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                         pd.dismiss();
                            Toast.makeText(getActivity() , "Error updating Image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(getActivity() , "Some Error Occured", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity() , e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent , IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //intent grabs images from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_url=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT , image_url);
        startActivityForResult(cameraIntent , IMAGE_PICK_CAMERA_CODE);

    }


    private void EditDialog() {
        String options[] = {
                "Edit Profile",
                "Edit Cover Photo",
                "Edit Name",
                "Edit Phone Number"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Action");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    pd.setMessage("Updating Profile Picture");
                    profileOrCover = "image";
                    ImageDialog();

                }
                else if(which==1)
                {
                    pd.setMessage("Updating Cover Picture");
                    profileOrCover = "cover";
                    ImageDialog();
                }
                else if( which==2)
                {
                    pd.setMessage("Updating Name");
                    NamePhoneDialog("name");
                }
                else if(which==3)
                {
                    pd.setMessage("Updating Phone");
                    NamePhoneDialog("phone");
                }
            }
        });

        builder.create().show();
}

    private void NamePhoneDialog(final String key) {

        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update" + key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter " +key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //text input
                String value = editText.getText().toString();
                if(!TextUtils.isEmpty(value))
                {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key , value);

                    reference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Updated......" , Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), e.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity() , "Please enter" + key , Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void ImageDialog() {

        String options[] = {
                "Camera",
                "Gallery",
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Pick Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    if(!checkcameraPermission())
                    {
                        requestcameraPermission();
                    }
                    else
                    {
                        pickFromCamera();
                    }
                }
                else if(which==1)
                {
                    if(!checkstoragePermission())
                    {
                        requestStorgagePermission();
                    }
                    else
                    {
                        pickFromGallery();
                    }
                }

            }
        });

        builder.create().show();



    }

    private void  CheckUser()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        {
            //user is signed in
            //.setText(user.getEmail());
        }
        else
        {
            //user is not signed in brought to main menu

            startActivity(new Intent(getActivity() , MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main , menu);
        super.onCreateOptionsMenu(menu , inflater);
    }

    //menu click

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if( id== R.id.action_logout )
        {
            firebaseAuth.signOut();
            CheckUser();
        }
        return super.onOptionsItemSelected(item);
    }
}
