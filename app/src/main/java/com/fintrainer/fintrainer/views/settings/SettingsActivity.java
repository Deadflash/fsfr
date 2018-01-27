//package com.fintrainer.fintrainer.views.settings;
//
//import android.app.Fragment;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.preference.Preference;
//import android.preference.PreferenceFragment;
//import android.support.annotation.Nullable;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.TextView;
//
//import com.fintrainer.fintrainer.R;
//import com.fintrainer.fintrainer.constants.GlobalConstants;
//import com.fintrainer.fintrainer.structure.AverageGradeStatisticDto;
//import com.fintrainer.fintrainer.structure.FavouriteQuestionsDto;
//import com.fintrainer.fintrainer.views.BaseActivity;
//
//import io.realm.Realm;
//import io.realm.RealmResults;
//
//import static com.fintrainer.fintrainer.utils.Constants.APP_PNAME;
//import static com.fintrainer.fintrainer.utils.Constants.CLEAR_FAVOURITE_PREF;
//import static com.fintrainer.fintrainer.utils.Constants.CLEAR_STATISTICS_PREF;
//import static com.fintrainer.fintrainer.utils.Constants.EXAM_BASE;
//
//public class SettingsActivity extends BaseActivity {
//
//    private Toolbar toolbar;
//    private Fragment fragment;
//    Integer examType;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.toolbar_layout);
//
//        setResult(RESULT_OK, new Intent());
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Настройки");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        examType = getIntent().getIntExtra("type", -1);
//
//        if (savedInstanceState == null) {
//            fragment = new Settings();
//            getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, "settings").commit();
//        } else {
//            fragment = getFragmentManager().findFragmentByTag("settings");
//        }
//    }
//
//    public static class Settings extends PreferenceFragment {
//
//        Preference clearFavourite;
//        Preference clearStatistics;
//        Preference like;
//        Preference info;
//
//        @Override
//        public void onCreate(@Nullable Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.main_prefs);
//            clearFavourite = findPreference("key_clear_favourite");
//            clearStatistics = findPreference("key_remove_statistics");
//            info = findPreference("key_information");
//            like = findPreference("key_like");
//
//            clearFavourite.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    showDialog(getString(R.string.clear_favourite), getString(R.string.clear_favourite_dialog), CLEAR_FAVOURITE_PREF);
//                    return true;
//                }
//            });
//            clearStatistics.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    showDialog(getString(R.string.remove_statistics), getString(R.string.clear_statistic_dialog), CLEAR_STATISTICS_PREF);
//                    return true;
//                }
//            });
//            info.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    showInfoDialog();
//                    return true;
//                }
//            });
//            like.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PNAME)));
//                    return true;
//                }
//            });
//        }
//
//        private void showInfoDialog() {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle(R.string.info);
//            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_get_premium, null);
//            ((TextView) view.findViewById(R.id.tvDescription)).setText(getResources().getString(R.string.programDescription));
//            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.setView(view);
//            builder.show();
//        }
//
//        private void showDialog(String title, String message, final Integer type) {
//            final Integer examType = ((SettingsActivity) getActivity()).examType;
//            String description = message + setDialogMessage(examType);
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//            dialog.setTitle(title);
//            dialog.setMessage(description);
//            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (type.equals(CLEAR_FAVOURITE_PREF)) {
//                        clearFavourite(examType);
//                    } else {
//                        if (type.equals(CLEAR_STATISTICS_PREF)) {
//                            clearStatistics(examType);
//                        }
//                    }
//                    dialog.dismiss();
//                }
//            });
//            dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//        }
//
//        private String setDialogMessage(Integer examId) {
//            String message = "";
//            if (examId != null) {
//                if (examId.equals(EXAM_BASE)) {
//                    message = getString(R.string.clear_base_exam_description);
//                } else {
//                    message = " \"" + examId + getString(R.string.clear_serial_exam_description);
//                }
//            }
//            return message;
//        }
//
//        private void clearFavourite(final Integer examType) {
//            if (examType != null) {
//                Realm realm = Realm.getInstance(((FinTrainerApplication) getActivity().getApplication()).statisticConf);
//                try {
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            RealmResults results = realm.where(FavouriteQuestionsDto.class).equalTo("type", examType).findAll();
//                            if (results.isEmpty()) {
//                                showSnackBar(getString(R.string.favourite_is_empty));
//                            } else {
//                                results.deleteAllFromRealm();
//                                showSnackBar(getString(R.string.favourite_cleared));
//                            }
//                        }
//                    });
//                } finally {
//                    realm.close();
//                }
//            }
//        }
//
//        private void showSnackBar(String message) {
//            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
//        }
//
//        private void clearStatistics(final Integer examType) {
//            if (examType != null) {
//                Realm realm = Realm.getInstance(((FinTrainerApplication) getActivity().getApplication()).statisticConf);
//                try {
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            RealmResults results = realm.where(AverageGradeStatisticDto.class).equalTo("testType", examType).findAll();
//                            if (results.isEmpty()) {
//                                showSnackBar(getString(R.string.statistics_is_empty));
//                            } else {
//                                results.deleteAllFromRealm();
//                                showSnackBar(getString(R.string.statistics_cleared));
//                            }
//                        }
//                    });
//                } finally {
//                    realm.close();
//                }
//            }
//        }
//    }
//}
