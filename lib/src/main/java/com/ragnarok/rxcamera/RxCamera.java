package com.ragnarok.rxcamera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.TextureView;

import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.request.RxCameraRequestBuilder;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by ragnarok on 15/10/25.
 */
public class RxCamera  {

    private static final String TAG = "RxCamera";

    private RxCameraInternal cameraInternal = new RxCameraInternal();

    /**
     * open the camera
     * @param context
     * @param config
     * @return
     */
    public static Observable<RxCamera> open(final Context context, final RxCameraConfig config) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                RxCamera rxCamera = new RxCamera(context, config);
                if (rxCamera.cameraInternal.openCameraInternal()) {
                    subscriber.onNext(rxCamera);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(rxCamera.cameraInternal.openCameraException());
                }
            }
        });
    }

    /**
     * open camera and start preview, bind a {@link SurfaceView}
     * @param context
     * @param config
     * @param surfaceView
     * @return
     */
    public static Observable<RxCamera> openAndStartPreview(Context context, RxCameraConfig config, final SurfaceView surfaceView) {
        return open(context, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.bindSurface(surfaceView);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.startPreview();
            }
        });
    }

    /**
     * open camera and start preview, bind a {@link TextureView}
     * @param context
     * @param config
     * @param textureView
     * @return
     */
    public static Observable<RxCamera> openAndStartPreview(Context context, RxCameraConfig config, final TextureView textureView) {
        return open(context, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.bindTexture(textureView);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.startPreview();
            }
        });
    }

    private RxCamera(Context context, RxCameraConfig config) {
        this.cameraInternal = new RxCameraInternal();
        this.cameraInternal.setConfig(config);
        this.cameraInternal.setContext(context);
    }

    /**
     * bind a {@link SurfaceView} as the camera preview surface
     * @param surfaceView
     * @return
     */
    public Observable<RxCamera> bindSurface(final SurfaceView surfaceView) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                boolean result = cameraInternal.bindSurfaceInternal(surfaceView);
                if (result) {
                    subscriber.onNext(RxCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.bindSurfaceFailedException());
                }
            }
        });
    }

    /**
     * bind a {@link TextureView} as the camera preview surface
     * @param textureView
     * @return
     */
    public Observable<RxCamera> bindTexture(final TextureView textureView) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                boolean result = cameraInternal.bindTextureInternal(textureView);
                if (result) {
                    subscriber.onNext(RxCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.bindSurfaceFailedException());
                }
            }
        });
    }

    /**
     * start preview, must be called after bindTexture or bindSurface
     * @return
     */
    public Observable<RxCamera> startPreview() {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                boolean result = cameraInternal.startPreviewInternal();
                if (result) {
                    subscriber.onNext(RxCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.startPreviewFailedException());
                }
            }
        });
    }

    /**
     * close the camera, return an Observable as the result
     * @return
     */
    public Observable<Boolean> closeCameraWithResult() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(cameraInternal.closeCameraInternal());
                subscriber.onCompleted();
            }
        });
    }

    /**
     * return a {@link RxCameraRequestBuilder} which you can request the camera preview frame data
     * @return
     */
    public RxCameraRequestBuilder request() {
        return new RxCameraRequestBuilder(this);
    }

    /**
     * directly close the camera
     * @return true if close success
     */
    public boolean closeCamera() {
        return cameraInternal.closeCameraInternal();
    }

    public boolean isOpenCamera() {
        return cameraInternal.isOpenCamera();
    }

    public boolean isBindSurface() {
        return cameraInternal.isBindSurface();
    }

    public RxCameraConfig getConfig() {
        return cameraInternal.getConfig();
    }

    public Camera getNativeCamera() {
        return cameraInternal.getNativeCamera();
    }

    public void installPreviewCallback(OnRxCameraPreviewFrameCallback previewCallback) {
        this.cameraInternal.installPreviewCallback(previewCallback);
    }

    public void uninstallPreviewCallback(OnRxCameraPreviewFrameCallback previewCallback) {
        this.cameraInternal.uninstallPreviewCallback(previewCallback);
    }

    public void installOneShotPreviewCallback(OnRxCameraPreviewFrameCallback previewFrameCallback) {
        this.cameraInternal.installOneShotPreviewCallback(previewFrameCallback);
    }

    public void uninstallOneShotPreviewCallback(OnRxCameraPreviewFrameCallback previewFrameCallback) {
        this.cameraInternal.uninstallOneShotPreviewCallback(previewFrameCallback);
    }
}
