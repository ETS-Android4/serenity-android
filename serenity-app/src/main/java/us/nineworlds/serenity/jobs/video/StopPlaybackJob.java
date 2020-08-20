package us.nineworlds.serenity.jobs.video;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.birbit.android.jobqueue.RetryConstraint;
import javax.inject.Inject;
import us.nineworlds.serenity.common.android.injection.InjectingJob;
import us.nineworlds.serenity.common.rest.SerenityClient;

public class StopPlaybackJob extends InjectingJob {

  @Inject SerenityClient serenityClient;

  private String videoId;
  private long offset;

  public StopPlaybackJob(String videoId, long offset) {
    super();
    this.videoId = videoId;
    this.offset = offset;
  }

  @Override public void onAdded() {

  }

  @Override public void onRun() throws Throwable {
    serenityClient.stopPlaying(videoId, offset);
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

  }

  @Override
  protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
    return null;
  }
}
