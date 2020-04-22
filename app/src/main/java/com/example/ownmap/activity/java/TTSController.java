package com.example.ownmap.activity.java;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

public class TTSController implements SynthesizerListener, AMapNaviListener {

    public static TTSController ttsManager;
    boolean isfinish = true;
    private Context mContext;
    // 合成对象.
    private SpeechSynthesizer mSpeechSynthesizer;
    /**
     * 用户登录回调监听器.
     */
    private SpeechListener listener = new SpeechListener() {

        @Override
        public void onCompleted(SpeechError error) {
            if (error != null) {
            }
        }

        @Override
        public void onEvent(int arg0, Bundle arg1) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }
    };

    TTSController(Context context) {
        mContext = context;
    }

    public static TTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new TTSController(context);
        }
        return ttsManager;
    }

    public void init() {
        AMapNavi mAMapNavi = null;
        mAMapNavi = AMapNavi.getInstance(mContext);
        mAMapNavi.setUseInnerVoice(true);
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=" + "5e95c981");
//        SpeechUser.getUser().login(mContext, null, null, "appid=" + "输入您讯飞的appid", listener);
        // 初始化合成对象.
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.e("AAA","2A");
            }
        });
        initSpeechSynthesizer();
    }

    /**
     * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
     *
     * @param
     */
    public void playText(String playText) {
        if (!isfinish) {
            return;
        }
        if (null == mSpeechSynthesizer) {
            // 创建合成对象.
            mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
                @Override
                public void onInit(int i) {
                    Log.e("AAA","2A");
                }
            });
            initSpeechSynthesizer();
        }
        // 进行语音合成.
        mSpeechSynthesizer.startSpeaking(playText, this);
    }

    public void stopSpeaking() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stopSpeaking();
        }
    }

    public void startSpeaking() {
        isfinish = true;
    }

    private void initSpeechSynthesizer() {
//      // 设置发音人
//      mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
//      // 设置语速
//      mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "tts_speed");
//      // 设置音量
//      mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "tts_volume");
//      // 设置语调
//      mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "tts_pitch");
    }

    @Override
    public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
    }

    @Override
    public void onCompleted(SpeechError arg0) {
        isfinish = true;
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    @Override
    public void onSpeakBegin() {
        isfinish = false;
    }

    @Override
    public void onSpeakPaused() {
    }

    @Override
    public void onSpeakProgress(int arg0, int arg1, int arg2) {
    }

    @Override
    public void onSpeakResumed() {
    }

    public void destroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stopSpeaking();
        }
    }

    @Override
    public void onArriveDestination() {
        this.playText("到达目的地");
    }

    @Override
    public void onArrivedWayPoint(int arg0) {
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        this.playText("路径计算失败，请检查网络或输入参数");
    }

    @Override
    public void onEndEmulatorNavi() {
        this.playText("导航结束");
    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {
        this.playText(arg1);
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onInitNaviFailure() {
    }

    @Override
    public void onInitNaviSuccess() {
    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        this.playText("前方路线拥堵，路线重新规划");
    }

    @Override
    public void onReCalculateRouteForYaw() {
        this.playText("您已偏航");
    }

    @Override
    public void onStartNavi(int arg0) {
    }

    @Override
    public void onTrafficStatusUpdate() {
    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
    }

    @Override
    public void hideCross() {
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        String calculateResult = "路径计算就绪";
        this.playText(calculateResult);
    }

    @Override
    public void notifyParallelRoad(int i) {
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }
}
