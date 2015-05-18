/*
 * Copyright (C) 2014 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of Netcetera AG, Switzerland.
 * The program(s) may be used and/or copied only with the written permission of Netcetera AG or
 * in accordance with the terms and conditions stipulated in the agreement/contract under which 
 * the program(s) have been supplied.
 */
package com.example.textrecoapp.gameplay;

import android.content.Context;

import com.example.textrecoapp.R;
import com.example.textrecoapp.characters.Character;


public final class GameplayUtils {
  
  public static String getDifficultyDescriptor(Context context, int level) {
    switch (level) {
      case Character.KNOWLEDGE_BASIC:
        return context.getResources().getString(R.string.knowledge_basic);

      case Character.KNOWLEDGE_INTERMEDIATE:
        return context.getResources().getString(R.string.knowledge_intermediate);

      case Character.KNOWLEDGE_SUPERIOR:
        return context.getResources().getString(R.string.knowledge_superior);

      case Character.KNOWLEDGE_EXPERT:
        return context.getResources().getString(R.string.knowledge_expert);
    }

    return "";
  }

}
