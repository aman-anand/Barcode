package android.rn.com.barcode.adapter;

import android.content.Context;
import android.content.Intent;
import android.rn.com.barcode.R;
import android.rn.com.barcode.ResultHandler;
import android.rn.com.barcode.base.AppConstants;
import android.rn.com.barcode.base.RealmRecyclerViewAdapter;
import android.rn.com.barcode.base.bean.NfcBean;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import io.realm.OrderedRealmCollection;

/**
 * Created by DELL on 1/11/2018.
 */
public class NfcAdapter extends RealmRecyclerViewAdapter<NfcBean, NfcAdapter.MyViewHolder> {

    private boolean inDeletionMode = false;
    private Set<Integer> countersToDelete = new HashSet<Integer>();

    public NfcAdapter(OrderedRealmCollection<NfcBean> data) {
        super(data, true);
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(true);
    }

    void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if (!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }

    Set<Integer> getCountersToDelete() {
        return countersToDelete;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.barcode_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NfcBean obj = getItem(position);
        if (obj != null) {
            holder.datal.setText(obj.getData());
            if (TextUtils.isEmpty(obj.getDesc())) {
                holder.descLayout.setVisibility(View.GONE);
            } else {
                holder.desc.setText(obj.getDesc());
            }

        }

    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView datal, desc;
        ImageView imageView;
        CardView cardView;
        LinearLayout nameLayout, descLayout, typeLayout;
        Context context;
//        public BarcodeBean data;

        MyViewHolder(View view) {
            super(view);
            context = view.getContext();

            datal = (TextView) view.findViewById(R.id.data);
            nameLayout = (LinearLayout) view.findViewById(R.id.nameLayout);
            descLayout = (LinearLayout) view.findViewById(R.id.descLayout);
            typeLayout = (LinearLayout) view.findViewById(R.id.typeLayout);
            desc = (TextView) view.findViewById(R.id.desc);
            cardView = view.findViewById(R.id.card);
            nameLayout.setVisibility(View.GONE);
            imageView = view.findViewById(R.id.image);
            imageView.setVisibility(View.GONE);
            typeLayout.setVisibility(View.GONE);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            getAdapterPosition()
            Log.e("RV", String.valueOf(getItem(getAdapterPosition()).getId()));
            Intent intent = new Intent(context, ResultHandler.class);
            intent.putExtra(AppConstants.UPDATE, AppConstants.NFC);
            intent.putExtra(AppConstants.ID, getItem(getAdapterPosition()).getId());
            context.startActivity(intent);
        }
    }
}