package tv.mta.flutter_playout.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import tv.mta.flutter_playout.R;

public class PlayerView implements PlatformView, MethodChannel.MethodCallHandler {

    private final PlayerLayout player;

    private Activity activity;

    public static final String TAG = "eventchannelsample";

    private Disposable timerSubscription;

    private PublishSubject<Boolean> fullscreenSubject = PublishSubject.create();

    PlayerView(Context context, Activity activity, int id, BinaryMessenger messenger, Object args) {

        new MethodChannel(messenger, "tv.mta/NativeVideoPlayerMethodChannel_" + id)
                .setMethodCallHandler(this);

       /* new EventChannel(messenger, "com.amond.eventchannelsample/stream_" + id).setStreamHandler(
                new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object args, final EventChannel.EventSink events) {
                        Log.d("PlayerView", "onListen");

                        timerSubscription = fullscreenSubject.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(
                                        new Consumer<Boolean>() {
                                            @Override
                                            public void accept(Boolean timer) throws Exception {
                                                Log.w(TAG, "emitting timer event " + timer);
                                                events.success(timer);
                                            }
                                        },
                                        new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable error) throws Exception {
                                                Log.e(TAG, "error in emitting timer", error);
                                                events.error("STREAM", "Error in processing observable", error.getMessage());
                                            }
                                        },
                                        new Action() {
                                            @Override
                                            public void run() throws Exception {
                                                Log.w(TAG, "closing the timer observable");
                                            }
                                        }
                                );
                    }

                    @Override
                    public void onCancel(Object args) {
                        Log.d("PlayerView", "onCancel");
                    }
                }
        );*/

        this.activity = activity;
        player = new PlayerLayout(context, activity, messenger, id, args);
    }



    private Activity getActivity() {
        Context context = getView().getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @Override
    public View getView() {
        return player;
    }

    @Override
    public void dispose() {
        player.onDestroy();
    }

    @Override
    public void onMethodCall(MethodCall call, @NotNull MethodChannel.Result result) {
        switch (call.method) {
            case "onMediaChanged":
                player.onMediaChanged(call.arguments);
                result.success(true);
                break;
            case "onShowControlsFlagChanged":
                player.onShowControlsFlagChanged(call.arguments);
                result.success(true);
                break;
            case "resume":
                player.play();
                result.success(true);
                break;
            case "pause":
                player.pause();
                result.success(true);
                break;
            case "setPreferredAudioLanguage":
                player.setPreferredAudioLanguage(call.arguments);
                result.success(true);
                break;
            case "seekTo":
                player.seekTo(call.arguments);
                result.success(true);
                break;
            case "dispose":
                dispose();
                result.success(true);
                break;
            default:
                result.notImplemented();
        }
    }
}