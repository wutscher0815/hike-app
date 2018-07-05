package org.do_the_math_baby.hike;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class hikeActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    Hiker hiker = new Hiker();

    LocationManager lm;

    LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            hiker.lat = location.getLatitude();
            hiker.lng = location.getLongitude();
            if (isTracking())
                track();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            System.out.println("provider enabled changed");
            setTracking(true);
        }

        @Override
        public void onProviderDisabled(String s) {
            System.out.println("provider disabled changed");
            setTracking(false);
        }
    };


    public String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        try {
            if (tracking) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15 * 1000, 0, ll);
            } else {
                lm.removeUpdates(ll);
                if (this.hiker.id != null && this.hiker.id != "")
                    this.delete(this.hiker.id);
            }
            this.tracking = tracking;
        } catch (SecurityException e) {
            Context context = getApplicationContext();
            CharSequence text = "Could not access location";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (IOException e) {
            Context context = getApplicationContext();
            CharSequence text = "Network error";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void track() {
        try {
            this.post(this.hiker.toJSON().toString());

        } catch (JSONException e) {

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public boolean tracking = true;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setTracking(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hike, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void post(String json) {
        new PostHiker().execute(json);
    }

    void delete(String id) throws IOException {
        new DeleteHiker().execute(id);
    }

    private class PostHiker extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... jsons) {
            Request request;
            try {
                String json = jsons[0];
                RequestBody body = RequestBody.create(JSON, json);
                request = new Request.Builder()
                        .url("http://hike.eu-gb.mybluemix.net/hiker")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {
            }
        }

        protected void onPostExecute(String json) {
            if (json != "error") {
                try {
                    Hiker hiker = new Hiker(new JSONObject(json));
                    hiker.name = hiker.name;
                    hiker.id = hiker.id;
                } catch (JSONException e) {

                }
            }
        }
    }

    private class DeleteHiker extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... id) {
            Request request;
            try {

                request = new Request.Builder()
                        .url("http://hike.eu-gb.mybluemix.net/hiker/" + id[0])
                        .delete()
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {
            }
        }

        protected void onPostExecute(String json) {
            if (json != "error") {

            }
        }
    }


}
