package edu.bluejack17_2.tolongku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText txtName, txtEmail, txtPhone,txtPassword;
    public RadioButton rbnMale, rbnFemale;
    public Button btnRegister;
    public TextView lblLoginOffer;
    private FirebaseAuth mAuth;



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
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
