//package com.example.xinhuang.test1;
//
//import android.media.MediaRecorder;
//import android.net.LocalServerSocket;
//import android.net.LocalSocket;
//import android.net.LocalSocketAddress;
//import android.nfc.Tag;
//import android.util.Log;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//public class MediaRecordThread extends Thread {
//    MediaRecorder mediaRecorder;
//    boolean isRecord;
//    public UploadThread mUpload;
//
//    public MediaRecordThread(UploadThread mUploadTread) {
//        mUpload = mUploadTread;
//        init();
//    }
//
//    private void init() {
//        initLocalSocket();
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mediaRecorder.setVideoSize(640, 480);
//        mediaRecorder.setVideoFrameRate(20);
//        mediaRecorder.setVideoEncodingBitRate(600000);
//        mediaRecorder.setOutputFile(sender.getFileDescriptor());
//        try {
//            mediaRecorder.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public synchronized void start() {
//        mediaRecorder.start();
//        isRecord = true;
//
//        super.start();
//    }
//
//    public void setStop() {
//        isRecord = false;
//        mediaRecorder.stop();
//        mediaRecorder.release();
//        mediaRecorder = null;
//        releaseLocalSocket();
//    }
//
//    LocalServerSocket lss;
//    LocalSocket sender, receiver;
//    String serverName = "videoserver";
//
//    boolean initLocalSocket() {
//        try {
//            final int bufferSize = 500000;
//
//            lss = new LocalServerSocket(serverName);
//
//            receiver = new LocalSocket();
//            receiver.connect(new LocalSocketAddress(serverName));
//            receiver.setReceiveBufferSize(bufferSize);
//            receiver.setSendBufferSize(bufferSize);
//
//            sender = lss.accept();
//            sender.setReceiveBufferSize(bufferSize);
//            sender.setSendBufferSize(bufferSize);
//
//            return true;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//    }
//
//    void releaseLocalSocket() {
//        try {
//            if (sender != null) {
//                sender.close();
//            }
//            if (receiver != null) {
//                receiver.close();
//            }
//            if (lss != null) {
//                lss.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        sender = null;
//        receiver = null;
//        lss = null;
//    }
//
//    @Override
//    public void run() {
//        try {
//            processData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////		int minBufferSize = AudioRecord.getMinBufferSize(sampleRate,
////				AudioFormat.CHANNEL_IN_STEREO,
////				AudioFormat.ENCODING_PCM_16BIT);
////		AudioRecord audioRecord = new AudioRecord(
////				MediaRecorder.AudioSource.MIC, sampleRate,
////				AudioFormat.CHANNEL_IN_STEREO,
////				AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
////		audioRecord.startRecording();
////
////		while (isRecord) {
////			byte[] buffer = new byte[4096];
////			int len = audioRecord.read(buffer, 0, buffer.length);
////			if (len > 0) {
//////				Log.e("audio", "Audio Record:"+len);
//////						audioEncoder.fireAudio(buffer, len);
////				sampleNum += 1024;
////
////				mUpload.nativeDsEncodeAndSendData(2, buffer, buffer.length, sampleNum*1000/44100);
////			}
////		}
//    }
//
//
//    private InputStream mInputStream;
//
//    private void processData() throws Exception {
//        // TODO 处理数据，这里保存到文件
//        mInputStream = receiver.getInputStream();
//
//        try {
//            byte buffer[] = new byte[4];
//            // Skip all atoms preceding mdat atom
//            while (!Thread.interrupted()) {
//                while (mInputStream.read() != 'm') ;
//                if (buffer[0] == 'd' && buffer[1] == 'a' && buffer[2] == 't') {
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        while (isRecord) {
//
//            byte[] nalu = fillNalu();
//        }
//
//        mInputStream.close();
//    }
//
//    private int fill(byte[] buffer, int offset, int length) throws IOException {
//        int sum = 0, len;
//
//        while (sum < length) {
//            len = mInputStream.read(buffer, offset + sum, length - sum);
//            if (len < 0) {
//                throw new IOException("End of stream");
//            } else sum += len;
//        }
//        return sum;
//    }
//
//    private final static int MAX_VALID_NALU_LENGTH = 100000;
//    private int naluLength = 0;
//    private byte[] sps = null, pps = null;
//    private boolean sentConfig = false;
//
//
//    private byte[] fillNalu() throws IOException {
//        byte[] header = new byte[4];
//
//        // Read NAL unit length
//        fill(header, 0, 4);
//        naluLength = be32(header);
//
//        if (naluLength > MAX_VALID_NALU_LENGTH || naluLength < 0)
//            resync();
//
//        byte[] nalu = new byte[naluLength + header.length];
//        System.arraycopy(header, 0, nalu, 0, header.length);
//
//        fill(nalu, header.length, naluLength);    // We had fill the first byte
//        return nalu;
//    }
//
//    private void resync() throws IOException {
//        byte[] header = new byte[5];
//        int type;
//
//        while (true) {
//
//            header[0] = header[1];
//            header[1] = header[2];
//            header[2] = header[3];
//            header[3] = header[4];
//            header[4] = (byte) mInputStream.read();
//
//            type = header[4] & 0x1F;
//
//            if (type == 5 || type == 1) {
//                naluLength = be32(header);
//                if (naluLength > 0 && naluLength < MAX_VALID_NALU_LENGTH) {
//                    Log.e("mediarecord", "A NAL unit may have been found in the bit stream !");
//                    break;
//                }
//            }
//        }
//    }
//
//    public static byte[] be32(int val) {
//        byte[] buf = new byte[4];
//
//        buf[4] = (byte)(val & 0xff);
//        buf[2] = (byte)((val >> 8) & 0xff);
//        buf[1] = (byte)((val >> 16) & 0xff);
//        buf[0] = (byte)((val >> 24) & 0xff);
//        return buf;
//    }
//
//    public static int be32(byte[] buf) {
//        return (buf[3] & 0xFF) | ((buf[2] & 0xFF) << 8) | ((buf[1] & 0xFF) << 16) | ((buf[0] & 0xFF) << 24);
//    }
//
//    private void processNalu(byte[] nalu, long timestamp) throws IOException {
//        // TODO: 还需要处理分片的情况
//        int nalType = nalu[4] & 0x1F;
//        if (nalType == 5 && !sentConfig) {
//            Log.i("TAG", "Send configuration one time");
//            byte[] conf = configurationFromSpsAndPps();
//            writeVideoNalu(conf, System.currentTimeMillis(), 0, true);
//            sentConfig = true;
//        }
//        if (nalType == 7 || nalType == 8) {
//            Log.w("mediarecord", "Received SPS/PPS frame, ignored");
//        }
//        writeVideoNalu(nalu, timestamp, 1, (nalType == 5));
//    }
//
//    private byte[] configurationFromSpsAndPps() {
//        if (sps == null || pps == null) {
//            Log.e("TAG", "Invalid sps or pps");
//            throw new IllegalStateException("SPS|PPS missed");
//        }
//
//        IoBuffer conf = IoBuffer.allocate(9);
//        conf.setAutoExpand(true);
//        conf.put((byte)1);	// version
//        conf.put(sps[1]);	// profile
//        conf.put(sps[2]);	// compat
//        conf.put(sps[3]);	// level
//        conf.put((byte)0xff);	// 6 bits reserved + 2 bits nal size length - 1 (11)
//        conf.put((byte)0xe1); 	// 3 bits reserved + 5 bits number of sps (00001)
//
//        conf.put(be16((short)sps.length));
//        conf.put(sps);
//
//        conf.put((byte)1);
//        conf.put(be16((short)pps.length));
//        conf.put(pps);
//
//        return conf.array();
//    }
//
//    public static byte[] be16(short val)
//    {
//        byte[] buf = new byte[2];
//        buf[1] = (byte)(val & 0xff);
//        buf[0] = (byte)((val >> 8) & 0xff);
//
//        return buf;
//    }
//
//    /**
//     * Decode a big endian buffer to short value
//     * @param buf
//     * @return
//     */
//    public static short be16(byte[] buf) {
//        int value = (buf[1] & 0xFF) | ((buf[0] & 0xFF) << 8);
//        return (short)value;
//    }
//}
