package com.musicbean.rtmp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.boyo.camerafilterfbo.encoder.TextureMovieEncoder;

import net.ossrs.yasea.SrsFlvMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by boyo on 16/10/21.
 */
public class RtmpPublisher {

    private static final String TAG = "RtmpPublisher";

    String mRtmpUrl;
    private SrsFlvMuxer flvMuxer;
    private int videoFlvTrack = -1;
    public static final int VGOP = 48;
    private long mPresentTimeUs;

    private AudioRecord mic;
    private boolean aloop = false;
    private Thread aworker;

    private MediaCodec aencoder;
    private MediaCodec.BufferInfo aebi = new MediaCodec.BufferInfo();
    public static final String ACODEC = "audio/mp4a-latm";
    public static final int ABITRATE = 32 * 1000;
    private int audioFlvTrack;

    net.ossrs.yasea.rtmp.RtmpPublisher.EventHandler mHandler = new net.ossrs.yasea.rtmp.RtmpPublisher.EventHandler() {
        @Override
        public void onRtmpConnecting(String msg) {
            Log.e(TAG, "connecting:" + msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            Log.e(TAG, "connected:" + msg);
        }

        @Override
        public void onRtmpVideoStreaming(String msg) {

        }

        @Override
        public void onRtmpAudioStreaming(String msg) {

        }

        @Override
        public void onRtmpStopped(String msg) {
            Log.e(TAG, "stopped:" + msg);
        }

        @Override
        public void onRtmpDisconnected(String msg) {
            Log.e(TAG, "disconnected:" + msg);
        }

        @Override
        public void onRtmpOutputFps(double fps) {

        }

        @Override
        public void onRtmpVideoBitrate(double bitrate) {

        }

        @Override
        public void onRtmpAudioBitrate(double bitrate) {

        }
    };

    public RtmpPublisher(int width, int height, String url) {
        flvMuxer = new SrsFlvMuxer(mHandler);
        flvMuxer.setVideoResolution(width, height);
        mRtmpUrl = url;

        mic = chooseAudioRecord();

        try {
            aencoder = MediaCodec.createEncoderByType(ACODEC);

            int ach = aChannelConfig == AudioFormat.CHANNEL_IN_STEREO ? 2 : 1;
            MediaFormat audioFormat = MediaFormat.createAudioFormat(ACODEC, ASAMPLERATE, ach);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, ABITRATE);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
            aencoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            // add the audio tracker to muxer.
            audioFlvTrack = flvMuxer.addTrack(audioFormat);

            aencoder.start();
        } catch (IOException e) {
            Log.e(TAG, "create aencoder failed.");
            e.printStackTrace();
        }

    }

    public void start() {
        mPresentTimeUs = System.nanoTime() / 1000;

        try {
            flvMuxer.start(mRtmpUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        aworker = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                startAudio();
            }
        });
        aloop = true;
        aworker.start();
    }

    public static final int ASAMPLERATE = 44100;
    public static int aChannelConfig = AudioFormat.CHANNEL_IN_STEREO;

    public AudioRecord chooseAudioRecord() {
        int minBufferSize = AudioRecord.getMinBufferSize(ASAMPLERATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord mic = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, ASAMPLERATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        if (mic.getState() != AudioRecord.STATE_INITIALIZED) {
            mic = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, ASAMPLERATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
            if (mic.getState() != AudioRecord.STATE_INITIALIZED) {
                mic = null;
            } else {
                aChannelConfig = AudioFormat.CHANNEL_IN_MONO;
            }
        } else {
            aChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }

        return mic;
    }

    private void startAudio() {
        if (mic != null) {
            mic.startRecording();

            byte pcmBuffer[] = new byte[4096];
            while (aloop && !Thread.interrupted()) {
                int size = mic.read(pcmBuffer, 0, pcmBuffer.length);
                if (size <= 0) {
                    break;
                }
                onGetPcmFrame(pcmBuffer, size);
            }
        }
    }

    public void onGetPcmFrame(byte[] data, int size) {
        ByteBuffer[] inBuffers = aencoder.getInputBuffers();
        ByteBuffer[] outBuffers = aencoder.getOutputBuffers();

        int inBufferIndex = aencoder.dequeueInputBuffer(-1);
        if (inBufferIndex >= 0) {
            ByteBuffer bb = inBuffers[inBufferIndex];
            bb.clear();
            bb.put(data, 0, size);
            long pts = System.nanoTime() / 1000 - mPresentTimeUs;
            aencoder.queueInputBuffer(inBufferIndex, 0, size, pts, 0);
        }

        for (; ; ) {
            int outBufferIndex = aencoder.dequeueOutputBuffer(aebi, 0);
            if (outBufferIndex >= 0) {
                ByteBuffer bb = outBuffers[outBufferIndex];
                onEncodedAacFrame(bb, aebi);
                aencoder.releaseOutputBuffer(outBufferIndex, false);
            } else {
                break;
            }
        }
    }

    private void onEncodedAacFrame(ByteBuffer es, MediaCodec.BufferInfo bi) {
        try {
            flvMuxer.writeSampleData(audioFlvTrack, es, bi);
        } catch (Exception e) {
            Log.e(TAG, "muxer write audio sample failed.");
            e.printStackTrace();
        }
    }


    public void writeVideoData(ByteBuffer es, MediaCodec.BufferInfo bi) {
        if (videoFlvTrack == -1 && TextureMovieEncoder.getInstance().getMediaFormat() != null) {
            videoFlvTrack = flvMuxer.addTrack(TextureMovieEncoder.getInstance().getMediaFormat());
        }

        if (videoFlvTrack == -1) {
            Log.e("wcb", "videoFlvTrack == -1");
            return;
        }

        AtomicInteger videoFrameCacheNumber = flvMuxer.getVideoFrameCacheNumber();
        if (videoFrameCacheNumber != null && videoFrameCacheNumber.get() < VGOP) {
            try {
                long pts = System.nanoTime() / 1000 - mPresentTimeUs;
                bi.presentationTimeUs = pts;
                flvMuxer.writeSampleData(videoFlvTrack, es, bi);
            } catch (Exception e) {
                Log.e(TAG, "muxer write video sample failed.");
                e.printStackTrace();
            }
        } else {

        }
    }

    public void Stop() {

        aloop = false;
        if (aworker != null) {
            aworker.interrupt();
            aworker = null;
        }

        if (mic != null) {
            mic.setRecordPositionUpdateListener(null);
            mic.stop();
            mic.release();
            mic = null;
        }

        if (aencoder != null) {
            Log.i(TAG, "stop aencoder");
            aencoder.stop();
            aencoder.release();
            aencoder = null;
        }

        flvMuxer.stop();
    }

    public boolean checkAudio() {
        if (aworker != null && aworker.isAlive()) {
            return true;
        } else {
            aworker = new Thread(new Runnable() {
                @Override
                public void run() {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                    startAudio();
                }
            });
            aloop = true;
            aworker.start();
            return false;
        }
    }
}
