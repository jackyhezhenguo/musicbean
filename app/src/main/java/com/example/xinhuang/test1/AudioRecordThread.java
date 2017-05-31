package com.example.xinhuang.test1;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioRecordThread extends Thread {
    MediaRecorder mediaRecorder;
    boolean isRecord;
    String filePath;
    private static int sampleRateInHz = 8000;
    public UploadThread mUpload;
    public int sampleRate = 44100;
    public int sampleNum = 0;

    public AudioRecordThread(UploadThread mUploadTread) {
        mUpload = mUploadTread;
//		init(context);
    }

//	private void init(Context context) {
//		initLocalSocket();
//		filePath = context.getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/"
//				+ System.currentTimeMillis();
//		mediaRecorder = new MediaRecorder();
//		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
//		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//		mediaRecorder.setAudioSamplingRate(sampleRateInHz);
//		mediaRecorder.setOutputFile(sender.getFileDescriptor());
//		try {
//			mediaRecorder.prepare();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

    @Override
    public synchronized void start() {
//		mediaRecorder.start();
        isRecord = true;

        super.start();
    }

    public void setStop() {
        isRecord = false;
//		mediaRecorder.stop();
//		mediaRecorder.release();
//		mediaRecorder = null;
//		releaseLocalSocket();
    }

//	LocalServerSocket lss;
//	LocalSocket sender, receiver;
//	String serverName = "aacserver";
//
//	boolean initLocalSocket() {
//		try {
//			final int bufferSize = 1024;
//
//			lss = new LocalServerSocket(serverName);
//
//			receiver = new LocalSocket();
//			receiver.connect(new LocalSocketAddress(serverName));
//			receiver.setReceiveBufferSize(bufferSize);
//			receiver.setSendBufferSize(bufferSize);
//
//			sender = lss.accept();
//			sender.setReceiveBufferSize(bufferSize);
//			sender.setSendBufferSize(bufferSize);
//
//			return true;
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//
//	}
//
//	void releaseLocalSocket() {
//		try {
//			if (sender != null) {
//				sender.close();
//			}
//			if (receiver != null) {
//				receiver.close();
//			}
//			if (lss != null) {
//				lss.close();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		sender = null;
//		receiver = null;
//		lss = null;
//	}

    @Override
    public void run() {
//		try {
//			processAudio();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        audioRecord.startRecording();

        byte[] buffer = new byte[4096];
        while (isRecord) {
            try {
                int len = audioRecord.read(buffer, 0, buffer.length);
                if (len > 0) {
//				Log.e("audio", "Audio Record:"+len);
//						audioEncoder.fireAudio(buffer, len);
                    //sampleNum += 1024;

                    long tt = System.currentTimeMillis() - mUpload.startTimestamp;
                    mUpload.nativeDsSendRawAudioDataByte(buffer, buffer.length, tt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        audioRecord.stop();
    }

//	private void processAudio() throws Exception {
//		// TODO 处理数据，这里保存到文件
//		InputStream dataInput = receiver.getInputStream();
//		FileOutputStream fout = new FileOutputStream(new File(filePath));
//
//		byte[] sendBuffer = new byte[4096];
//
//		while (isRecord) {
//			Log.e("send audio", "ready read audio");
//			int s = dataInput.read(sendBuffer, 0, 4096);
//			if (s > 0) {
////				fout.write(sendBuffer, 0, s);
//				Log.e("send audio", "send: "+s);
//				mUpload.nativeDsSendData(2, sendBuffer, s, 0);
//			}
//		}
//
//		dataInput.close();
//		fout.close();
//	}

}
