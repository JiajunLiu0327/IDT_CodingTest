package jiajunliu.location_service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.widget.TextView;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liukakun on 2/11/20.
 */

public class location extends Fragment {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private  String BASE_URL = "http://10.0.2.2:3000";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location, container, false);

        Button button;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);


        button = view.findViewById(R.id.getLocation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                    return;
                }
                FusedLocationProviderClient client;

                client = LocationServices.getFusedLocationProviderClient(getActivity());
                client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if(location!=null){
                            TextView textView = getView().findViewById(R.id.location);
                            final String text = "Latitude: "+Double.toString(location.getLatitude())+"   Longitude: "+Double.toString(location.getLongitude());
                            textView.setText("Latitude: "+Double.toString(location.getLatitude())+"\nLongitude: "+Double.toString(location.getLongitude()));

                            Button buttonShare;
                            buttonShare = getView().findViewById(R.id.share);
                            buttonShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    String shareBody = "My location is at "+text;
                                    intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                                    startActivity(Intent.createChooser(intent,"Share Location using"));
                                }
                            });

                            Calendar date = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = dateFormat.format(date.getTime());

                            String message = currentTime + " " + location.getLatitude() + " " + location.getLongitude()+" \n";
                            System.out.println(message);


                            HashMap<String,String> map = new HashMap<>();
                            map.put("loc",text);
                            map.put("timestamp",currentTime);
                            Call<Void> call =retrofitInterface.executeSetHis(map);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code()==200){
                                        Toast.makeText(getActivity(),"Upload to DB successfully",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(getActivity(),"Error uploading to DB",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(getActivity(), t.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });


                            /* Logging using Files
                            String state = Environment.getExternalStorageState();
                            if (Environment.MEDIA_MOUNTED.equals(state)) {
                                File root = Environment.getExternalStorageDirectory();
                                File dir = new File(root.getAbsolutePath()+"/location_service");
                                if (!dir.exists()){
                                    dir.mkdir();
                                }
                                    String fileInternalLocation = "Successfully got location, stored at ";
                                    try {
                                        FileOutputStream writeLog = getActivity().openFileOutput("fuc_log",getActivity().MODE_APPEND);
                                        fileInternalLocation += getActivity().getFilesDir().getAbsolutePath();
                                        writeLog.write(message.getBytes());
                                        writeLog.close();
                                        Toast.makeText(getActivity().getApplicationContext(),fileInternalLocation,Toast.LENGTH_LONG).show();
                                    } catch (IOException ee) {
                                        Toast.makeText(getActivity().getApplicationContext(),"Cannot write to log file.",Toast.LENGTH_LONG).show();
                                        ee.printStackTrace();
                                    }
                                }*/
                            }


                        }


                });

            }
        });


        return view;
    }



}
