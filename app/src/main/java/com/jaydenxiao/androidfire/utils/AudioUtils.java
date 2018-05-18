package com.jaydenxiao.androidfire.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by xtt on 2017/11/2.
 */

public class AudioUtils {

    private TextToSpeech mSpeech; // TTS对象
    private static AudioUtils audioUtils;
    private static final String TAG_TTS = "AudioUtils";
    private Context context;
    public AudioUtils(Context context) {
            this.context=context;
            speechInit();
    }

    /**
     * 初始化TextToSpeech，在onCreate中调用
     */
    private void speechInit() {
        releaseTTS();
        // 创建TTS对象
        mSpeech = new TextToSpeech(context, new TTSListener());
    }

    private final class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            Log.e(TAG_TTS, "初始化结果：" + (status == TextToSpeech.SUCCESS));
            int result = mSpeech.setLanguage(Locale.CHINESE);
            //如果返回值为-2，说明不支持这种语言
            Log.e(TAG_TTS, "是否支持该语言：" + (result != TextToSpeech.LANG_NOT_SUPPORTED));
        }
    }

    /**
     * 将文本用TTS播放
     *
     * @param str 播放的文本内容
     */
    public void playTTS(String str) {
        if (mSpeech == null) mSpeech = new TextToSpeech(context,new TTSListener());
        mSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        Log.i(TAG_TTS, "播放语音为：" + str);
    }

    public void releaseTTS(){
        if (mSpeech != null) {
            mSpeech.stop();
            mSpeech.shutdown();
            mSpeech = null;
        }
    }
}
