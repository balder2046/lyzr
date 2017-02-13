package com.d2eam.g201401;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class CCAudioRecoder extends Thread
{
    protected AudioRecord audioRecord;
    protected int         minBufSize;
    protected int         recMaxSeconds;
    protected int         recPosition;
    protected byte []     recBuffer;
    protected boolean     isRunning;
    protected short       channelConfig;
    protected short       audioFormat;
    protected int         sampleRate;

    public static int makeAlign(int src, int aligned) {
        return ((src + aligned - 1) / aligned) * aligned;
    }

    public void run() {
        Log.d("cocos2dx", "begin recode.");  
        try {
            final int channlCount = ( channelConfig == AudioFormat.CHANNEL_IN_MONO ) ? 1 : 2;
            final int unitBytes = ( audioFormat == AudioFormat.ENCODING_PCM_16BIT ) ? 2 : 1;
            final int maxRecSize = makeAlign( recMaxSeconds * sampleRate * channlCount * unitBytes, minBufSize );
            recBuffer = new byte [maxRecSize];
            audioRecord.startRecording();
            recPosition = 0;
            Log.d("cocos2dx", "begin recode maxRecSize="+maxRecSize+" recUnit="+(maxRecSize/minBufSize)); 
            while(isRunning && recPosition < maxRecSize) {
                Log.d("cocos2dx", "read " + recPosition); 
                audioRecord.read(recBuffer, recPosition, minBufSize);
                recPosition += minBufSize;
            }
            Log.d("cocos2dx", "ended " + recPosition + " buf=" + recBuffer.length);  
            audioRecord.stop();
            audioRecord.release();  
            audioRecord = null;
            GameClient.onAudioRecodeEnded( recBuffer, recPosition, sampleRate, channelConfig, audioFormat );
        }
        catch(Exception e)
        {
            Log.d("cocos2dx", "catched Exception");  
            e.printStackTrace();
        }
    }
  
    private AudioRecord createAudioRecord() {  
        channelConfig = AudioFormat.CHANNEL_IN_MONO;
        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        for (int sampleRate : new int[]{11025, 22050, 44100}) {  
            try {  
                Log.v("cocos2dx", "try sampleRate="+sampleRate+" channelConfig="+channelConfig+" audioFormat="+audioFormat);
                minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);  
                if (minBufSize <= 0) 
                    continue;  
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,  
                    sampleRate, channelConfig, audioFormat,  minBufSize * 4);  
                Log.v("cocos2dx", "try createAudioRecord="+audioRecord.getState());
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    this.sampleRate = sampleRate;
                    return audioRecord;  
                }
                audioRecord.release();  
                audioRecord = null;  
            } catch (Exception e) {  
            // Do nothing  
            }  
        }  
        Log.d("cocos2dx", "createAudioRecord() failed : no suitable audio configurations on this device.");  
        return null;
    }

    public boolean beginRecode( int recMaxSeconds )
    {
        this.recMaxSeconds = recMaxSeconds;
        audioRecord = createAudioRecord();
        if ( audioRecord == null )
            return false;
        isRunning = true;
        Log.v("cocos2dx", "start AudioRecord");
        start();
        return true;
    }

    public void endRecode()
    {
        isRunning = false;
    }
}
