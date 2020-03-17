package com.dove.readandroid.tts;

public interface WhyTTS {
     void speak(final String content);
     void pause();
     void resume();
     void  stop();
     void setSpeechRate(float newRate);
     void setSpeechPitch(float newPitch);
}