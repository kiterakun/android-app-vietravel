package com.group6.vietravel.features.user.ui.main.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.group6.vietravel.R;
import com.group6.vietravel.core.utils.UserUtils;
import com.group6.vietravel.data.repositories.auth.AuthRepository;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    // Khai báo các View
    private ShapeableImageView img_edit_avatar;
    private TextInputEditText edt_edit_username, edt_edit_email;
    private Button btn_cancel_edit, btn_save_profile;
    private CardView btn_pick_image;
    private Uri selectedImageUri = null;
    private EditProfileBottomSheet.OnUpdateProfileListener mListener;

    public interface OnUpdateProfileListener {
        void onUpdateProfile(Uri uri, String username, String email);
    }

    public void setOnUpdateProfileListener(EditProfileBottomSheet.OnUpdateProfileListener listener) {
        this.mListener = listener;
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    img_edit_avatar.setImageURI(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_edit_profile, container, false);

        img_edit_avatar = view.findViewById(R.id.img_edit_avatar);
        btn_pick_image = view.findViewById(R.id.btn_pick_image);
        edt_edit_username = view.findViewById(R.id.edt_edit_username);
        edt_edit_email = view.findViewById(R.id.edt_edit_email);
        btn_save_profile = view.findViewById(R.id.btn_save_profile);
        btn_cancel_edit = view.findViewById(R.id.btn_cancel_edit);

        String imageUrl = AuthRepository.getInstance().getUserProfileLiveData().getValue().getAvatar_url();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(img_edit_avatar);
        }

        btn_pick_image.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        btn_save_profile.setOnClickListener(v -> {
            String newName = edt_edit_username.getText().toString().trim();
            String newEmail = edt_edit_email.getText().toString().trim();

            if (newName.isEmpty()) {
                edt_edit_username.setError("Không được để trống tên");
                return;
            }

            if (mListener != null) {
                mListener.onUpdateProfile(selectedImageUri, newName, newEmail);
            }

            Toast.makeText(getContext(), "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btn_cancel_edit.setOnClickListener(v -> dismiss());

        return view;
    }
}
