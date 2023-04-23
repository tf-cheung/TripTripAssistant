package com.example.tripassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripassistant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText signupEmailEditText;
    private EditText signupUsernameEditText;
    private EditText signupPasswordEditText;
    private EditText signupConfirmPasswordEditText;
    private PopupWindow popupWindow;

    private Button backButton;

    private TextView passwordCriteriaTextView;

    // Password criteria regular expression
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signupPasswordEditText = findViewById(R.id.signup_password);
        passwordCriteriaTextView = findViewById(R.id.password_length_criteria_textview);

        signupPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP || event.getAction() == KeyEvent.ACTION_DOWN) {
                    isValidPassword(signupPasswordEditText.getText().toString());
                }
                return false;
            }
        });

        signupEmailEditText = findViewById(R.id.signup_email);
        signupUsernameEditText = findViewById(R.id.signup_username);
        signupConfirmPasswordEditText = findViewById(R.id.signup_confirm_password);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginTextView = findViewById(R.id.login_text);
        ImageButton backButton = findViewById(R.id.back_button);

        // Initialize the popupWindow
        popupWindow = new PopupWindow();
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

        signupPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showPasswordCriteriaPopup();
                } else {
                    hidePasswordCriteriaPopup();
                }
            }
        });
    }

    public void showPasswordCriteriaPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_password_criteria, null);

        TextView passwordCriteriaUppercaseTextView = popupView.findViewById(R.id.password_criteria_uppercase);
        TextView passwordCriteriaLowercaseTextView = popupView.findViewById(R.id.password_criteria_lowercase);
        TextView passwordCriteriaNumberTextView = popupView.findViewById(R.id.password_criteria_number);
        TextView passwordCriteriaSpecialCharTextView = popupView.findViewById(R.id.password_criteria_special_char);

        passwordCriteriaUppercaseTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
        passwordCriteriaLowercaseTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
        passwordCriteriaNumberTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
        passwordCriteriaSpecialCharTextView.setTextColor(ContextCompat.getColor(this, R.color.red));

        popupWindow.setContentView(popupView);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        // Set the initial color based on the current password
        String password = signupPasswordEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(password)) {
            isValidPassword(password);
        }

        signupPasswordEditText.requestFocus();
    }

    public void hidePasswordCriteriaPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void registerUser() {
        String email = signupEmailEditText.getText().toString().trim();
        String username = signupUsernameEditText.getText().toString().trim();
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
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                saveUserToDatabase(userId, email, username);
                                startActivity(new Intent(SignUpActivity.this, DisplayTripActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration succeeded, but failed to retrieve user information.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign up fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "Email is already in use.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void saveUserToDatabase(String userId, String email, String username) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(email, username);
        usersRef.child(userId).setValue(user);
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isSpecialChar(char c) {
        String specialChars = "!@#$%^&*()_-+=[]{}\\|:;\"'<>,.?/~`";
        return specialChars.contains(Character.toString(c));
    }

    private boolean isValidPassword(CharSequence password) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (isSpecialChar(c)) {
                hasSpecialChar = true;
            }
        }

        if (popupWindow != null && popupWindow.isShowing()) {
            View popupView = popupWindow.getContentView();
            TextView uppercaseTextView = popupView.findViewById(R.id.password_criteria_uppercase);
            if (uppercaseTextView != null) {
                uppercaseTextView.setTextColor(hasUppercase ? ContextCompat.getColor(this, R.color.green) : ContextCompat.getColor(this, R.color.red));
            }

            TextView lowercaseTextView = popupView.findViewById(R.id.password_criteria_lowercase);
            if (lowercaseTextView != null) {
                lowercaseTextView.setTextColor(hasLowercase ? ContextCompat.getColor(this, R.color.green) : ContextCompat.getColor(this, R.color.red));
            }

            TextView numberTextView = popupView.findViewById(R.id.password_criteria_number);
            if (numberTextView != null) {
                numberTextView.setTextColor(hasNumber ? ContextCompat.getColor(this, R.color.green) : ContextCompat.getColor(this, R.color.red));
            }

            TextView specialCharTextView = popupView.findViewById(R.id.password_criteria_special_char);
            if (specialCharTextView != null) {
                specialCharTextView.setTextColor(hasSpecialChar ? ContextCompat.getColor(this, R.color.green) : ContextCompat.getColor(this, R.color.red));
            }

            TextView lengthTextView = popupView.findViewById(R.id.password_length_criteria_textview);
            if (lengthTextView != null) {
                if (password.length() < 8) {
                    lengthTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
                } else {
                    lengthTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
                }
            }
        }

        return hasUppercase && hasLowercase && hasNumber && hasSpecialChar && password.length() >= 8;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus && popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}