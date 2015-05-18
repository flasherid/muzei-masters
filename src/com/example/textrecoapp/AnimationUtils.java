/*
 * Copyright (C) 2014 by Netcetera AG. All rights reserved. The copyright to the computer program(s)
 * herein is the property of Netcetera AG, Switzerland. The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been supplied.
 */
package com.example.textrecoapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public final class AnimationUtils {

  public static void fadeInView(View view) {
    view.setAlpha(0f);
    view.setVisibility(View.VISIBLE);
    view.animate().alpha(1f);
  }

  public static void fadeOutView(final View view) {
    view.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {

      public void onAnimationEnd(Animator animation) {
        view.setVisibility(View.INVISIBLE);
        view.setAlpha(1f);
        view.animate().setListener(null);
      }
    });
  }
}
