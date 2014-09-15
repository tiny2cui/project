

package com.simit.video.stream;

import java.io.IOException;

import com.simit.video.server.MediaStream;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class VideoStream extends MediaStream {

	protected final static String TAG = "VideoStream";
	
	protected VideoQuality quality = VideoQuality.defaultVideoQualiy.clone();
	protected SurfaceHolder.Callback surfaceHolderCallback = null;
	protected SurfaceHolder surfaceHolder = null;
	protected boolean flashState = false,  qualityHasChanged = false, cameraError = false;
	protected int videoEncoder, cameraId;
	protected Camera camera;

	public VideoStream(int cameraId) {
		super();
		this.cameraId = cameraId;
	}
	
	public void stop() {
		if (streaming) {
			try {
				super.stop();
			} catch (RuntimeException e) {
				// stop() can throw a RuntimeException when called too quickly after start() !
				Log.d(TAG,"stop() called too quickly after start() but it's okay");
			} 
			try {
				// We reconnect to camera just to stop the preview
				if (camera != null) {
					camera.reconnect();
					camera.stopPreview();
				}
			} catch (IOException ignore) {}
		}
	}
	
	public void prepare() throws IllegalStateException, IOException {
		
		if (!cameraError) {
			if (camera == null) {
				//camera = Camera.open(cameraId);
				camera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
				
				camera.setErrorCallback(new ErrorCallback(){
					// Will be called if the media server dies
					// FIXME: In what thread is this called ? Concurrent use of camera may happen here ?!
					public void onError(int error, Camera cameraArg){
						Log.e(TAG, "Media server probably died !!");
						if (cameraArg != null) {
							cameraArg.release();
							cameraArg = null;
						}
						// We won't use a camera with the mediarecorder anymore
						cameraError = true;
					}
				});
			}

			// We reconnect to camera to change flash state if needed
			camera.reconnect();
			Parameters parameters = camera.getParameters();
			parameters.setFlashMode(flashState?Parameters.FLASH_MODE_TORCH:Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameters);
			camera.setDisplayOrientation(quality.orientation);
			camera.stopPreview();
			camera.unlock();
			super.setCamera(camera);
		}
		
		if (cameraError) {
			super.setCamera(null);
			super.reset();
			try {
				Thread.sleep(200);
			} catch (InterruptedException ignore) {}
		}
		
		// MediaRecorder should have been like this according to me:
		// all configuration methods can be called at any time and
		// changes take effects when prepare() is called
		super.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		super.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		if (mode==MODE_DEFAULT) {
			super.setMaxDuration(1000);
			super.setMaxFileSize(Integer.MAX_VALUE);
		} else {
			// On some phones a RuntimeException might be thrown :/
			try {
				super.setMaxDuration(0);
				super.setMaxFileSize(Integer.MAX_VALUE); 
			} catch (RuntimeException e) {
				Log.e(TAG,"setMaxDuration or setMaxFileSize failed !");
			}
		}
		super.setVideoEncoder(videoEncoder);
		super.setPreviewDisplay(surfaceHolder.getSurface());
		super.setVideoSize(quality.resX,quality.resY);
		super.setVideoFrameRate(quality.frameRate);
		super.setVideoEncodingBitRate(quality.bitRate);
		//super.setOrientationHint(quality.orientation); // FIXME: wrong orientation of the stream and setOrientationHint doesn't help
		super.prepare();
		
		// Reset flash state to ensure that default behavior is to turn it off
		flashState = false;
		
		// Quality has been updated
		qualityHasChanged = false;

	}
	
	/**
	 * Call this one instead of setPreviewDisplay(Surface sv) and don't worry about the SurfaceHolder.Callback
	 */
	public void setPreviewDisplay(SurfaceHolder sh) {
		surfaceHolder = sh;
		surfaceHolderCallback = new SurfaceHolder.Callback() {
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				Log.d(TAG,"Surface changed !");
				surfaceHolder = holder;
			}
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d(TAG,"Surface created !");
				surfaceHolder = holder;
			}
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (streaming) stop();
				Log.d(TAG,"Surface destroyed !");
			}
		};
		sh.addCallback(surfaceHolderCallback);
	}
	
	/** Turn flash on or off if phone has one */
	public void setFlashState(boolean state) {
		// Test if phone has a flash
		//if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			// Takes effect when configure() is called
			flashState = state;
		//}
	}
	
	public void setVideoSize(int width, int height) {
		if (quality.resX != width || quality.resY != height) {
			quality.resX = width;
			quality.resY = height;
			qualityHasChanged = true;
		}
	}
	
	public void setVideoFrameRate(int rate) {
		if (quality.frameRate != rate) {
			quality.frameRate = rate;
			qualityHasChanged = true;
		}
	}
	
	public void setVideoEncodingBitRate(int bitRate) {
		if (quality.bitRate != bitRate) {
			quality.bitRate = bitRate;
			qualityHasChanged = true;
		}
	}
	
	public void setVideoQuality(VideoQuality videoQuality) {
		if (!quality.equals(videoQuality)) {
			quality = videoQuality;
			qualityHasChanged = true;
		}
	}
	
	public void setVideoEncoder(int videoEncoder) {
		this.videoEncoder = videoEncoder;
	}
	
	public abstract String generateSessionDescriptor() throws IllegalStateException, IOException;

	public void release() {
		stop();
		if (camera != null) camera.release();
		if (surfaceHolderCallback != null && surfaceHolder != null) {
			surfaceHolder.removeCallback(surfaceHolderCallback);
		}
		super.release();
	}
	
}
