package edu.bluejack17_2.tolongku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText txtName, txtEmail, txtPhone,txtPassword;
    public RadioButton rbnMale, rbnFemale;
    public RadioGroup grpGender;
    public Button btnRegister;
    public TextView lblLoginOffer;
    private FirebaseAuth mAuth;
    private DatabaseReference storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        rbnMale = findViewById(R.id.rbnMale);
        rbnFemale = findViewById(R.id.rbnFemale);
        btnRegister = findViewById(R.id.btnRegister);
        lblLoginOffer = findViewById(R.id.lblLoginOffer);
        grpGender = findViewById(R.id.grpRegisterGender);
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(this);
        lblLoginOffer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.lblLoginOffer:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;

            case R.id.btnRegister:
                 registerUser();
                 break;
        }
    }

    public void registerUser()
    {
        String username = txtName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        txtName.setError(null);
        txtEmail.setError(null);
        txtPhone.setError(null);
        txtPassword.setError(null);

        if(username.isEmpty())
        {
            txtName.setError("Email is required");
            txtName.requestFocus();
            return;
        }
        if(email.isEmpty())
        {
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            txtEmail.setError("Please enter a valid email");
            txtEmail.requestFocus();
            return;
        }
        if(phone.isEmpty())
        {
            txtPhone.setError("Email is required");
            txtPhone.requestFocus();
            return;
        }
        if(grpGender.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Gender must be selected", Toast.LENGTH_SHORT).show();
            grpGender.requestFocus();
            return;
        }
        if(password.length() < 6)
        {
            txtPassword.setError("Minimum length of password should be minimum 6");
            txtPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    String currUserId = mAuth.getCurrentUser().getUid();
                    storeUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(currUserId);
                    storeUserData.child("userName").setValue(txtName.getText().toString().trim());
                    storeUserData.child("userEmail").setValue(txtEmail.getText().toString().trim());
                    storeUserData.child("userPhone").setValue(txtPhone.getText().toString().trim());
                    storeUserData.child("userContactNumber").setValue("");
                    storeUserData.child("userContactEmail").setValue("");
                    storeUserData.child("userMessage").setValue("");
                    storeUserData.child("userID").setValue(currUserId);
                    if(rbnMale.isChecked())
                    {
                        storeUserData.child("userGender").setValue("Male");
                    }
                    else
                    {
                        storeUserData.child("userGender").setValue("Female");
                    }
                    storeUserData.child("userImage").setValue("default_image").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                final FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Verification email sent to " + user.getEmail(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("RegisterActivity", "sendEmailVerification", task.getException());
                                            Toast.makeText(RegisterActivity.this,
                                                    "Failed to send verification email.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                MainActivity.fromRegister = true;
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                            else
                            {
                                FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                Toast.makeText(getApplicationContext(), "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Register","Failed Registration",e);
                                return;
                            }
                        }
                    });
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(), "Email has been registered", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
