package com.example.todolist.data.ui;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class RecyclerViewMatcher {

    public static Matcher<View> atPositionOnView(final int position, final Matcher<View> itemMatcher,
                                                 final int targetViewId) {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has view id " + itemMatcher + " at position " + position);
            }

            @Override
            public boolean matchesSafely(final RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                View targetView = viewHolder != null ? viewHolder.itemView.findViewById(targetViewId) : null;
                return itemMatcher.matches(targetView);
            }
        };
    }
}
