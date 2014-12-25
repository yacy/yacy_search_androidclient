package de.audioattack.yacy31c3search.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import de.audioattack.yacy31c3search.R;
import de.audioattack.yacy31c3search.service.SearchItem;

/**
 * Created by low012 on 22.12.14.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final List<SearchItem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final TextView url;
        public final TextView description;
        public final View v;

        public ViewHolder(final View v) {
            super(v);
            this.v = v;
            title = (TextView) v.findViewById(R.id.tv_title);
            url = (TextView) v.findViewById(R.id.tv_url);
            description = (TextView) v.findViewById(R.id.tv_description);

            v.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {

                                         final Uri uri = Uri.parse(getUriString());
                                         final Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                                         try {
                                             v.getContext().startActivity(browserIntent);
                                         } catch (ActivityNotFoundException ex) {

                                             final Context context = v.getContext();
                                             Toast.makeText(context,
                                                     String.format(Locale.US, context.getString(R.string.activity_not_found), uri.getScheme()),
                                                     Toast.LENGTH_LONG).show();

                                         } catch (Exception ex) {

                                             final Context context = v.getContext();
                                             Toast.makeText(context,
                                                     String.format(Locale.US, context.getString(R.string.activity_not_found), ex.getLocalizedMessage()),
                                                     Toast.LENGTH_LONG).show();
                                         }
                                     }
                                 }

            );
        }

        public String getUriString() {
            return url.getText().toString();
        }

    }

    public MyAdapter(final List<SearchItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final SearchItem item = mDataset.get(position);

        holder.title.setText(item.getTitle());
        holder.url.setText(item.getLink());
        final String descr = item.getDescription();
        holder.description.setText(descr == null ? "-" : Html.fromHtml(descr));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}