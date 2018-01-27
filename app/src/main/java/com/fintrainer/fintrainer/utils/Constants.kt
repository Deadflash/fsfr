package com.fintrainer.fintrainer.utils

/**
 * Created by krotk on 21.10.2017.
 */

object Constants {

    const val APP_PNAME = "com.fintrainer.fintrainer"
    const val REALM_SERVER_HTTP_URL = "http://91.240.84.213:9080"
    const val REALM_SERVER__REALM_URL = "realm://91.240.84.213:9080"
    const val REALM_SERVER_AUTH = "/auth"
    const val REALM_SERVER_SCHEMA_VERSION = 4L
    const val REALM_SERVER_DISCUSSION_REALM = "/fsfr-discussion-test"
    const val REALM_SUCCESS_CONNECT_CODE = 2
    const val REALM_FAIL_CONNECT_CODE: Int = -1
    const val AUTH_INTERNET_CONNECTION_ERROR = 7
    const val REALM_SUCCES_CODE = 0
    const val REALM_ERROR_CODE = 1
    const val REALM_AUTH_TOKEN_ERROR = 611
    const val ACCOUNT_ERROR_CODE: Int = -1
    const val RC_SIGN_IN = 10

    const val DRAWER_MAIN_FRAGMENT_TAG = "drawer_main_fragment"
    const val TESTING_FRAGMENT_TAG = "testing_fragment"
    const val DISCUSSIONS_FRAGMENT_TAG = "discussions_fragment"
    const val ADD_DISCUSSIONS_FRAGMENT_TAG = "add_discussions_fragment"
    const val COMMENTS_FRAGMENT_TAG = "comments_fragment"

    const val TESTING_INTENT = 1
    const val EXAM_INTENT = 0
    const val CHAPTER_INTENT = 2
    const val SEARCH_INTENT =3
    const val FAVOURITE_INTENT = 4
    const val RESULT_INTENT = 5
    const val DISCUSSION_INTENT = 6
    const val FAILED_TESTS_INTENT = 7
    const val SETTINGS_INTENT = 8

    const val RANDOM_TESTS_COUNT = 100
    const val EXAM_PASS_VALUE = 100

    const val EXAM_BASE = 0
    const val EXAM_SERIAL_1 = 1
    const val EXAM_SERIAL_2 = 2
    const val EXAM_SERIAL_3 = 3
    const val EXAM_SERIAL_4 = 4
    const val EXAM_SERIAL_5 = 5
    const val EXAM_SERIAL_6 = 6
    const val EXAM_SERIAL_7 = 7

    const val DISLIKES_TO_HIDE_COMMENT: Int = -3

    const val CLEAR_FAVOURITE_PREF = 0
    const val CLEAR_STATISTICS_PREF = 1
}