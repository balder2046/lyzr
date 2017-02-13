package com.d2eam.g201401;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class CCAudioPlayer extends Thread
{ 
    protected AudioTrack audioTrack;
    protected int        minBufSize;
    protected int        pcmMaxSeconds = 60;
    protected int        pcmPosition;
    protected byte []    pcmBuffer;
    protected boolean    isRunning;
    protected short      channelConfig;
    protected short      audioFormat;
    protected int        sampleRate;

    public void begin( final byte [] pcmBuffer )
    {
        this.pcmBuffer = pcmBuffer.clone();
    	begin();
    }

    public void begin()
    {
        try
        {
            channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
            audioFormat = AudioFormat.ENCODING_PCM_16BIT; 
            sampleRate = 11025; 
            minBufSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            Log.v("cocos2dx", "start AudioPlayer minBufSize="+minBufSize);
            if ( minBufSize <= 0 )
                return;

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channelConfig, audioFormat, minBufSize, AudioTrack.MODE_STREAM);
        
            isRunning = true;
            Log.v("cocos2dx", "start AudioPlayer state="+audioTrack.getState());
            start();
        }
        catch(Exception e)
        {
            Log.v("cocos2dx", "get Exception in begin()");
            e.printStackTrace();
        }
    }

    public void end()
    {
        isRunning = false;
    }
 
    public void onPause()
    {
        // Log.v("cocos2dx", "play onPause");
        audioTrack.pause();
    }

    public void onResume()
    {
        audioTrack.play();
    }

    public void onDestroy()
    {
        // Log.v("cocos2dx", "play onDestroy");
        isRunning = false;
    }

    public void run()
    {
        try
        {
            byte [] pcmStream = null;
            if ( pcmBuffer == null )
                pcmStream = new byte[minBufSize];
            audioTrack.play();
            Log.v("cocos2dx", "start playing");
            int num = 0;
            while (isRunning) {
                if ( audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING ) {
                    // Log.v("cocos2dx", "play pos = "+num++);
                    if ( pcmBuffer == null ) {
                        GameClient.nativeSetPCMStreamInThread(pcmStream, minBufSize, sampleRate, channelConfig, audioFormat);
                        audioTrack.write(pcmStream, 0, minBufSize);
                    } else {
                        int validSize = minBufSize;
                        if ( validSize > pcmBuffer.length - pcmPosition )
                            validSize = pcmBuffer.length - pcmPosition;
                        audioTrack.write(pcmBuffer, pcmPosition, validSize);
                        pcmPosition += validSize;
                        if (pcmPosition >= pcmBuffer.length)
                            break;
                    }
                } else {
                    try {
                        Thread.sleep( 200 );
                    } catch (final Exception e) {
                    }
                }
            }
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
        catch(Exception e)
        {
            Log.v("cocos2dx", "get Exception in run()");
            e.printStackTrace();
        }
    }
}
