package xyz.fatahillah.bookcatalog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    BookAdapter bookAdapter;
    ArrayList<Book> bookArrayList;
    EditText keywordText;
    Button searchButton;
    ProgressDialog progressDialog;

    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keywordText = (EditText) findViewById(R.id.keywords_edit_text);
        searchButton = (Button) findViewById(R.id.search_button);
        listView = (ListView) findViewById(R.id.book_list_view);
        searchButton = (Button) findViewById(R.id.search_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        bookAdapter = new BookAdapter(MainActivity.this, new ArrayList<Book>());
        listView.setAdapter(bookAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {

                Book book = bookArrayList.get(i);

                //convert String url menjadi URI object
                Uri bookUri = Uri.parse(book.getBookInfoLink());

                //create new intent untuk membuka halaman website
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                //mengirim intent untuk membuka website
                startActivity(websiteIntent);

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                BookAsyncTask task = new BookAsyncTask();
                task.execute();
            }
        });

    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        private String searchKeyword = keywordText.getText().toString();

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {

            searchKeyword = searchKeyword.replace(" ", "+");
            URL url = createUrl(BOOK_REQUEST_URL + searchKeyword);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                // the function will be declared later.
                // `makeHttpRequest()` return String formated JSON.
            } catch (IOException e) {
                Log.e("MainActivity", "IOException", e);
                // if error happened, we log the error
            }

            bookArrayList = extractBookInfoFromJson(jsonResponse); // method declared later

            return bookArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookList) {
            progressDialog.dismiss();

            if (bookList == null) {
                bookAdapter = new BookAdapter(MainActivity.this, new ArrayList<Book>());
                listView.setAdapter(bookAdapter);
                return;
            }

            bookAdapter = new BookAdapter(MainActivity.this, bookList);
            listView.setAdapter(bookAdapter);
        }

    }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("MainActivity", "Error with creating URL", exception);
            return null;
        }

        return url;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                // read the response data and make it as a single string
            } else {
                Log.e("MainActivity", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    //
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    //
    private ArrayList<Book> extractBookInfoFromJson(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        ArrayList<Book> books = new ArrayList<Book>();

        try {

            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            if (baseJsonResponse.getInt("totalItems") == 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Result not found.", Toast.LENGTH_SHORT).show();
                    }
                });

                return null;
            }

            JSONArray itemArray = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject cuurentItem = itemArray.getJSONObject(i);
                JSONObject bookInfo = cuurentItem.getJSONObject("volumeInfo");

                // Get the book title
                String title = bookInfo.getString("title");

                // Get the list of book's author(s)
                String[] authors = new String[]{};
                JSONArray authorJsonArray = bookInfo.optJSONArray("authors");
                if (authorJsonArray != null) {
                    ArrayList<String> authorList = new ArrayList<String>();
                    for (int j = 0; j < authorJsonArray.length(); j++) {
                        authorList.add(authorJsonArray.get(j).toString());
                    }
                    authors = authorList.toArray(new String[authorList.size()]);
                }

                // Get the book's description
                String description = "";
                if (bookInfo.optString("description") != null)
                    description = bookInfo.optString("description");

                String infoLink = "";
                if (bookInfo.optString("infoLink") != null)
                    infoLink = bookInfo.optString("infoLink");

                //get cover image book
                String urlCover = "";
                JSONObject coverInfoLink = bookInfo.getJSONObject("imageLinks");
                urlCover = coverInfoLink.getString("thumbnail");

                // Add book information info into books array
                books.add(new Book(title, authors,
                        description, infoLink,
                        urlCover));
            }

        } catch (JSONException e) {
            Log.e("MainActivity", "Problem parsing the book JSON results", e);
        }

        return books;

    }

}