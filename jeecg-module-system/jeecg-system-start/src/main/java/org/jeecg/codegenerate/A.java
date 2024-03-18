package org.jeecg.codegenerate;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.IOException;

/**
 * @author: create by qianshihua
 * @version: v1.0
 * @date:2024/3/17 15:15
 * @description:
 */
public class A {

    /**
     * flv转mp4
     *
     * @param inputFile  flv地址
     * @param outputFile mp4地址
     * @throws IOException
     * @throws InterruptedException
     */
    public static void flv2Mp4(String inputFile, String outputFile) throws IOException {
        long start = System.currentTimeMillis();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight());
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setAudioCodec(grabber.getAudioCodec());
//        recorder.setAudioChannels(grabber.getAudioChannels());
//        recorder.setAudioBitrate(grabber.getAudioBitrate());
        recorder.setSampleRate(grabber.getSampleRate());
        recorder.start();
        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            recorder.record(frame);
        }
        recorder.stop();
        grabber.stop();
//        log.info("flv转mp4 占用时长(毫秒): {} ", (System.currentTimeMillis() - start));
    }

    public static void main(String[] args) throws IOException {
        flv2Mp4("/Users/qianshihua/Downloads/兰泾花苑8#楼西单元东台-2024-02-27 19_41_42-困人.flv","/Users/qianshihua/Downloads/qian.mp4");
    }
}
