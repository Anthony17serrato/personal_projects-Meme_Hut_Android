package gp.whatuwant.anthony.social.media.untitled.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import gp.whatuwant.anthony.social.media.untitled.R;
import life.knowledge4.videotrimmer.utils.FileUtils;

import static android.app.Activity.RESULT_OK;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ComposeDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link ComposeDialogFragment.Listener}.</p>
 */
public class ComposeDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private Listener mListener;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS= 5;
    private static final int TRIM_RESULT=4;
    private static final int GALLERY_REQUEST=2;
    private static final int VIDEO_REQUEST=3;
    private String post_type = null;
    private Uri uri=null;
    ImageView preview;
    FloatingActionButton fab;
    Button trim;
    RelativeLayout rlt;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    // TODO: Customize parameters
    public static ComposeDialogFragment newInstance(int itemCount) {
        final ComposeDialogFragment fragment = new ComposeDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_post_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final View myView=view;
        preview=(ImageView)view.findViewById(R.id.preview);
        Button addImage=(Button)view.findViewById(R.id.button2);
        ImageButton ib= (ImageButton)view.findViewById(R.id.imageButton);
        rlt= (RelativeLayout)view.findViewById(R.id.relative);
        trim=(Button)view.findViewById(R.id.buttoncrop);
        fab= (FloatingActionButton)view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri= null;
                rlt.setVisibility(View.GONE);
                trim.setVisibility(View.GONE);
                post_type=null;
            }
        });
        trim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showExplanation("Storage Permission Needed", "Rationale: trimmed video must access storage in order to save and access video", Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                    else {
                        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                } else {
                    Intent trim = new Intent(getActivity(), TrimActivity.class);
                    trim.putExtra("uri", FileUtils.getPath(getContext(), uri));
                    startActivityForResult(trim, TRIM_RESULT);
                }
            }
        });
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference=database.getInstance().getReference().child("InstaApp");
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getImage= new Intent(Intent.ACTION_GET_CONTENT);
                getImage.setType("image/*");
                startActivityForResult(getImage,GALLERY_REQUEST);
            }
        });
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getVideo= new Intent(Intent.ACTION_GET_CONTENT);
                getVideo.setType("video/mp4/*");
                startActivityForResult(getVideo,VIDEO_REQUEST);
            }
        });
        Button post=(Button)view.findViewById(R.id.button3);
        final EditText message=(EditText)view.findViewById(R.id.editText);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text=message.getText().toString().trim();
                ProgressBar pb=(ProgressBar)myView.findViewById(R.id.progress);
                pb.setVisibility(View.VISIBLE);
                LinearLayout hm=(LinearLayout)myView.findViewById(R.id.hide_me);
                hm.setVisibility(View.INVISIBLE);
                if(!TextUtils.isEmpty(text)&&uri!=null){
                    StorageReference filepath=storageReference.child("PostImage").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadurl =taskSnapshot.getDownloadUrl();
                            Toast.makeText(getActivity(),"Uploaded",Toast.LENGTH_LONG).show();
                            DatabaseReference newPost= databaseReference.push();
                            newPost.child("title").setValue(text);
                            newPost.child("image").setValue(downloadurl.toString());
                            newPost.child("post_type").setValue(post_type);
                            dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==GALLERY_REQUEST && resultCode ==RESULT_OK){
            uri= data.getData();
            rlt.setVisibility(View.VISIBLE);
            preview.setImageURI(uri);
            post_type="image";
        }
        if(requestCode ==VIDEO_REQUEST && resultCode ==RESULT_OK){
            uri= data.getData();
            rlt.setVisibility(View.VISIBLE);
            trim.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(uri).into(preview);
            post_type="video";
        }
        if(requestCode ==TRIM_RESULT && resultCode ==RESULT_OK){
            uri = null;
            String x = data.getParcelableExtra("trim_result").toString();
            uri = Uri.fromFile(new File(x));
            rlt.setVisibility(View.VISIBLE);
            trim.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(uri).into(preview);
            post_type="video";
        }
    }

    public interface Listener {
        void onComposeClicked(int position);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent trim = new Intent(getActivity(), TrimActivity.class);
                    trim.putExtra("uri", FileUtils.getPath(getContext(), uri));
                    startActivityForResult(trim, TRIM_RESULT);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(),"this operation cannot be completed without starage permission",Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }
    private void requestPermission(String permissionName, int permissionRequestCode) {
        requestPermissions(
                new String[]{permissionName}, permissionRequestCode);
    }



}
