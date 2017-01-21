package android.nextlevel_global.com.popularmovies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Infinite scroll listener for RecyclerView.
 */
abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    private int visibleThreshold = 10;

    private int currentPage = 1;

    private int previousTotalItemCount = 0;

    private boolean loading = true;

    private final int startingPageIndex = 1;

    private final RecyclerView.LayoutManager mLayoutManager;

    InfiniteScrollListener(GridLayoutManager layoutManager, int currentPage) {
        this.currentPage = currentPage;
        mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int totalItemCount = mLayoutManager.getItemCount();
        int lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();

        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex;
            previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, recyclerView);
            loading = true;
        }
    }

    void resetState() {
        currentPage = startingPageIndex;
        previousTotalItemCount = 0;
        loading = true;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}
