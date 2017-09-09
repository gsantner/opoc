/*
 * ---------------------------------------------------------------------------- *
 * Gregor Santner <gsantner.github.io> wrote this file. You can do whatever
 * you want with this stuff. If we meet some day, and you think this stuff is
 * worth it, you can buy me a coke in return. Provided as is without any kind
 * of warranty. No attribution required.                  - Gregor Santner
 *
 * License of this file: Creative Commons Zero (CC0 1.0)
 *  http://creativecommons.org/publicdomain/zero/1.0/
 * ----------------------------------------------------------------------------
 */

/*
 * Place adblock hosts file in raw: src/main/res/raw/adblock_domains__xyz.txt
 * Always blocks both, www and non www

 * Load hosts (call e.g. via Application-Object)
AdBlock.getInstance().loadHostsFromRawAssetsAsync(context);

 * Override inside a WebViewClient:
public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    return AdBlock.getInstance().isAdHost(url)
            ? AdBlock.createEmptyResponse()
            : super.shouldInterceptRequest(view, url);
}
*/
package net.gsantner.opoc.util;

import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import APPPACK.R;

/**
 * Simple Host-Based AdBlocker
 */
@SuppressWarnings({"WeakerAccess", "SpellCheckingInspection", "unused"})
public class AdBlock {
    private static final AdBlock instance = new AdBlock();

    public static AdBlock getInstance() {
        return instance;
    }

    //########################
    //##
    //##     Members
    //##
    //########################
    private final Set<String> _adblockHostsFromRaw = new HashSet<>();
    private final Set<String> _adblockHosts = new HashSet<>();
    private boolean _isLoaded;

    //########################
    //##
    //##     Methods
    //##
    //########################
    private AdBlock() {
    }

    public boolean isAdHost(String urlS) {
        if (urlS != null && !urlS.isEmpty() && urlS.startsWith("http")) {
            try {
                URI url = new URI(urlS);
                String host = url.getHost().trim();
                if (host.startsWith("www.") && host.length() >= 4) {
                    host = host.substring(4);
                }
                return _adblockHosts.contains(host) || _adblockHosts.contains("www." + host);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public AdBlock reset() {
        _adblockHosts.clear();
        _adblockHosts.addAll(_adblockHostsFromRaw);
        return this;
    }

    public boolean isLoaded() {
        return _isLoaded;
    }

    public static WebResourceResponse createEmptyResponse() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }

    public void addBlockedHosts(String... hosts) {
        for (String host : hosts) {
            if (host != null) {
                host = host.trim();
                if (host.startsWith("www.") && host.length() >= 4) {
                    host = host.substring(4);
                }
                if (!host.startsWith("#") && !host.startsWith("\"")) {
                    _adblockHosts.add(host);
                }
            }
        }

    }

    public void loadHostsFromRawAssetsAsync(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadHostsFromRawAssets(context);
                    _isLoaded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadHostsFromRawAssets(Context context) throws IOException {
        BufferedReader br = null;
        String host;

        _adblockHosts.clear();
        for (int rawId : getAdblockIdsInRaw()) {
            try {
                br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rawId)));
                while ((host = br.readLine()) != null) {
                    addBlockedHosts(host);
                }
            } catch (Exception e) {
                Log.d(AdBlock.class.getName(), "Error: Cannot read adblock res " + rawId);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        _adblockHostsFromRaw.clear();
        _adblockHostsFromRaw.addAll(_adblockHosts);
    }

    private List<Integer> getAdblockIdsInRaw() {
        ArrayList<Integer> adblockResIds = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            try {
                int resId = field.getInt(field);
                String resFilename = field.getName();
                if (resFilename.startsWith("adblock_domains__")) {
                    adblockResIds.add(resId);
                }
            } catch (IllegalAccessException | IllegalArgumentException ignored) {
            }
        }
        return adblockResIds;
    }
}
