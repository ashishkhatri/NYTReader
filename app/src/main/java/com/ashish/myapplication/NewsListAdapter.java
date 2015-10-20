package com.ashish.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
    private Context mContext;
    private List<NewsItem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView mCardView;
        public ImageView mThumbnail;
        public TextView mTitle;
        public TextView mAuthor;
        public ImageView mExpand;
        public TextView mAbstract;
        public ItemClickListener mClickListener;

        public ViewHolder(View view) {
            super(view);

            mCardView = (CardView) view.findViewById(R.id.cardview);
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            mTitle = (TextView) view.findViewById(R.id.title);
            mAuthor = (TextView) view.findViewById(R.id.author);
            mExpand = (ImageView) view.findViewById(R.id.expand);
            mAbstract = (TextView) view.findViewById(R.id.txtAbstract);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(view, getAdapterPosition());
        }
    }

    public NewsListAdapter(Context context, List<NewsItem> myDataset) {
        mContext = context;
        mDataset = myDataset;
    }


    @Override
    public NewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_news_list, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final NewsItem current = mDataset.get(position);

        if (!TextUtils.isEmpty(current.mImageUrl)) {
            Picasso.with(holder.mThumbnail.getContext())
                    .load(current.mImageUrl)
                    .into(holder.mThumbnail);
        } else {
            holder.mThumbnail.setImageResource(R.drawable.nyt_logo);
        }

        holder.mTitle.setText(current.mTitle);
        holder.mAuthor.setText(current.mAuthor);
        holder.mAbstract.setText(current.mAbstract);
        holder.mExpand.setImageResource(current.mExpanded ?
                R.drawable.expander_close_holo_light : R.drawable.expander_open_holo_light);
        holder.mAbstract.setVisibility(current.mExpanded ? View.VISIBLE : View.GONE);

        holder.mClickListener = new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent detailIntent = new Intent(mContext, NewsDetailActivity.class);
                detailIntent.putExtra(NewsDetailActivity.EXTRA_URL, current.mUrl);
                mContext.startActivity(detailIntent);
            }
        };

        holder.mExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataset.get(position).mExpanded = !mDataset.get(position).mExpanded;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}