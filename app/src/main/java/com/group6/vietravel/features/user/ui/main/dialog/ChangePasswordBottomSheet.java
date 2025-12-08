package com.group6.vietravel.features.user.ui.main.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.group6.vietravel.R;

public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {

    private TextInputEditText edt_current_pass, edt_new_pass, edt_confirm_pass;
    private Button btn_cancel_pass, btn_save_pass;

    private ChangePasswordBottomSheet.OnChangePasswordListener mListener;

    public interface OnChangePasswordListener {
        void onChangePassword(String currPassword, String newPassword);
    }

    public void setOnChangePasswordListener(ChangePasswordBottomSheet.OnChangePasswordListener listener) {
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_change_password, container, false);

        edt_current_pass = view.findViewById(R.id.edt_current_pass);
        edt_new_pass = view.findViewById(R.id.edt_new_pass);
        edt_confirm_pass = view.findViewById(R.id.edt_confirm_pass);
        btn_save_pass = view.findViewById(R.id.btn_save_pass);
        btn_cancel_pass = view.findViewById(R.id.btn_cancel_pass);

        btn_save_pass.setOnClickListener(v -> {
            String currentPass = edt_current_pass.getText().toString();
            String newPass = edt_new_pass.getText().toString();
            String confirmPass = edt_confirm_pass.getText().toString();
            
            if (newPass.length() < 6) {
                edt_new_pass.setError("Mật khẩu phải trên 6 ký tự");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                edt_confirm_pass.setError("Mật khẩu xác nhận không khớp");
                return;
            }

            if(mListener!=null){
                mListener.onChangePassword(currentPass, newPass);
            }

            dismiss();
        });

        btn_cancel_pass.setOnClickListener(v -> dismiss());

        return view;
    }
}
