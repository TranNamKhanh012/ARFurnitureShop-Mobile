package com.example.arfurnitureshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.models.UserAddress;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<UserAddress> addressList;
    private OnAddressClickListener listener;

    // Tạo Interface để báo cáo sự kiện bấm nút
    public interface OnAddressClickListener {
        void onEdit(UserAddress address);
        void onDelete(UserAddress address);
    }

    public AddressAdapter(List<UserAddress> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        UserAddress address = addressList.get(position);

        holder.tvNamePhone.setText(address.getReceiverName() + " | " + address.getPhoneNumber());
        holder.tvFullAddress.setText(address.getFullAddress());
        holder.tvDefaultBadge.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        // Kích hoạt sự kiện bấm
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(address));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(address));
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamePhone, tvDefaultBadge, tvFullAddress, btnEdit, btnDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamePhone = itemView.findViewById(R.id.tvNamePhone);
            tvDefaultBadge = itemView.findViewById(R.id.tvDefaultBadge);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}