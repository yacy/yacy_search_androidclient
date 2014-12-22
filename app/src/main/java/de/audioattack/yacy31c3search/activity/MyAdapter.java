package de.audioattack.yacy31c3search.activity;

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

import de.audioattack.yacy31c3search.R;
import de.audioattack.yacy31c3search.service.SearchItem;

/**
 * Created by low012 on 22.12.14.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<SearchItem> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView title;
        public final TextView url;
        public final TextView description;
        public final View v;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            title = (TextView) v.findViewById(R.id.tv_title);
            url = (TextView) v.findViewById(R.id.tv_url);
            description = (TextView) v.findViewById(R.id.tv_description);

            v.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {

                                         final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
                                                 .parse(getUriString()));
                                         try {
                                             v.getContext().startActivity(browserIntent);
                                         } catch (Exception ex) {
                                             Toast.makeText(v.getContext(),
                                                     ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                         }
                                     }
                                 }

            );
        }

        public String getUriString() {
            return url.getText().toString();
        }

    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<SearchItem> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final SearchItem item = mDataset.get(position);

        holder.title.setText(item.getTitle());
        holder.url.setText(item.getLink().toExternalForm());
        final String descr = item.getDescription();
        holder.description.setText(descr == null ? "-" : Html.fromHtml(descr));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}