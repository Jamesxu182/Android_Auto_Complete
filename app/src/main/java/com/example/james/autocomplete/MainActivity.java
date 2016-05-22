package com.example.james.autocomplete;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, LocationListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private Cursor cursor = null;
    private Button button = null;
    private Button star = null;
    private Button history = null;
    private EditText edit_text = null;
    private ListView list_view = null;
    private SampleDBManager manager = new SampleDBManager(MainActivity.this);
    private ArrayList<Item> items = new ArrayList<Item>();
    private ArrayList<Item> items_from_google = new ArrayList<Item>();
    private ArrayList<Item> items_from_db = new ArrayList<Item>();
    private ArrayList<Item> items_from_place = new ArrayList<Item>();
    private String search = new String();
    private LocationManager locationManager;
    private double longitude = 0;
    private double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.edit_text = (EditText) findViewById(R.id.editText);
        this.button = (Button) findViewById(R.id.button);
        this.star = (Button) findViewById(R.id.star);
        this.history = (Button) findViewById(R.id.history);
        this.list_view = (ListView) findViewById(android.R.id.list);

        try {
            manager.open();

            //input some data into database to test.
            //manager.insertSomething("Google");
            //manager.insertSomething("Facebook");
            //manager.insertSomething("Twetter");
            //manager.insertSomething("Dropbox");
            //manager.insertSomething("Oracle");
            //manager.insertSomething("Whatsapp");
            //manager.deleteSomething("Whatsapp");

            //Read the data from database.
            cursor = manager.ListData();
            this.items_from_db = manager.getItems(cursor);
            this.items = items_from_db;

            cursor.close();

            manager.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        list_view.setAdapter(new CustomAdapter(MainActivity.this, items));
        this.edit_text.addTextChangedListener(this);
        this.button.setOnClickListener(this);
        this.star.setOnClickListener(this);
        this.history.setOnClickListener(this);

        //Test whether the internet is connected when the programming run.
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "No network connect available", Toast.LENGTH_LONG).show();
        }

        //set ClickListener and LongClickListener for each item in list view.
        this.list_view.setOnItemClickListener(this);
        this.list_view.setOnItemLongClickListener(this);


        //Check I
        edit_text.setFilters(new InputFilter[]{new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (source.charAt(i) == '\'') {
                        return "";
                    }
                }
                return null;
            }
        }});

        //update the location information when programming run.
        setUpLocation();
    }


    //if the location changed, the programming will execute the function below.
    //The device will fresh the location information
    @Override
    public void onLocationChanged(Location location) {

        if(location != null) {
            this.longitude = location.getLongitude();
            this.latitude = location.getLatitude();
            //System.out.println("(" + this.latitude+ "," + this.longitude + ")");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        ;
    }

    @Override
    public void onProviderEnabled(String provider) {
        ;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        ;
    }

    //call system service to fresh the location information.
    private void setUpLocation() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 5, this);
    }

    //When the system are on resume. the location information will start.
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 5, this);
    }

    //When the system are pause, the GPS module will stop.
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    //Execute onCick() when the copy button be pressed. Copy the text in edit text to the clipboard.
    @Override
    public void onClick(View view) {
        //Get the string in edit text component
        if(view.getId() == R.id.button) {
            String content = this.edit_text.getText().toString();

            if (content.length() != 0) {
                //Copy content in edit text component to clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(content, content);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copy to clipboard", Toast.LENGTH_LONG).show();

                //if the item you input not much any record in database, it will be inserted into database.
                if (content.length() != 0) {
                    try {
                        this.manager.open();
                        this.manager.insertSomething(content);
                        this.manager.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //if there are nothing on the clipboard, It will give some information on Toast.
                Toast.makeText(getApplicationContext(), "Please input something", Toast.LENGTH_LONG).show();
            }
            //When star button will be pressed, the list view will display all item starred.
        } else if(view.getId() == R.id.star) {
            try {
                manager.open();
                this.items = manager.getItems(manager.ListStar());
                manager.close();
                list_view.setAdapter(new CustomAdapter(MainActivity.this, items));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //When history button will be pressed, the list view will display history of what you used to search.
        } else if(view.getId() == R.id.history) {
            try {
                manager.open();
                this.items = manager.getItems(manager.ListData());
                manager.close();
                list_view.setAdapter(new CustomAdapter(MainActivity.this, items));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        ;
    }

    //When something change in edit view component. It will fresh the item in listview according to what is in the edit view.
    @Override
    public void afterTextChanged(Editable s) {
        this.search = this.edit_text.getText().toString();
        freshListView(search);
        //Toast.makeText(getApplicationContext(), "key be pressed", Toast.LENGTH_LONG).show();
    }


    //Set a function will run after any item be clicked. the content in every item will be copy to edit view.
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Item temp = (Item)adapterView.getItemAtPosition(i);
        this.edit_text.setText(temp.getContent());
        this.edit_text.setSelection(this.edit_text.getText().length());
    }

    //Set a function will run after any item be long clicked. the view switcher will changer other view on the item.
    //there will be some buttons to replace the text on item.
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
        final Item temp = (Item) adapterView.getItemAtPosition(i);
        int source = temp.getSource();
        final ViewSwitcher switcher = (ViewSwitcher)view.findViewById(R.id.viewSwitcher);
        ImageButton delete_button = (ImageButton)view.findViewById(R.id.imageButton);
        ImageButton search_button = (ImageButton)view.findViewById(R.id.imageButton2);
        ImageButton back_button = (ImageButton)view.findViewById(R.id.imageButton3);
        CheckBox check_box = (CheckBox)view.findViewById(R.id.checkBox);
        switcher.showNext();

        if(temp.getStar() == true) {
            check_box.setChecked(true);
        } else {
            check_box.setChecked(false);
        }

        //When the star check box be click, it will star or unstar
        check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(temp.getStar() == true) {
                        manager.open();
                        manager.updateSomething(temp);
                        manager.close();
                    } else {
                        manager.open();
                        manager.updateSomething(temp);
                        manager.insertSomething(temp.getContent(), true);
                        manager.close();
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        //Set Listener on delete button on each list item, it will delete the item which you choose.
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    manager.open();
                    manager.deleteSomething(items.get(i).getContent());
                    manager.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                items.remove(i);
                Collections.sort(items, new ItemComparator(search));
                list_view.setAdapter(new CustomAdapter(MainActivity.this, items));
            }
        });

        //Set Listener for search button on list item. Google search item and Database item will jump to browser to search.
        //Google Place Item will jump to google map activity to show place.
        if((source == 1) || (source == 2)) {

            search_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse("http://www.google.ie/search?q=" + temp.getContent().replaceAll(" ", "+"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

        } else if(source == 3) {
            search_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Place_ID_Finder().execute(String.format("https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=AIzaSyDZS6b3fQFtOccTpFlTXjgxipxwhFPKoag", ((GooglePlaceItem) temp).getPlaceId()));
                }
            });
        }

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switcher.showPrevious();
            }
        });

        return true;
    }


    //fresh the listview, the item on the list view have 3 sources, which are database, google search and google place.
    public void freshListView(String content)
    {
        //When there are some thing in edit view. the items from database, google search and google place.
        if(content.length() != 0) {
            ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            try {
                this.items = new ArrayList<Item>();
                //Check internet connect
                if(networkInfo != null && networkInfo.isConnected()) {
                    //get items from database.
                    new Google_Search_Auto_Complete().execute("http://suggestqueries.google.com/complete/search?client=firefox&q=" + content.replaceAll(" ", "%20"));
                    //check location work or not. get items from google place.
                    if(this.longitude != 0 && this.latitude != 0) {
                        new Google_Place_Auto_Complete().execute(String.format("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&location=%s,%s&key=AIzaSyDZS6b3fQFtOccTpFlTXjgxipxwhFPKoag", content.replaceAll(" ", "%20"), Double.toString(this.latitude), Double.toString(this.longitude)));
                    } else {
                        new Google_Place_Auto_Complete().execute(String.format("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&key=AIzaSyDZS6b3fQFtOccTpFlTXjgxipxwhFPKoag", content.replaceAll(" ", "%20")));
                    }

                } else {
                    //textView.setText("No network connect available");
                    Toast.makeText(getApplicationContext(), "No network connect available", Toast.LENGTH_LONG).show();
                }

                //get item from database.
                this.manager.open();
                this.items_from_db = this.manager.getItems(this.manager.selectSomeThing(content));
                this.manager.close();
                this.items.addAll(this.items_from_db);
                this.items.addAll(this.items_from_google);
                //System.out.println("Google: " + items_from_google.toString());
                //System.out.println("Database: " + items_from_db.toString());
                //System.out.println("Items: " + items.toString());
                Collections.sort(this.items, new ItemComparator(this.search));
                this.list_view.setAdapter(new CustomAdapter(MainActivity.this, this.items));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //When nothing in the edit view. the items are only from database.
        } else {
            try {
                //get item from database.
                this.manager.open();
                this.items_from_db = this.manager.getItems(this.manager.ListData());
                this.items = this.items_from_db;
                this.list_view.setAdapter(new CustomAdapter(MainActivity.this, items));
                this.manager.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //Get google search auto complete
    private class Google_Search_Auto_Complete extends AsyncTask<String, Void, String>
    {
        //Download the json from google search
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                //System.out.println("URL: " + urls[0]);
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        // Parse json and make it become json item.
        @Override
        protected void onPostExecute(String result) {
            //System.out.println(result);
            try {
                items_from_google = new ArrayList<Item>();
                JSONArray array0 = new JSONArray(result);
                items_from_google.add(new Item(array0.getString(0), 2));
                //System.out.println(array0.getString(0));
                JSONArray array1 = array0.getJSONArray(1);
                for(int i = 0; i < array1.length(); i++) {
                    //System.out.println(array1.getString(i));
                    items_from_google.add(new GoogleSearchItem(array1.getString(i), 2));
                }

                items = new ArrayList<Item>();
                items.addAll(items_from_db);
                items.addAll(items_from_google);
                items.addAll(items_from_place);
                //System.out.println("Google: " + items_from_google.toString());
                //System.out.println("Database: " + items_from_db.toString());
                //System.out.println("Items: " + items.toString());
                Collections.sort(items, new ItemComparator(search));
                list_view.setAdapter(new CustomAdapter(MainActivity.this, items));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Get google place auto complete
    private class Google_Place_Auto_Complete extends AsyncTask<String, Void, String>
    {
        //Download json google place.
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                //System.out.println("URL: " + urls[0]);
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        //Parse Json
        @Override
        protected void onPostExecute(String result) {
            try {
                items_from_place = new ArrayList<Item>();
                JSONObject object = new JSONObject(result);
                JSONArray array = object.getJSONArray("predictions");
                //Provide 5 items from google place
                for(int i = 0; i < array.length() && i < 5; i++) {
                    JSONObject subobject = array.getJSONObject(i);
                    //System.out.println(subobject.getString("description"));
                    //System.out.println(subobject.getString("place_id"));
                    GooglePlaceItem item = new GooglePlaceItem(subobject.getString("description"), 3, subobject.getString("place_id"));
                    items_from_place.add(item);
                }

                items = new ArrayList<Item>();
                items.addAll(items_from_db);
                items.addAll(items_from_google);
                items.addAll(items_from_place);
                //System.out.println("Google: " + items_from_google.toString());
                //System.out.println("Database: " + items_from_db.toString());
                //System.out.println("Items: " + items.toString());
                Collections.sort(items, new ItemComparator(search));
                list_view.setAdapter(new CustomAdapter(MainActivity.this, items));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Get the longitude and latitude of location by place id
    private class Place_ID_Finder extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                //System.out.println("URL: " + urls[0]);
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            System.out.println(result);
            try {
                JSONObject object = new JSONObject(result);
                JSONObject location = object.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);


                intent.putExtra("latitude", location.getDouble("lat"));
                intent.putExtra("longitude", location.getDouble("lng"));

                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //System.out.println(result);

        }
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.

        try {
            URL url = new URL(myurl);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        /*Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[10000];
        reader.read(buffer);
        return new String(buffer);*/
        StringBuffer response = new StringBuffer();
        String buffer = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            while((buffer = reader.readLine()) != null) {
                response.append(buffer + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(response.toString());
        return response.toString();
    }
}


