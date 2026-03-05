package com.example.arfurnitureshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.utils.CartManager;
import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> list;
    private Runnable onUpdate;

    public CartAdapter(List<CartItem> list, Runnable onUpdate) {
        this.list = list;
        this.onUpdate = onUpdate;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        return new CartViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_cart, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder h, int pos) {
        CartItem i = list.get(pos);
        DecimalFormat df = new DecimalFormat("#,###");
        h.name.setText(i.getProduct().getName());
        h.price.setText("₫ " + df.format(i.getProduct().getPrice()) + " VND");
        h.qty.setText(String.valueOf(i.getQuantity()));
        Glide.with(h.itemView.getContext()).load(i.getProduct().getImageUrl()).into(h.img);

        h.plus.setOnClickListener(v -> { i.setQuantity(i.getQuantity()+1); CartManager.getInstance(v.getContext()).add(i); notifyDataSetChanged(); onUpdate.run(); });
        h.minus.setOnClickListener(v -> { if(i.getQuantity()>1){ i.setQuantity(i.getQuantity()-1); CartManager.getInstance(v.getContext()).add(i); notifyDataSetChanged(); onUpdate.run(); }});
        h.del.setOnClickListener(v -> { CartManager.getInstance(v.getContext()).remove(i.getProduct().getId()); list.remove(pos); notifyDataSetChanged(); onUpdate.run(); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView img, del; TextView name, price, qty, plus, minus;
        public CartViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.imgCartProduct); del = v.findViewById(R.id.ivDelete);
            name = v.findViewById(R.id.tvCartProductName); price = v.findViewById(R.id.tvCartProductPrice);
            qty = v.findViewById(R.id.tvQuantity); plus = v.findViewById(R.id.tvPlus); minus = v.findViewById(R.id.tvMinus);
        }
    }
}