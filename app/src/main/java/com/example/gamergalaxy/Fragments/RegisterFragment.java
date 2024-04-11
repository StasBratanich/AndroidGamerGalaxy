package com.example.gamergalaxy.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamergalaxy.Data.UsersData;
import com.example.gamergalaxy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> mGetContent;
    private Uri selectedImageUri;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mGetContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            ImageView imageView = getView().findViewById(R.id.F_register_uploadImage);
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(imageView);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();

        EditText registerConfirmPassword = view.findViewById(R.id.F_register_confirmPassword);
        EditText registerPassword = view.findViewById(R.id.F_register_password);
        Button registerBTN = view.findViewById(R.id.F_register_registerBTN);
        ImageView registerUploadImage = view.findViewById(R.id.F_register_uploadImage);

        registerUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mGetContent.launch(intent);
        });

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = registerPassword.getText().toString();
                String confirmPassword = registerConfirmPassword.getText().toString();

                if (!checkMatchingPasswords(password, confirmPassword)) {
                    Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                } else {
                    registerFunction();
                }
            }
        });

        return view;
    }

    public void registerFunction() {
        String email = ((EditText) getView().findViewById(R.id.F_register_email)).getText().toString().trim();
        String password = ((EditText) getView().findViewById(R.id.F_register_password)).getText().toString().trim();
        String id = ((EditText) getView().findViewById(R.id.F_register_ID)).getText().toString().trim();

        if (!id.matches("\\d{9}")){
            Toast.makeText(getContext(), "ID must be 9 digits long", Toast.LENGTH_SHORT).show();
            return;
        }else if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email must be entered", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(getContext(), "Password must be entered", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid Email format", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 digits long", Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Profile image must be uploaded", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(getContext(), "ID already exists", Toast.LENGTH_SHORT).show();
                } else{
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String imageUrl = selectedImageUri.toString();
                                        uploadImageAndSaveUserData(email, id, user.getUid());
                                        Toast.makeText(getContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("email", email);
                                        bundle.putString("profileImage", imageUrl);
                                        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_lobbyFragment, bundle);
                                    } else {
                                        Toast.makeText(getContext(), "Email exists already", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error checking ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageAndSaveUserData(String email, String id, String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images/" + userId + ".jpg");

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    addUserViaID(email, id, imageUrl, userId);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    public void addUserViaID(String email, String id, String imageUrl, String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        UsersData data;
        if (imageUrl != null) {
            data = new UsersData(email, id, imageUrl); // Adjust UsersData constructor to accept imageUrl
        } else {
            data = new UsersData(email, id);
        }
        usersRef.child(userId).setValue(data);
    }

    private boolean checkMatchingPasswords(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
