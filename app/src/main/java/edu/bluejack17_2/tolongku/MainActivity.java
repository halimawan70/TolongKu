package edu.bluejack17_2.tolongku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Iterator;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    static public GoogleApiClient mGoogleApiClient;
    static public GoogleSignInOptions gso;

    private static final int RC_SIGN_IN = 123;
    private Button btnSignIn;
    CallbackManager callbackManager;
    LoginButton loginButton;
    private DatabaseReference storeUserData;

    static public boolean facebookAuth = false;
    static public boolean googleAuth = false;
    static public boolean firebaseAuth = false;
    static public String authID = "";

    public static boolean fromRegister = false;

   
    @Override
    public void onBackPressed()
    {
        //check auth

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Toast.makeText(this, "firebase user null", Toast.LENGTH_SHORT).show();
        }
        if(MainActivity.mGoogleApiClient == null && !mGoogleApiClient.isConnected())
        {
            Toast.makeText(this, "google user null", Toast.LENGTH_SHORT).show();
        }
        if(AccessToken.getCurrentAccessToken() == null)
        {
            Toast.makeText(this, "facebook user null", Toast.LENGTH_SHORT).show();
        }

       if(FirebaseAuth.getInstance().getCurrentUser() == null && (MainActivity.mGoogleApiClient == null ) && (AccessToken.getCurrentAccessToken() == null))
       {
            Toast.makeText(getApplicationContext(),"You are not supposed to be here",Toast.LENGTH_SHORT).show();

            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
       }
    }


    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.lblSignupOffer:
                //goto register
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                break;
            case R.id.btnSignIn:
                loginBtnClick();
                break;
            case R.id.btnGoogleSignin:
                googleSignIn();
                break;


        }



    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.



        // Build a GoogleSignInClient with the options specified by gso.

        //facebook sign in
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
        loginButton = findViewById(R.id.btnFacebookSignin);

        loginButton.registerCallback(callbackManager,new FacebookCallback<LoginResult>(){

            public void onSuccess(LoginResult loginResult)
            {
                facebookAuth = true;
                Log.e("facebook","sampe sini");
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(final JSONObject object, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    authID = object.optString("id");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ref.child("Users").child(object.optString("id")).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                // username already exists
                                            } else {
                                                String name= object.optString("name");
                                                String id = object.optString("id");
                                                String gender = object.optString("gender");
                                                String email = object.optString("email");

                                                Log.e("facebook",name+" "+id+" "+gender+" "+email);

                                                storeUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                                                storeUserData.child("userName").setValue(name);
                                                storeUserData.child("userEmail").setValue(email);
                                                storeUserData.child("userPhone").setValue(0);
                                                storeUserData.child("userGender").setValue(gender);
                                                storeUserData.child("userContactNumber").setValue(0);
                                                storeUserData.child("userContactEmail").setValue("");
                                                storeUserData.child("userMessage").setValue("");


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
                                }
                            }

                        }).executeAsync();

            }

            public void onCancel()
            {
                Toast.makeText(MainActivity.this, "Facebook Login Canceled", Toast.LENGTH_SHORT).show();
            }

            public void onError(FacebookException error)
            {
                Toast.makeText(MainActivity.this, "Facebook login failed", Toast.LENGTH_SHORT).show();
            }
            
        });



        //onclick listener registration
        findViewById(R.id.lblSignupOffer).setOnClickListener(this);
        findViewById(R.id.btnGoogleSignin).setOnClickListener(this);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);


    }

    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            if(fromRegister)
            {
                fromRegister = false;
                return;
            }
            Toast.makeText(this, "User is logged in!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        }else{
            Toast.makeText(this, "User is not logged in!", Toast.LENGTH_SHORT).show();
        }


        // check existing user

        if( MainActivity.mGoogleApiClient != null && MainActivity.mGoogleApiClient.isConnected())
        {
            Toast.makeText(this, "Google User is logged in!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        }

        if(AccessToken.getCurrentAccessToken() != null)
        {
            Toast.makeText(this, "Facebook User is logged in!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        }
    }

    public void googleSignIn()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void loginBtnClick(){
        TextView tbEmail = findViewById(R.id.tbUsername);
        TextView tbPassword = findViewById(R.id.tbPassword);

        String email = tbEmail.getText().toString();
        String password = tbPassword.getText().toString();



        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            firebaseAuth = true;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(!user.isEmailVerified())
                            {
                                Toast.makeText(getApplicationContext(),"Please verify email fist",Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                MainActivity.firebaseAuth = false;
                                return;
                            }
                            authID = mAuth.getCurrentUser().getUid();
//                            updateUI(user);
                            Toast.makeText(MainActivity.this,
                                    "Authentication succeeded.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(..);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account != null)
            {
                googleAuth = true;
                authID = account.getId();
                Toast.makeText(getApplicationContext(),account.getId(), Toast.LENGTH_LONG).show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("Users").child(account.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.d("MainActivity","ada biji");

                            // use "username" already exists
                            // Let the user know he needs to pick another username.
                        } else {
                            Log.d("MainActivity",""+dataSnapshot.getChildrenCount());
                            String id = account.getId();
                            String name = account.getDisplayName();
                            String email = account.getEmail();
                            String gender = "";

                            storeUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                            storeUserData.child("userName").setValue(name);
                            storeUserData.child("userEmail").setValue(email);
                            storeUserData.child("userPhone").setValue(0);
                            storeUserData.child("userGender").setValue(gender);
                            storeUserData.child("userContactNumber").setValue(0);
                            storeUserData.child("userContactEmail").setValue("");
                            storeUserData.child("userMessage").setValue("");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            // Signed in successfully, show authenticated UI.

            startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LOGINGERROR", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d("Google Sign In", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Google Sign In", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(MainActivity.this, "Google Sign In Success!"
                                    , Toast.LENGTH_SHORT).show();
                        }else{
                            // If sign in fails, display a message to the user.
                            Log.w("Google Sign In", "signInWithCredential:failure",
                                    task.getException());
                            Toast.makeText(MainActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Connection", "Connection failed: " + connectionResult);
    }
}
