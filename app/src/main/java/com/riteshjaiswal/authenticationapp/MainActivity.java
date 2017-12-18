package com.riteshjaiswal.authenticationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_GOOGLE_SIGN_IN = 1000;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    private Context mContext;
    private TextView fbToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        mContext =this;
        fbToken =(TextView)findViewById(R.id.fbToken);

        GoogleSignInOptions googleSignInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("761998153486-5k4qqessj7knp6vkf3p9m8bghtp4dv3k.apps.googleusercontent.com")
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent= Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");
        Log.i("TAG","hahahah");
        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("TAG",loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getFaceBookData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            private void getFaceBookData(JSONObject object) {
                try {
                    fbToken.setText(object.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onCancel() {
                // App code
                Log.i("TAG", "Cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("TAG","Error");
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GOOGLE_SIGN_IN){
            GoogleSignInResult googleSignInResult =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSingInResult(googleSignInResult);
        }

            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            Log.i("TAG","Result");


    }


    private void handleSingInResult(GoogleSignInResult googleSignInResult){
        if(googleSignInResult.isSuccess()){
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

            Log.i("TAG","Token "+googleSignInAccount.getIdToken());
            Intent intent =new Intent(this, LoginActivity.class);


            intent.putExtra("Email",googleSignInAccount.getEmail().toString());

            startActivity(intent);
        }
        else
            Log.i("TAG","Failure");

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("TAG", "Connection Failed");
    }


}
