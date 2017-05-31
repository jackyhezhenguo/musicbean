package com.example.xinhuang.test1;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.content.ContentValues.TAG;

public class UploadThread {

    // TODO: native method
//	public native void testJni();
//	public native void testJniData(byte []data, int data_len);
//	public native void testJniSendPacket(byte []data, int data_len, int isCfg);
    public int iWidth;
    public int iHeight;
    public int framerate;
    public int bitrate;
    public int useHWEncode;
    public int useTcpAdapter;
    public long startTimestamp;

    public static class MyData {
        public int[] data;
        public int data_len;
        public long timestamp;
    }

    // Native API start
    public native int nativeDsInitHandle(String rtmpUrl,
                                         int width,
                                         int height,
                                         int framerate,
                                         int bitrate,
                                         int useSoftEncode,
                                         int useTcpAdapter,
                                         String savePath);

    public native void nativeDestroyHandle();

    //##video
    //raw yuv
    public native int nativeDsSendRawVideoDataByte(byte[] data,
                                                   int data_len,
                                                   int width,
                                                   int height,
                                                   int stride,
                                                   long timestamp);

    //raw rgb
    public native int nativeDsSendRawVideoDataInt(int[] data,
                                                  int data_len,
                                                  int width,
                                                  int height,
                                                  int stride,
                                                  long timestamp);

    //encoded data
    public native int nativeDsSendEncodedVideoData(byte[] data, int data_len, long timestamp, int isCfg);

    //##audio
    public native int nativeDsSendEncodedAudioData(byte[] data, int data_len, long timestamp, int isCfg);

    public native int nativeDsSendRawAudioDataByte(byte[] data,
                                                   int data_len,
                                                   long timestamp);


    byte[] h264;// = new byte[iWidth*iHeight*3/2];
    byte[] tmpData;// = new byte[iWidth*iHeight*3/2];

    public UploadThread(int width, int height) {
        int len = width * height * 3 / 2;
        h264 = new byte[len];
        tmpData = new byte[len];
        Log.e("tmpData", "tmpData:" + tmpData.length);
        iWidth = width;
        iHeight = height;
        startTimestamp = System.currentTimeMillis();
    }

    private void processData(MyData obj) {
        // TODO 上传数据
        if (useHWEncode == 1) {

//            int ret = offerEncoder(obj.data, h264);
//            if (ret > 0) {
//                nativeDsSendEncodedVideoData(h264, ret, 0, 0);
//            }
        } else {
            nativeDsSendRawVideoDataInt(obj.data, obj.data_len, iWidth, iHeight, iWidth, obj.timestamp);

//            byte[] mBufferByte = new byte[obj.data_len * 4];
//            int idx = 0;
//            for (int i = 0; i < iHeight; i++) {
//                for (int j = 0; j < iWidth; j++) {
//                    int v = obj.data[(iHeight - i - 1) * iWidth + j];
//                    mBufferByte[idx++] = (byte) (v >> 16);
//                    mBufferByte[idx++] = (byte) (v >> 8);
//                    mBufferByte[idx++] = (byte) v;
//                    mBufferByte[idx++] = (byte) (v >> 24);
//                }
//            }
//            nativeDsSendRawVideoDataByte(mBufferByte, mBufferByte.length, iWidth, iHeight, iWidth, obj.timestamp);

        }


//		testJniData(data, data.length);
//		NV21ToNV12(data, tmpData, 640, 480);
//		int ret = offerEncoder(tmpData, h264);
//		if(ret > 0)
//		{
//			testJniSendPacket(h264, ret, 0);
//		}
//		Log.e("UploadThread", "processData finish");
    }

    public void addData(byte[] data, int isCfg) {
        long timestamp = System.currentTimeMillis() - startTimestamp;

        nativeDsSendEncodedVideoData(data, data.length, timestamp, isCfg);
    }

