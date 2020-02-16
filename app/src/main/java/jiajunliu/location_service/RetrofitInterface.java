package jiajunliu.location_service;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by liukakun on 2/15/20.
 */

public interface RetrofitInterface {

    @POST("/getHistory")
    Call<GetHistoryResult> executeGetHis();

    @POST("/setHistory")
    Call<Void> executeSetHis(@Body HashMap<String,String> map)
;
}
