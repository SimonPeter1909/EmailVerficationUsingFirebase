package trickandroid.firebasedatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccActivity extends Activity {

    private static final String TAG = "CreateAccActivity";

    private TextView createAccTV;
    private EditText verEmailET, verPassET;
    private Button accCreateBTN;

    //Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

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

        verEmailET = (EditText) findViewById(R.id.verEmailET);
        verPassET = (EditText) findViewById(R.id.verPassET);

        accCreateBTN = (Button) findViewById(R.id.accCreateBTN);

        accCreateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = verEmailET.getText().toString();
                final String pass = verPassET.getText().toString();

                if (email.isEmpty()) {
                    toastMessage("Enter E-Mail");
                } else if (pass.isEmpty()) {
                    toastMessage("Enter Password");
                } else {

                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(CreateAccActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(CreateAccActivity.this,VerificationActivity.class);
                                        i.putExtra("email",email);
                                        i.putExtra("pass",pass);
                                        startActivity(i);
                                    } else {
                                        toastMessage("Error!!! Check Internet Connection");
                                    }

                                    // ...
                                }
                            });


                }


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
