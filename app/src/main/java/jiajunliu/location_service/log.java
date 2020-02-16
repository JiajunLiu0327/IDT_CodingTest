package jiajunliu.location_service;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liukakun on 2/11/20.
 */

public class log extends Fragment {


    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private  String BASE_URL = "http://10.0.2.2:3000";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log, container,false);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        view.findViewById(R.id.gethis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<GetHistoryResult> call = retrofitInterface.executeGetHis();
                call.enqueue(new Callback<GetHistoryResult>() {
                    @Override
                    public void onResponse(Call<GetHistoryResult> call, Response<GetHistoryResult> response) {
                        if(response.code()==200){
                            GetHistoryResult result = response.body();

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            builder1.setTitle(result.getLoc());
                            builder1.setMessage(result.getTimeStamp());
                            builder1.show();

                            Toast.makeText(getActivity(),"Import from DB successfully",Toast.LENGTH_LONG).show();


                        }
                        else if (response.code() == 404){
                            Toast.makeText(getActivity(),"Data not found",Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onFailure(Call<GetHistoryResult> call, Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



/*  Logging using Files
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            String logMessage;
            try {
                FileInputStream readLog = getActivity().openFileInput("fuc_log");
                InputStreamReader logReader = new InputStreamReader(readLog);
                BufferedReader logBuffer = new BufferedReader(logReader);
                StringBuffer stringBuffer = new StringBuffer();


                TextView textView = view.findViewById(R.id.log);
                while ((logMessage = logBuffer.readLine()) != null) {
                    stringBuffer.append(logMessage);

                }
                logMessage = stringBuffer.toString();
                System.out.println(logMessage);
                String[] pinLocation = logMessage.split(" ");
                System.out.println(pinLocation.length);
                for(int i=0; i<=pinLocation.length-4; i+=4){
                    textView.append("Lat: " + pinLocation[i+2] + "  Lon: " + pinLocation[i+3] + "  at  " + pinLocation[i] + "   " + pinLocation[i+1] + "\n");
                }


            } catch (IOException ee) {
                Toast.makeText(getActivity().getApplicationContext(), "Log File Does Not Exist\nPlease Pin First", Toast.LENGTH_LONG).show();
                ee.printStackTrace();

            }


        }
*/
        return view;
    }


}
