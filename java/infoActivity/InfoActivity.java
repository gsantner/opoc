/*
 * ---------------------------------------------------------------------------- *
 * Gregor Santner <gsantner.github.io> wrote this file. You can do whatever
 * you want with this stuff. If we meet some day, and you think this stuff is
 * worth it, you can buy me a coke in return. Provided as is without any kind
 * of warranty. No attribution required.                  - Gregor Santner
 *
 * License: Creative Commons Zero (CC0 1.0)
 *  http://creativecommons.org/publicdomain/zero/1.0/
 * ----------------------------------------------------------------------------
 */

 /*
 * Get updates:
 *  https://github.com/gsantner/onePieceOfCode/blob/master/java/infoActivity/InfoActivity.java
 * A simple activity to show information about the app.
 * Intended to use together: SimpleMarkdownParser, Helpers, SettingsActivity and it's xml-layout.
 */

package APPPACK.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import APPPACK.R;
import io.github.gsantner.opoc.util.Helpers;
import io.github.gsantner.opoc.util.HelpersA;

public class InfoActivity extends AppCompatActivity {
    //####################
    //##  Ui Binding
    //####################
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.info__activity__text_app_version)
    TextView textAppVersion;

    @BindView(R.id.info__activity__text_maintainers)
    TextView textMaintainers;

    @BindView(R.id.info__activity__text_contributors)
    TextView textContributors;


    //####################
    //##  Methods
    //####################
    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info__activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textMaintainers.setText(new SpannableString(Html.fromHtml(
                Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.maintainers, ""))));
        textMaintainers.setMovementMethod(LinkMovementMethod.getInstance());

        textContributors.setText(new SpannableString(Html.fromHtml(
                Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.contributors, "* ")
        )));
        textContributors.setMovementMethod(LinkMovementMethod.getInstance());


        // App version
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            textAppVersion.setText(getString(R.string.app_version_v, info.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.info__activity__text_app_version, R.id.info__activity__button_third_party_licenses, R.id.info__activity__button_app_license})
    public void onButtonClicked(View v) {
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.info__activity__text_app_version: {
                Helpers.get().openWebpageInExternalBrowser(getString(R.string.app_www_source));
                break;
            }
            case R.id.info__activity__button_app_license: {
                HelpersA.get(this).showDialogWithHtmlTextView(R.string.info__licenses, Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.license, ""));
                break;
            }
            case R.id.info__activity__button_third_party_licenses: {
                HelpersA.get(this).showDialogWithHtmlTextView(R.string.info__licenses, Helpers.get().loadMarkdownForTextViewFromRaw(R.raw.licenses_3rd_party, ""));
                break;
            }
        }
    }
}
