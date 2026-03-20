package com.example.arfurnitureshop.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.activities.SearchResultsActivity;

public class SearchHelper {

    // Gọi hàm này ở bất kỳ Activity nào để "kích hoạt" thanh tìm kiếm
    public static void setupSearch(Activity activity) {
        TextView tvFakeSearch = activity.findViewById(R.id.tvFakeSearch);
        ImageView ivSearch = activity.findViewById(R.id.ivSearch);

        android.view.View.OnClickListener openSearchDialog = v -> showSearchDialog(activity);

        if (tvFakeSearch != null) tvFakeSearch.setOnClickListener(openSearchDialog);
        if (ivSearch != null) ivSearch.setOnClickListener(openSearchDialog);
    }

    // Hàm hiển thị hộp thoại tìm kiếm nổi
    private static void showSearchDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_search);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtRealSearch = dialog.findViewById(R.id.edtRealSearch);
        Button btnRealSearch = dialog.findViewById(R.id.btnRealSearch);

        btnRealSearch.setOnClickListener(v -> {
            String keyword = edtRealSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(activity, "Vui lòng nhập từ khóa!", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();

            Intent intent = new Intent(activity, SearchResultsActivity.class);
            intent.putExtra("SEARCH_KEYWORD", keyword);
            activity.startActivity(intent);
        });

        edtRealSearch.setOnEditorActionListener((v, actionId, event) -> {
            btnRealSearch.performClick();
            return true;
        });

        dialog.show();
    }
}