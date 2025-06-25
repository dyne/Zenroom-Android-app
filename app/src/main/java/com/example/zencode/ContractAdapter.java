package com.example.zencode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {

    private final List<ZencodeContract> contractList;
    private final OnContractListener onContractListener;

    public ContractAdapter(List<ZencodeContract> contractList, OnContractListener onContractListener) {
        this.contractList = contractList;
        this.onContractListener = onContractListener;
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contract, parent, false);
        return new ContractViewHolder(itemView, onContractListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        ZencodeContract currentContract = contractList.get(position);
        holder.titleTextView.setText(currentContract.getTitle());
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    public static class ContractViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleTextView;
        OnContractListener onContractListener;

        public ContractViewHolder(@NonNull View itemView, OnContractListener onContractListener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            this.onContractListener = onContractListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onContractListener.onContractClick(getAdapterPosition());
        }
    }

    public interface OnContractListener {
        void onContractClick(int position);
    }
}