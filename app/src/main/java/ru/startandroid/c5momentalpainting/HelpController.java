package ru.startandroid.c5momentalpainting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Vyacheslav on 14.04.2019.
 */

public class HelpController {

    public static int LoadModeFromMenuActivity(Context context, SharedPreferences sPrefHelp, int mode)
    {
        int page_id = -1;
        switch (mode)
        {
            case 0: page_id = 0; break;
            case 1: page_id = 3; break;
            case 3: page_id = 4; break;
            case 2: page_id = 5; break;
            case 4: page_id = 7; break;
        }

        if (page_id != -1 && sPrefHelp.getBoolean("SHOW" + page_id, true))
        {
            SharedPreferences.Editor ed = sPrefHelp.edit();
            ed.putBoolean("SHOW" + page_id, false);
            int page_id_pref = sPrefHelp.getInt("PAGE_ID", 1);
            if (page_id_pref < page_id)
                ed.putInt("PAGE_ID", page_id);
            ed.commit();
            return page_id;
        }
        return -1;
    }

    public static void LoadLevelFromGameActivity(Context context, SharedPreferences sPrefHelp, int mode, int id_level)
    {
        int page_id = -1;
        switch (1000 * mode + id_level)
        {
            case 0000: page_id = 0; break;
            case 0007: page_id = 1; break;
            case 0013: page_id = 2; break;
            case 1000: page_id = 3; break;
            case 3000: page_id = 4; break;
            case 2000: page_id = 5; break;
            case 2020: page_id = 6; break;
            case 4000: page_id = 7; break;
        }

        if (page_id != -1 && sPrefHelp.getBoolean("SHOW" + page_id, true))
        {
            SharedPreferences.Editor ed = sPrefHelp.edit();
            ed.putBoolean("SHOW" + page_id, false);
            int page_id_pref = sPrefHelp.getInt("MAX_PAGE_ID", 2);
            if (page_id_pref < page_id)
                ed.putInt("MAX_PAGE_ID", page_id);
            ed.commit();

            Intent intent = new Intent(context, HelpActivity.class)
                    .putExtra("id_page", page_id);
            context.startActivity(intent);

        }
    }
}
