package video.report.mediaplayer.ui.fragment;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    boolean isResumed = false;
    boolean isHint = true;
    boolean isHide = false;

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isResumed = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isHint = isVisibleToUser;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHide = hidden;
    }

    public boolean isFragmentForeground() {
        return isResumed && isHint && !isHide;
    }
}
