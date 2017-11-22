package com.ppbike.model;

import com.ppbike.bean.AccountInfomationResult;
import com.ppbike.bean.AppVersionResult;
import com.ppbike.bean.BikeDetailsResult;
import com.ppbike.bean.BikeListResult;
import com.ppbike.bean.BoothListResult;
import com.ppbike.bean.CommentListResult;
import com.ppbike.bean.CreateOrderResult;
import com.ppbike.bean.LoginResult;
import com.ppbike.bean.MessageListResult;
import com.ppbike.bean.NearRepairShopResult;
import com.ppbike.bean.OrderDetailsResult;
import com.ppbike.bean.PerfectInfomationRequest;
import com.ppbike.bean.PrepaidResult;
import com.ppbike.bean.RentBikeOrderListResult;
import com.ppbike.bean.UploadBikeResult;
import com.ppbike.constant.Api;

import cn.master.volley.commons.VolleyHelper;
import cn.master.volley.models.response.handler.ResponseHandler;
import cn.master.volley.models.response.listener.ResponseListener;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class RequestModel {
    public static void login(ResponseHandler handler,String tag,String body){
        ResponseListener<LoginResult> listener = new ResponseListener<>(handler,LoginResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_LOGIN,body,null,listener,listener);
    }
    public static void register(ResponseHandler handler,String tag,String body){
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(tag,Api.URL,Api.CMD_REGISTER,body,null,listener,listener);
    }
    public static void updateUserInfomation(ResponseHandler handler, String tag, String body,String token){
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(tag,Api.URL,Api.CMD_UPDATE_USER_INFOMATION,body,token,listener,listener);
    }
    public static void updateVersion(ResponseHandler handler,String tag){
        ResponseListener<AppVersionResult> listener = new ResponseListener<>(handler,AppVersionResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_UPDATE_VERSION,null,null,listener,listener);
    }
    public static void obtainVerificationCode(ResponseHandler handler,String tag,String body){
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(tag,Api.URL,Api.CMD_VERIFICATION_CODE,body,null,listener,listener);
    }
    public static void modifyPassword(ResponseHandler handler,String tag,String body,String token){
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(tag,Api.URL,Api.CMD_MODIFY_PASSWORD,body,token,listener,listener);
    }
    public static void obtainNearBikeList(ResponseHandler handler,String tag,String body,String token){
        ResponseListener<BikeListResult> listener = new ResponseListener<>(handler,BikeListResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_NEAR_BIKE,body,token,listener,listener);
    }
    public static void obtainBikeDetatils(ResponseHandler handler ,String tag,String body,String token){
        ResponseListener<BikeDetailsResult> listener = new ResponseListener<>(handler,BikeDetailsResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_BIKE_DETTAILS,body,token,listener,listener);
    }
    public static void obtainRepairList(ResponseHandler handler ,String body,String token){
        ResponseListener<NearRepairShopResult> listener = new ResponseListener<>(handler,NearRepairShopResult.class);
        VolleyHelper.post(null,Api.URL,Api.CMD_NEAR_REPAIR_SHOP,body,token,listener,listener);
    }
    public static void obtainUserInfomation(ResponseHandler handler,String tag,String token){
        ResponseListener<PerfectInfomationRequest> listener = new ResponseListener<>(handler,PerfectInfomationRequest.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_OBTAIN_USER_INFOMATION,null,token,listener,listener);
    }
    public static void feedback(ResponseHandler handler, String body,String token) {
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(null,Api.URL,Api.CMD_FEEDBACK,body,token,listener,listener);

    }
    public static void obtainMessageList(ResponseHandler handler,String tag, String body,String token) {
        ResponseListener<MessageListResult> listener = new ResponseListener<>(handler,MessageListResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_MESSAGE_LIST,body,token,listener,listener);

    }
    public static void forgetPassword(ResponseHandler handler,String tag,String body,String token){
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(tag,Api.URL,Api.CMD_FORGET_PASSWORD,body,token,listener,listener);
    }
    public static void loginout(ResponseHandler handler, String tag,String token) {
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(tag,Api.URL,Api.CMD_LOGINOUT,null,token,listener,listener);
    }

    public static void uploadBike(ResponseHandler handler, String body, String token) {
        ResponseListener<UploadBikeResult> listener = new ResponseListener<>(handler,UploadBikeResult.class);
        VolleyHelper.post(null,Api.URL,Api.CMD_UPLOAD_BIKE,body,token,listener,listener);
    }

    public static void obtainBoothList(ResponseHandler handler,String tag, String body, String token) {
        ResponseListener<BoothListResult> listener = new ResponseListener<>(handler,BoothListResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_BOOTHLIST,body,token,listener,listener);

    }

    public static void uploadRepairShop(ResponseHandler handler, String body, String token) {
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(null,Api.URL,Api.CMD_ADD_REPAIR_SHOP,body,token,listener,listener);

    }

    public static void obtainRepairDetails(ResponseHandler handler, String body, String token) {
//        ResponseListener<RepairShopDetailsResult> listener = new ResponseListener<>(handler,RepairShopDetailsResult.class);
//        VolleyHelper.post(null,Api.URL,Api.CMD_REPAIR_SHOP,body,token,listener,listener);
    }

    public static void obtainBikeDetatilsEvaluation(ResponseHandler handler, String tag, String body, String token) {
        ResponseListener<CommentListResult> listener = new ResponseListener<>(handler,CommentListResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_COMMENT_LIST,body,token,listener,listener);
    }

    public static void createOrder(ResponseHandler handler, String body, String token) {
        ResponseListener<CreateOrderResult> listener = new ResponseListener<>(handler,CreateOrderResult.class);
        VolleyHelper.post(null,Api.URL,Api.CMD_CREATE_ORDER,body,token,listener,listener);

    }

    public static void obtainPayInfo(ResponseHandler handler, String body, String token) {
        ResponseListener<PrepaidResult> listener = new ResponseListener<>(handler,PrepaidResult.class);
        VolleyHelper.post(null,Api.URL,Api.CMD_PREPAID,body,token,listener,listener);

    }

    public static void obtainRentBikeOrderList(ResponseHandler handler, String tag, String body, String token) {
        ResponseListener<RentBikeOrderListResult> listener = new ResponseListener<>(handler,RentBikeOrderListResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_RENTBIKE_ORDERLIST,body,token,listener,listener);

    }
    public static void obtainRentBikeOrderDetails(ResponseHandler handler, String tag, String body, String token) {
        ResponseListener<OrderDetailsResult> listener = new ResponseListener<>(handler,OrderDetailsResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_ORDER_DETAILS,body,token,listener,listener);

    }
    public static void updateOrder(ResponseHandler handler, String tag, String body, String token) {
        ResponseListener<Object> listener = new ResponseListener<>(handler);
        VolleyHelper.post(tag,Api.URL,Api.CMD_UPDATE_ORDER,body,token,listener,listener);

    }

    public static void obtainBikeRentOrderList(ResponseHandler handler, String tag, String body, String token) {
        ResponseListener<RentBikeOrderListResult> listener = new ResponseListener<>(handler,RentBikeOrderListResult.class);
        VolleyHelper.post(tag,Api.URL,Api.CMD_BIKERENT_ORDERLIST,body,token,listener,listener);

    }

    public static void obtainUserAccountInfomation(ResponseHandler handler, String tag_info, String token) {
        ResponseListener<AccountInfomationResult> listener = new ResponseListener<>(handler,AccountInfomationResult.class);
        VolleyHelper.post(tag_info,Api.URL,Api.CMD_USER_ACCOUNT_INFOMATION, null,token,listener,listener);

    }
}
