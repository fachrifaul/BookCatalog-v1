package xyz.fatahillah.bookcatalog;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    BookAdapter bookAdapter;
    ArrayList<Book> bookArrayList;
    EditText keywordText;
    Button searchButton;

    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keywordText = (EditText) findViewById(R.id.keywords_edit_text);
        searchButton = (Button) findViewById(R.id.search_button);
        listView = (ListView) findViewById(R.id.book_list_view);

        bookAdapter = new BookAdapter(MainActivity.this, bookArrayList);
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        private String searchKeyword = keywordText.getText().toString();

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookList) {
        }

    }

    private URL createUrl(String stringUrl) {
    }

    private String makeHttpRequest(URL url) throws IOException {
    }

    private String readFromStream(InputStream inputStream) throws IOException {
    }

    private ArrayList<Book> extractBookInfoFromJson(String bookJSON) {}

}