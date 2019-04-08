package gp.whatuwant.anthony.social.media.untitled.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gp.whatuwant.anthony.social.media.untitled.Activities.MainActivity;
import gp.whatuwant.anthony.social.media.untitled.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText loginEmail;
    private EditText loginPass;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final View myView = view;
        SignInButton si=(SignInButton)myView.findViewById(R.id.sign_in_button);
        TextView textView = (TextView) si.getChildAt(0);
        textView.setText("Sign In With Google");
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        loginEmail=(EditText)myView.findViewById(R.id.editText3);
        loginPass=(EditText)myView.findViewById(R.id.editText4);
        Button signin=(Button)myView.findViewById(R.id.button4);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=loginEmail.getText().toString().trim();
                String pass= loginPass.getText().toString().trim();
                if(!email.equals("")&&!pass.equals("")){
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkUserExists();
                            }
                            else{
                                Toast.makeText(getContext(),"invalid credentials",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void checkUserExists(){
        final String user_id= mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){
                    Intent loginIntent= new Intent(getActivity(),MainActivity.class);
                    startActivity(loginIntent);
                }else{
                    Toast.makeText(getContext(),"invalid credentials",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
