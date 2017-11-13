package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class HelpFragment extends BaseFragment {

  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.pager) ViewPager pager;
  Unbinder unbinder;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_help, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.faq);
    setupViewPager(pager);
    tabLayout.setupWithViewPager(pager);
    changeTabsFont(tabLayout);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  private void setupViewPager(ViewPager viewPager) {
    ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
    adapter.addFragment(new FAQFragment(), "Lorum Topic 1");
    adapter.addFragment(new FAQFragment(), "Lorum Topic 2");
    adapter.addFragment(new FAQFragment(), "Lorum Topic 3");
    adapter.addFragment(new FAQFragment(), "Lorum Topic 4");
    viewPager.setAdapter(adapter);
  }

  static class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
      super(manager);
    }

    @Override public Fragment getItem(int position) {
      return mFragmentList.get(position);
    }

    @Override public int getCount() {
      return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
      mFragmentList.add(fragment);
      mFragmentTitleList.add(title);
    }

    @Override public CharSequence getPageTitle(int position) {
      return mFragmentTitleList.get(position);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.card_shop_menu, menu);
    MenuItem myActionMenuItem = menu.findItem(R.id.search);
    final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
    searchView.setOnSearchClickListener(v -> setTitle(R.string.empty_string));
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {

        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        //todo filter logic write here
        return false;
      }
    });
    searchView.setOnCloseListener(() -> {
      setTitle(R.string.faq);
      return false;
    });

    super.onCreateOptionsMenu(menu, inflater);
  }
}
