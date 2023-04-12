package com.example.tripassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText signupUsernameEditText;
    private EditText signupPasswordEditText;
    private EditText signupConfirmPasswordEditText;
    private Button signupButton;
    private TextView loginTextView;
    private Button backButton;

    // Password criteria regular expression
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signupUsernameEditText = findViewById(R.id.signup_username);
        signupPasswordEditText = findViewById(R.id.signup_password);
        signupConfirmPasswordEditText = findViewById(R.id.signup_confirm_password);
        signupButton = findViewById(R.id.signup_button);
        loginTextView = findViewById(R.id.login_text);
        backButton = findViewById(R.id.back_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void registerUser() {
        String email = signupUsernameEditText.getText().toString().trim();
        String password = signupPasswordEditText.getText().toString().trim();
        String confirmPassword = signupConfirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            signupUsernameEditText.setError("Email is required.");
            return;
        }

        if (!isValidEmail(email)) {
            signupUsernameEditText.setError("Please provide a valid email address.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            signupPasswordEditText.setError("Password is required.");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            signupConfirmPasswordEditText.setError("Confirm password is required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            signupConfirmPasswordEditText.setError("Passwords do not match.");
            return;
        }

        if (!isValidPassword(password)) {
            signupPasswordEditText.setError("Password does not meet the criteria.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, save the username (email) and navigate to the main activity
                            String userId = mAuth.getCurrentUser().getUid();

                            saveUsernameToDatabase(userId, email);

                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveUsernameToDatabase(String userId, String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usernames");
        databaseReference.child(userId).setValue(username);
    }
}



