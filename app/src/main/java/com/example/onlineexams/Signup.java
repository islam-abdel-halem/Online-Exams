package com.example.onlineexams;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private FirebaseAuth Auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        EditText first_name = findViewById(R.id.firstName);
        EditText last_name = findViewById(R.id.lastName);
        EditText email = findViewById(R.id.Email);
        EditText password = findViewById(R.id.password);
        EditText confirm_password = findViewById(R.id.confirm_password);
        Button signup = findViewById(R.id.signup);
        TextView login = findViewById(R.id.login);

        signup.setOnClickListener(view -> {
            ProgressDialog progressDialog = new ProgressDialog(Signup.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Thread thread = new Thread(() -> {

                String pass = password.getText().toString();
                String confirm_pass = confirm_password.getText().toString();
                String em = email.getText().toString();
                String FirstName = first_name.getText().toString();
                String LastName = last_name.getText().toString();

                if (!pass.equals(confirm_pass)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            confirm_password.setError("Password not matched");
                            progressDialog.dismiss();

                        }
                    });
                    return;
                }
                Auth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener(Signup.this, (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Auth.getCurrentUser();
                        assert user != null;
                        DatabaseReference ref = database.child("Users").child(user.getUid());
                        ref.child("FirstName").setValue(FirstName);
                        ref.child("LastName").setValue(LastName);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Intent i = new Intent(Signup.this, Home.class);
                                i.putExtra("User UID", user.getUid());
                                startActivity(i);
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(Signup.this, "Opration Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            });
            thread.start();
        });


        login.setOnClickListener(view -> {
            Intent i = new Intent(Signup.this, MainActivity.class);
            startActivity(i);
            finish();
        });


    }

    ;
}
