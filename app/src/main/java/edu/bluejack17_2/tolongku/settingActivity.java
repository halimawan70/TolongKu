package edu.bluejack17_2.tolongku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class settingActivity extends AppCompatActivity implements View.OnClickListener {

    private StorageReference mStorageRef;
    private DatabaseReference updateUserData;

    private EditText txtProfileName;
    private EditText txtProfilePassword;
    private EditText txtProfileConfirm;
    private EditText txtProfileEmail;
    private EditText txtProfileNumber;
    private EditText txtProfileImportantNumber;
    private EditText txtProfileImportantEmail;
    private EditText txtProfileMessage;
    private Button btnProfileSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfilePassword = findViewById(R.id.txtProfilePassword);
        txtProfileConfirm = findViewById(R.id.txtProfileConfirm);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtProfileNumber = findViewById(R.id.txtProfileNumber);
        btnProfileSave = findViewById(R.id.btnProfileSave);
        txtProfileImportantNumber = findViewById(R.id.txtProfileImportantNumber);
        txtProfileImportantEmail = findViewById(R.id.txtProfileImportantEmail);
        txtProfileMessage = findViewById(R.id.txtProfileMessage);

        updateUserData= FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.authID);
        Toast.makeText(getApplicationContext(),MainActivity.authID,Toast.LENGTH_SHORT).show();
        btnProfileSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnProfileSave:
                updateData();break;
        }
    }

    public void updateData()
    {
        //Toast.makeText(getApplicationContext(),"pencet save",Toast.LENGTH_SHORT).show();

        Log.d("updateprofile", "mau update");
        if(updateUserData!=null)
        {
            Log.d("updateprofile", "updateData tidak null");
            updateUserData.child("userEmail").setValue(txtProfileEmail.getText().toString());
            updateUserData.child("userName").setValue(txtProfileName.getText().toString());
            updateUserData.child("userPhone").setValue(txtProfileNumber.getText().toString());
            updateUserData.child("userContactNumber").setValue(txtProfileImportantNumber.getText().toString());
            updateUserData.child("userContactEmail").setValue(txtProfileImportantEmail.getText().toString());
            updateUserData.child("userMessage").setValue(txtProfileMessage.getText().toString());


            if(MainActivity.firebaseAuth == true)
            {
                //Toast.makeText(getApplicationContext(),""+MainActivity.firebaseAuth,Toast.LENGTH_SHORT).show();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),txtProfilePassword.getText().toString());
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("updateprofile",txtProfileConfirm.getText().toString());
                            user.updatePassword(txtProfileConfirm.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("updateprofile", "Password updated");
                                        //Toast.makeText(getApplicationContext(),"password updated",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("updateprofile", "Error password not updated");
                                    }
                                }
                            });
                        }
                        else
                        {
                            Log.d("updateprofile", "Error auth failed");
                        }
                    }
                });
            }

            Toast.makeText(getApplicationContext(),"Data updated",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
        }
    }

}
