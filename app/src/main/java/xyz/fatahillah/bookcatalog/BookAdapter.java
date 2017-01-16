package xyz.fatahillah.bookcatalog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mac on 1/16/17.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> scoreList) {
        super(context, R.layout.book_item, scoreList);
    }

    public static class ViewHolder{
        TextView bookTitle;
        TextView bookAuthors;
        TextView bookDescription;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the data item for this position
        Book book = getItem(position);
        ViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item,null);

            viewHolder = new ViewHolder();
            viewHolder.bookTitle = (TextView)convertView.findViewById(R.id.title_text_view);
            viewHolder.bookAuthors = (TextView)convertView.findViewById(R.id.author_text_view);
            viewHolder.bookDescription = (TextView)convertView.findViewById(R.id.description_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //set Text
        viewHolder.bookTitle.setText(book.getBookTitle());
        viewHolder.bookAuthors.setText(book.generateStringOfAuthor());
        viewHolder.bookDescription.setText(book.getBookDescription());

        //retun view of item
        return convertView;
    }

}