    public void addData(int[] data, int timestamp) {

//        byte[] buffer = mBufferByteQueue.poll();
//        if (buffer == null) {
//            if (mBufferNewCount < BUFFER_MAX_SIZE) {
//                buffer = new byte[mBytesLen];
//                mBufferNewCount++;
//            } else {
//                return;
//            }
//        }

        MyData obj = new MyData();
        obj.data = data;
        //System.arraycopy(data, 0, obj.data, 0, data.length);
        obj.data_len = data.length;
        obj.timestamp = System.currentTimeMillis() - startTimestamp;

        //boolean ret = mDataQueue.offer(obj);

        processData(obj);
        //Log.e("UploadThread", "offer data:" + ret + " timestamp: " + obj.timestamp);
    }

    public void setStop() {

    }

    private MediaCodec mediaCodec;
    int m_width;
    int m_height;
    byte[] m_info = null;


    private byte[] yuv420 = null;

    public void AvcEncoder(int width, int height, int framerate, int bitrate) {

        m_width = width;
        m_height = height;
        yuv420 = new byte[width * height * 3 / 2];
        String mime = "video/avc";
        try {
            mediaCodec = MediaCodec.createEncoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(mime, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);

        showSupportedColorFormat();
//		Log.e("yxx", "getDefaultFormat:"+MediaCodecInfo.CodecCapabilities.getDefaultFormat().toString);
        //23以后deprecated COLOR_FormatYUV420Planar,用下面这个
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); //关键帧间隔时间 单位s
//		mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);//不加这个会crash
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
    }

    public void showSupportedColorFormat() {

        String mimeType = "video/avc";
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
//轮询所要的解码器
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals("video/avc")) {
                    System.out.println("found");
                    found = true;
                }
            }
            if (!found)
                continue;
            codecInfo = info;
        }
        Log.d(TAG, "Found " + codecInfo.getName() + " supporting " + "video/avc");
        int colorFormat = 0;
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length && colorFormat == 0; i++) {
            int format = capabilities.colorFormats[i];
            Log.e(TAG, "Using color format " + format);
        }
    }

    public void close() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public int offerEncoder(byte[] input, byte[] output) {
        int pos = 0;
//		swapYV12toI420(input, yuv420, m_width, m_height);
        NV21ToNV12(input, yuv420, iWidth, iHeight);
        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(yuv420);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv420.length, 0, 0);
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//				Log.e("outputBuffer", "outputBuffer sie:"+bufferInfo.size);
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);

                if (m_info != null) {
                    System.arraycopy(outData, 0, output, pos, outData.length);
                    pos += outData.length;

                } else //保存pps sps 只有开始时 第一个帧里有， 保存起来后面用
                {
                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                    if (spsPpsBuffer.getInt() == 0x00000001) {
                        m_info = new byte[outData.length];
                        System.arraycopy(outData, 0, m_info, 0, outData.length);
                        nativeDsSendEncodedVideoData(m_info, m_info.length, 0, 1);
//						testJniSendPacket(m_info, m_info.length, 1);
                    } else {
                        return -1;
                    }
                }

                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }

            if (output[4] == 0x65) //key frame   编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上
            {
                System.arraycopy(output, 0, yuv420, 0, pos);
                System.arraycopy(m_info, 0, output, 0, m_info.length);
                Log.e("test sps pps", "Get sps/pps len:" + m_info.length + "content:" + bytesToHexString(m_info));
                System.arraycopy(yuv420, 0, output, m_info.length, pos);
                pos += m_info.length;
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return pos;
    }

    //yv12 转 yuv420p  yvu -> yuv
    private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height) {
        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
        System.arraycopy(yv12bytes, width * height + width * height / 4, i420bytes, width * height, width * height / 4);
        System.arraycopy(yv12bytes, width * height, i420bytes, width * height + width * height / 4, width * height / 4);
    }

    //nv21==>>nv12
    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }

    //nv21==>>yuv420p
    public byte[] decodeYUV420SP2YUV420(byte[] data, int length, int width, int height) {

        byte[] str = new byte[length];
        System.arraycopy(data, 0, str, 0, width * height);

        int strIndex = width * height;

        for (int i = width * height + 1; i < length; i += 2) {
            str[strIndex++] = data[i];
        }
        for (int i = width * height; i < length; i += 2) {
            str[strIndex++] = data[i];
        }
        return str;
    } //YUV420SP2YUV420
}
