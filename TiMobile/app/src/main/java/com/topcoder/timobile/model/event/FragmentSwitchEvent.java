package com.topcoder.timobile.model.event;


import com.topcoder.timobile.baseclasses.BaseFragment;

import lombok.Getter;


public class FragmentSwitchEvent {

  @Getter
  BaseFragment baseFragment;

  @Getter int navId;

  public FragmentSwitchEvent(BaseFragment baseFragment, int navId) {
    this.baseFragment = baseFragment;
    this.navId = navId;
  }
}
