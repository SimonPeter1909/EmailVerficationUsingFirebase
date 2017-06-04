package trickandroid.firebasedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity {


    private static final String TAG = "EmailVerification";

    private String email,pass;

    private TextView verEmailTV,afterEmailTV;

    private Button emailVerifiedBTN, gotoBTN;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        email = getIntent().getExtras().getString("email");
        pass = getIntent().getExtras().getString("pass");

        afterEmailTV = (TextView) findViewById(R.id.afterEmailTV);

        afterEmailTV.setText("Verification E-Mail has been sent to \n" + email + "\n After Verifiying your E-Mail click the 'E-Mail Verified' Button below" );

        verEmailTV = (TextView) findViewById(R.id.verEmailTV);

        verEmailTV.setText(email);

        gotoBTN = (Button) findViewById(R.id.gotoBTN);

        emailVerifiedBTN = (Button) findViewById(R.id.emailVerifiedBTN);

        emailVerifiedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(EmailVerification.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user!=null){
                                        if (user.isEmailVerified()){
                                            toastMessage("Your E-Mail " + email + " has been successfully Verified");
                                            afterEmailTV.setText("Your E-Mail " + email + " has been successfully Verified. \n Goto Welcome page by pressing the Button below");
                                            emailVerifiedBTN.setVisibility(View.GONE);
                                            gotoBTN.setVisibility(View.VISIBLE);
                                        } else {
                                            toastMessage("Error!!! Check your Internet Connection");
                                        }
                                    } else {
                                        toastMessage("Error!!! Check your Internet Connection");
                                    }
                                }

                                // ...
                            }
                        });
            }
        });

        gotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EmailVerification.this, NewActivity.class);
                i.putExtra("email",email);
                startActivity(i);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
