package com.whitbread.premierinn.postcodefinder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.contentsquare.android.Contentsquare;
import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.databinding.ItemAdressFinderListBinding;
import com.whitbread.premierinn.domain.common.AddressShort;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class PostcodeFinderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AddressShort> postcodeAddresses;
    private Subject<AddressShort> postcodeAddressSubject;
    private ItemAdressFinderListBinding binding;

    public PostcodeFinderAdapter() {
        this.postcodeAddresses = new ArrayList<>();
        this.postcodeAddressSubject = PublishSubject.create();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemAdressFinderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AddressHolderView(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AddressHolderView addressViewHolder = (AddressHolderView) holder;
        addressViewHolder.bind(postcodeAddresses.get(position), postcodeAddressSubject);
    }

    @Override
    public int getItemCount() {
        return postcodeAddresses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setResults(@NonNull List<AddressShort> postcodeAddresses) {
        this.postcodeAddresses.clear();
        this.postcodeAddresses.addAll(postcodeAddresses);
        notifyDataSetChanged();
    }

    public Observable<AddressShort> onAddressClick() {
        return postcodeAddressSubject;
    }

    public static class AddressHolderView extends RecyclerView.ViewHolder {

        private ItemAdressFinderListBinding binding;

        public AddressHolderView(ItemAdressFinderListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AddressShort addressItem, Subject<AddressShort> postcodeAddressSubject) {
            binding.tvPostcodeFinderAddressText.setText(addressItem.getLine());
            RxView.clicks(binding.getRoot())
                    .map(object -> addressItem)
                    .subscribe(postcodeAddressSubject);
            Contentsquare.mask(binding.tvPostcodeFinderAddressText);
        }
    }
}
