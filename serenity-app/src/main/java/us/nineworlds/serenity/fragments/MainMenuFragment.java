/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;

import net.ganin.darv.DpadAwareRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import us.nineworlds.plex.rest.PlexappFactory;
import us.nineworlds.serenity.GalleryOnItemClickListener;
import us.nineworlds.serenity.GalleryOnItemSelectedListener;
import us.nineworlds.serenity.MainMenuTextViewAdapter;
import us.nineworlds.serenity.R;
import us.nineworlds.serenity.core.menus.MenuItem;
import us.nineworlds.serenity.core.model.impl.MenuMediaContainer;
import us.nineworlds.serenity.events.ErrorMainMenuEvent;
import us.nineworlds.serenity.events.MainMenuEvent;
import us.nineworlds.serenity.injection.InjectingFragment;
import us.nineworlds.serenity.jobs.MainMenuRetrievalJob;

public class MainMenuFragment extends InjectingFragment {

    @Inject
    PlexappFactory plexFactory;

    @Inject
    EventBus eventBus;

    @Inject
    JobManager jobManager;

    private Unbinder unbinder;

    List<MenuItem> menuItems = new ArrayList<>();

    public MainMenuFragment() {
        setRetainInstance(false);
    }

    @BindView(R.id.mainGalleryMenu)
    DpadAwareRecyclerView mainGallery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_menu_view, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    protected void fetchData() {
        jobManager.addJobInBackground(new MainMenuRetrievalJob());
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchData();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    private void setupGallery() {
//		SerenityMenuGridLayoutManager layoutManager = new SerenityMenuGridLayoutManager.Builder(getActivity()).circular(true).orientation(ExtGridLayoutManager.HORIZONTAL).spanCount(1).build();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mainGallery.setLayoutManager(linearLayoutManager);

        MainMenuTextViewAdapter adapter = new MainMenuTextViewAdapter();
        adapter.menuItems = this.menuItems;
        mainGallery.setAdapter(adapter);
        mainGallery
                .setOnItemSelectedListener(new GalleryOnItemSelectedListener());
        mainGallery.setOnItemClickListener(new GalleryOnItemClickListener(
                getActivity()));
        mainGallery.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainMenuRetrievalResponse(MainMenuEvent event) {
        menuItems = new MenuMediaContainer(event.getMediaContainer()).createMenuItems();
        setupGallery();
        mainGallery.setVisibility(View.VISIBLE);
        mainGallery.requestFocus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainMenuErrorResponse(ErrorMainMenuEvent event) {
        MenuMediaContainer mc = new MenuMediaContainer(null);

        menuItems.add(mc.createSettingsMenu());
        menuItems.add(mc.createOptionsMenu());

        setupGallery();

        Toast.makeText(
                getActivity(),
                "Unable to connect to Plex Library at "
                        + plexFactory.baseURL(), Toast.LENGTH_LONG)
                .show();

        mainGallery.requestFocus();
    }
}
