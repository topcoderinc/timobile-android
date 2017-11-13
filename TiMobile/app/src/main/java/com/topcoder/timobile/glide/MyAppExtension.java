package com.topcoder.timobile.glide;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.request.RequestOptions.decodeTypeOf;

/**
 * glide extension
 */
@GlideExtension public class MyAppExtension {
  private static final RequestOptions DECODE_TYPE_GIF = decodeTypeOf(GifDrawable.class).lock();

  private MyAppExtension() {
  } // utility class


}