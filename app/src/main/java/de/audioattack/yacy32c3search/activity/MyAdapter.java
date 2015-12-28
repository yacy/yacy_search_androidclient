/*
 * Copyright 2014 Marc Nause <marc.nause@gmx.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see  http:// www.gnu.org/licenses/.
 */
package de.audioattack.yacy32c3search.activity;

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

import de.audioattack.yacy32c3search.R;
import de.audioattack.yacy32c3search.parser.SearchItem;

/**
 * Simple adapter for search items.
 *
 * @author Marc Nause <marc.nause@gmx.de>
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final List<SearchItem> searchItems;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final TextView url;
        public final TextView description;

        /**
         * ViewHolder for items.
         *
         * @param v view to display data in
         */
        public ViewHolder(final View v) {
            super(v);
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

    /**
     * Constructor.
     *
     * @param searchItems contains search results
     */
    public MyAdapter(final List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final SearchItem item = searchItems.get(position);

        holder.title.setText(item.getTitle());
        holder.url.setText(item.getLink());
        final String descr = item.getDescription();
        holder.description.setText(descr == null ? "-" : Html.fromHtml(descr));
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

}