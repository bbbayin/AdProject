package video.report.mediaplayer.firebase

//events 埋点的string
object Events {
    // app events
    const val EVENT_APP_ACTIVE = "app_active"
    const val EVENT_APP_SERVICE_ACTIVE = "app_service_active"

    // rate us events
    const val EVENT_SETTING_RATEUS_SHOW = "setting_rateus_show"
    const val EVENT_SETTING_RATEUS_LATER_CLICK = "setting_rateus_later_click"
    const val EVENT_SETTING_RATEUS_RATE_CLICK_1 = "setting_rateus_rate_click_1"
    const val EVENT_SETTING_RATEUS_RATE_CLICK_2 = "setting_rateus_rate_click_2"
    const val EVENT_SETTING_RATEUS_RATE_CLICK_3 = "setting_rateus_rate_click_3"
    const val EVENT_SETTING_RATEUS_RATE_CLICK_4 = "setting_rateus_rate_click_4"
    const val EVENT_SETTING_RATEUS_RATE_CLICK_5 = "setting_rateus_rate_click_5"
    const val EVENT_SETTING_RATEUS_FEEBACK = "setting_rateus_feeback"

    const val EVENT_RATEUS_SHOW = "rateus_show"
    const val EVENT_RATEUS_LATER_CLICK = "rateus_later_click"
    const val EVENT_RATEUS_RATE_CLICK_1 = "rateus_rate_click_1"
    const val EVENT_RATEUS_RATE_CLICK_2 = "rateus_rate_click_2"
    const val EVENT_RATEUS_RATE_CLICK_3 = "rateus_rate_click_3"
    const val EVENT_RATEUS_RATE_CLICK_4 = "rateus_rate_click_4"
    const val EVENT_RATEUS_RATE_CLICK_5 = "rateus_rate_click_5"
    const val EVENT_RATEUS_FEEBACK = "rateus_feeback"

    // broswer
    const val EVENT_SETTING_CLEARCOOKIES_ITEM = "setting_clear_cookies_click"
    const val EVENT_SETTING_CLEARCOOKIES_DIALOG_SHOW = "setting_clear_cookies_show"
    const val EVENT_SETTING_CLEARCOOKIES = "setting_clear_cookies_OK"

    // setting events
    const val EVENT_HOME_IG_CLICK = "home_ig_click"
    const val EVENT_HOME_MORE_HOWTO_CLICK = "home_more_howto_click"
    const val EVENT_HOME_MORE_SHAREAPP_CLICK = "home_more_shareapp_click"
    const val EVENT_HOME_MORE_FOLLOWUS_CLICK_ON = "home_more_followus_click"

    // home events
    const val EVENT_HOME_SHOW = "home_show"
    const val EVENT_HOME_TAB_CLICK = "home_tab_click"
    const val EVENT_HOME_INPUTBOX_CLICK = "home_inputbox_click"
    const val EVENT_HOME_PASTELINK_CLICK = "home_pastelink_click"
    const val EVENT_HOME_DOWNLOAD_CLICK = "home_download_click"
    const val EVENT_HOME_CONTENT_CLICK = "home_content_click"
    const val EVENT_HOME_RETRY_SHOW = "home_retry_show"
    const val EVENT_HOME_RETRY_CLICK = "home_retry_click"
    const val EVENT_HOME_RETRY_URL_SHOW = "home_retry_url"


    // downloads events
    const val EVENT_DOWNLOADS_TAB_CLICK = "downloads_tab_click"
    const val EVENT_DOWNLOADS_LISTMODE_SHOW = "downloads_listmode_show"
    const val EVENT_DOWNLOADS_FEEDMODE_SHOW = "downloads_feedmode_show"
    const val EVENT_DOWNLOADS_LISTMODE_CLICK = "downloads_listmode_click"
    const val EVENT_DOWNLOADS_FEEDMODE_CLICK = "downloads_feedmode_click"
    const val EVENT_DOWNLOADS_POST_CLICK = "downloads_post_click"
    const val EVENT_DOWNLOADS_POST_MORE_CLICK = "downloads_post_more_click"

    // share app events
    const val EVENT_SHARE_APP_SHOW = "share_app_show"
    const val EVENT_SHARE_APP_LATER_CLICK = "share_app_later_click"
    const val EVENT_SHARE_APP_SHARENOW_CLICK = "share_app_sharenow_click"

    // critical path events
    const val EVENT_PARSE_URL = "parse_url"
    const val EVENT_CHECK_URL = "check_url"
    const val EVENT_DOWNLOAD_SUCCESS = "download_success"
    const val EVENT_DOWNLOAD_FAIL = "download_fail"
    const val EVENT_DOWNLOADED_REVIEW_SHOW = "downloaded_review_show"
    const val EVENT_DOWNLOADED_PLAY_SHOW = "downloaded_play_show"
    const val EVENT_DOWNLOADED_ACTION = "downloaded_action"
    const val EVENT_EXTRACTOR_FAIL = "extractor_fail"
    const val EVENT_PASTE_URL_FAIL = "parse_url_fail"

    // parameter name
    const val PARAM_MSG = "msg_"
    const val PARAM_RESULT = "result"
    const val PARAM_ACTION = "action"
    const val PARAM_URL = "url"

    // parse url result
    const val PARSE_URL_RESULT_SUCCESS_COPYLINK = "success_copylink"
    const val PARSE_URL_RESULT_SUCCESS_SHARELINK = "success_sharelink"
    const val PARSE_URL_RESULT_SUCCESS_DOWNLOAD = "success_download"
    const val PARSE_URL_RESULT_SUCCESS_USERCOPY = "success_usercopy"
    const val PARSE_URL_RESULT_SUCCESS_RETRY = "success_retry"
    const val PARSE_URL_RESULT_FAIL_COPYLINK = "fail_copylink"
    const val PARSE_URL_RESULT_FAIL_SHARELINK = "fail_sharelink"
    const val PARSE_URL_RESULT_FAIL_DOWNLOAD = "fail_download"
    const val PARSE_URL_RESULT_FAIL_USERCOPY = "fail_usercopy"
    const val PARSE_URL_RESULT_FAIL_RETRY = "fail_retry"

    // check url result
    const val CHECK_URL_RESULT_SUCCESS = "success"
    const val CHECK_URL_RESULT_NETWORK_ERROR = "network_error"
    const val CHECK_URL_RESULT_UNKNOWN_RESOURCE = "unknown_resource"
    const val CHECK_URL_RESULT_ELEMENT_MISSING = "element_missing"
    const val CHECK_URL_RESULT_PRIVATE_POST = "private postEntity"
    const val CHECK_URL_RESULT_STORY_POST = "story_post"
    const val CHECK_URL_RESULT_USER_PROFILE = "user_profile"
    const val CHECK_URL_RESULT_NO_RESOURCE = "no_resource"
    const val CHECK_URL_RESULT_WRONG_URL = "wrong_url"
    const val CHECK_URL_RESULT_HIGHLIGHT_STORY_POST = "highlight_story_post"

    // download action
    const val DOWNLOAD_ACTION_HOME_HASHTAG = "home_hashtag"
    const val DOWNLOAD_ACTION_HOME_CAPTION = "home_caption"
    const val DOWNLOAD_ACTION_HOME_REPOST = "home_repost"
    const val DOWNLOAD_ACTION_HOME_VIEWINIG = "home_viewinig"
    const val DOWNLOAD_ACTION_HOME_SHARE = "home_share"
    const val DOWNLOAD_ACTION_DOWNLOADS_HASHTAG = "downloads_hashtag"
    const val DOWNLOAD_ACTION_DOWNLOADS_CAPTION = "downloads_caption"
    const val DOWNLOAD_ACTION_DOWNLOADS_REPOST = "downloads_repost"
    const val DOWNLOAD_ACTION_DOWNLOADS_VIEWINIG = "downloads_viewinig"
    const val DOWNLOAD_ACTION_DOWNLOADS_SHARE = "downloads_share"
    const val DOWNLOAD_ACTION_DELETE = "downloads_delete"
    const val DOWNLOAD_ACTION_VIEW_HASHTAG = "view_hashtag"
    const val DOWNLOAD_ACTION_VIEW_CAPTION = "view_caption"
    const val DOWNLOAD_ACTION_VIEW_REPOST = "view_repost"
    const val DOWNLOAD_ACTION_VIEW_VIEWINIG = "view_viewinig"
    const val DOWNLOAD_ACTION_VIEW_SHARE = "view_share"
    const val DOWNLOAD_ACTION_VIEW_DETELE = "view_detele"
    const val DOWNLOAD_ACTION_PLAY_HASHTAG = "play_hashtag"
    const val DOWNLOAD_ACTION_PLAY_CAPTION = "play_caption"
    const val DOWNLOAD_ACTION_PLAY_REPOST = "play_repost"
    const val DOWNLOAD_ACTION_PLAY_VIEWINIG = "play_viewinig"
    const val DOWNLOAD_ACTION_PLAY_SHARE = "play_share"
    const val DOWNLOAD_ACTION_PLAY_LOCK = "play_lock"
    const val DOWNLOAD_ACTION_PLAY_ROTATE = "play_rotate"
    const val DOWNLOAD_ACTION_PLAY_FORWARD = "play_forward"
    const val DOWNLOAD_ACTION_PLAY_BACKFORWARD = "play_backforward"

    // mcc property
    const val USER_PROPERTY_COUNTRY = "countrymcc"

    const val AD_FREE_RESULT = "adfree_result"

    const val SINGLE_POST_CLICK = "single_post_click"
    const val SINGLE_VIDEO_CLICK = "single_video_click"
    const val MULTIPLE_POST_CLICK = "multiple_post_clck"

    const val PRIVATE_LINK_SHOW = "private_link_show"
    const val PRIVATE_LINK_CLICK = "private_link_click"
    const val PRIVATE_LINK_CANCEL = "private_link_cancel"
    const val PRIVATE_LINK_LOGIN_SHOW = "private_link_login_show"
    const val PRIVATE_LINK_LOGIN_CANCEL = "private_link_login_cancel"
    const val PRIVATE_LINK_LOGIN_SUCCESS = "private_link_login_success"
    const val PRIVATE_LINK_IN_SUCCESS = "login_success"
    const val PRIVATE_LINK_LOGIN_FAIL = "login_fail"

    const val START_DOWNLOAD = "ad_switch_main_open"

    // Share MyApplication
    const val SETTING_SHAREAPP_SHOW = "shareapp_settings_show"
    const val SETTING_SHAREAPP_SHOW_LATER = "shareapp_settings_later"
    const val SETTING_SHAREAPP_SHOW_SHARENOW = "shareapp_settings_sharenow"
    const val SHAREAPP_POPUP_SHOW = "shareapp_popup_show"
    const val SHAREAPP_POPUP_SHOW_LATER = "shareapp_popup_later"
    const val SHAREAPP_POPUP_SHOW_NOW = "shareapp_popup_sharenow"
    const val INVOKE_SHAREAPP_SHOW_SHARENOW = "shareapp_popup_show"

    // setting
    const val AUTO_DOWNLOAD_DETECT = "auto_download_detect"
    const val AUTO_DOWNLOAD_NOTIFICATION_SHOW = "auto_download_notification_show"
    const val AUTO_DOWNLOAD_NOTIFICATION_CLICK = "auto_download_notification_click"
    const val HOME_MORE_SETTING_CLICK = "home_more_setting_click"
    const val SETTING_SHOW = "setting_show"
    const val SETTING_AUTO_DOWNLOAD_OFF_CLICK = "setting_auto_download_off_click"
    const val SETTING_AUTO_DOWNLOAD_ON_CLICK = "setting_auto_download_on_click"
    const val SETTING_DOWNLOAD_LOCATION_CLICK = "setting_download_location_click"
    const val SETTING_RATE_US_CLICK = "setting_rate_us_click"
    const val SETTING_FEEDBACK_CLICK = "setting_feedback_click"
    const val SETTING_PRIVACY_CLICK = "setting_privacy_click"
    const val SETTINGS_LOCATION_PAGE_SHOW = "setting_location_page_show"
    const val SETTING_DOWNLOAD_COVER_IMAGE_OFF_CLICK = "setting_download_cover_image_on_click"
    const val SETTING_DOWNLOAD_COVER_IMAGE_ON_CLICK = "setting_download_cover_image_off_click"

    // postView related
    const val AD_VIDEOEXIT_EXIT_COME = "ad_videoexit_exit_come"
    const val AD_POSTVIEW_SHOW = "ad_postview_adshow"
    const val AD_POSTVIEW_COME = "ad_postview_come"
    const val AD_POSTVIEW_EXIT_COME = "ad_postview_exit_come"
    const val AD_POSTVIEW_WITH_NETWORK = "ad_postview_with_network"
    const val AD_POSTVIEW_NO_NETWORK = "ad_postview_with_nonetwork"
    const val AD_POSTVIEW_CLOSE = "ad_postview_close"
    const val AD_POSTVIEW_OPEN = "ad_postview_open"

    // download ad related
    const val AD_CLICKDOWNLOAD_COME = "ad_clickdownload_come"

    const val NOTIFICATION_CLICK = "auto_download_notification_click"

    const val MOST_RECENT_CARD_SHOW = "most_recent_card_show"
    const val MOST_RECENT_CARD_CLICK = "most_recent_card_click"
    const val MOST_RECENT_CARD_DELETE = "most_recent_card_delete"

    const val AD_HOME_CLICK_SHOW = "ad_clickdownload_backup_adshow"
    const val AD_DONWLOADINTERS_ONRESUME_COME = "ad_clickdownload_onresume_come"
    const val AD_DONWLOADINTERS_ONRESUME_COME_MEETRULE = "ad_clickdownload_onresume_meetrule"
    const val AD_DONWLOADINTERS_ONRESUME_NOCACHE = "ad_clickdownload_onresume_fetch"
    const val AD_DONWLOADINTERS_ONRESUME_FILLED = "ad_clickdownload_onresume_filled"

    const val PRIVATE_LINK_LOGIN_RETRY_SHOW = "private_link_login_retry_show"

    //download_fail
    const val ERROR = "error"

    const val AD_APPEXIT_COME = "ad_appexit_come"
    const val AD_APPEXIT_AD_OPEN = "ad_appexit_ad_open"
    const val AD_APPEXIT_AD_CLOSE = "ad_appexit_ad_close"
    const val AD_APPEXIT_WITH_NO_NETWORK = "ad_appexit_with_no_network"
    const val AD_APPEXIT_WITH_NETWORK = "ad_appexit_with_network"
    const val AD_APPEXIT_ADSHOW = "ad_appexit_adshow"
    const val AD_APPEXIT_ADCLICK = "ad_appexit_adclick"
    const val AD_APPEXIT_ADSHOW_ADMOB = "ad_appexit_adshow_admob"
    const val AD_APPEXIT_ADSHOW_MOPUB = "ad_appexit_adshow_mopub"

    const val AD_HOMEPAGE_COME = "ad_homepage_come"
    const val AD_HOMEPAGE_AD_OPEN = "ad_homepage_ad_open"
    const val AD_HOMEPAGE_AD_CLOSE = "ad_homepage_ad_close"
    const val AD_HOMEPAGE_WITH_NO_NETWORK = "ad_homepage_with_no_network"
    const val AD_HOMEPAGE_WITH_NETWORK = "ad_homepage_with_network"
    const val AD_HOMEPAGE_ADSHOW = "ad_homepage_adshow"

    const val AD_DOWNLOADS_COME = "ad_downloads_come"
    const val AD_DOWNLOADS_AD_OPEN = "ad_downloads_ad_open"
    const val AD_DOWNLOADS_AD_CLOSE = "ad_downloads_ad_close"
    const val AD_DOWNLOADS_WITH_NO_NETWORK = "ad_downloads_with_no_network"
    const val AD_DOWNLOADS_WITH_NETWORK = "ad_downloads_with_network"
    const val AD_DOWNLOADS_ADSHOW = "ad_downloads_adshow"
    const val AD_NAME_DOWNLOADLIST = "downloadlist"
    const val AD_DOWNLOADSTAB_ADSHOW_ADMOB = "ad_downloadstab_adshow_admob"
    const val AD_DOWNLOADSTAB_ADSHOW_MOPUB = "ad_downloadstab_adshow_mopub"
    const val AD_VIDEOEXIT_ADSHOW_ADMOB = "ad_videoexit_adshow_admob"
    const val AD_VIDEOEXIT_ADSHOW_MOPUB = "ad_videoexit_adshow_mopub"

    const val AD_VIDEOEXIT_COME = "ad_videoexit_come"
    const val AD_VIDEOEXIT_AD_OPEN = "ad_videoexit_ad_open"
    const val AD_VIDEOEXIT_AD_CLOSE = "ad_videoexit_ad_close"
    const val AD_VIDEOEXIT_WITH_NO_NETWORK = "ad_videoexit_with_no_network"
    const val AD_VIDEOEXIT_WITH_NETWORK = "ad_videoexit_with_network"
    const val AD_VIDEOEXIT_ADSHOW = "ad_videoexit_adshow"

    const val AD_TAB_ADSHOW_HOME = "ad_tab_adshow_home"
    const val AD_TAB_ADSHOW_DOWNLOAD = "ad_tab_adshow_download"
    const val AD_TAB_ADSHOW_COPYTO = "ad_tab_adshow_copyto"
    const val AD_TAB_ADSHOW_SHARETO = "ad_tab_adshow_shareto"
    const val AD_TAB_ADSHOW_ADMOB = "ad_tab_adshow_admob"
    const val AD_TAB_ADSHOW_MOPUB = "ad_tab_adshow_mopub"

    const val AD_TAB_COME = "ad_tab_come"
    const val AD_TAB_COME_HOME = "ad_tab_come_home"
    const val AD_TAB_COME_DOWNLOADS = "ad_tab_come_downloads"
    const val AD_TAB_COME_COPYTO = "ad_tab_come_copyto"
    const val AD_TAB_COME_SHARETO = "ad_tab_come_shareto"
    const val AD_TAB_AD_OPEN = "ad_tab_ad_open"
    const val AD_TABAD_CLOSE = "ad_tabad_close"
    const val AD_TAB_WITH_NO_NETWORK = "ad_tab_with_no_network"
    const val AD_TAB_WITH_NETWORK = "ad_tab_with_network"
    const val AD_TAB_MEETRULE = "ad_tab_meetrule"
    const val AD_TAB_NOADS_REQUIREADS = "ad_tab_noads_requireads"
    const val AD_TAB_ADSHOW = "ad_tab_adshow"

    const val AD_POST_COME = "ad_post_come"
    const val AD_POST_AD_OPEN = "ad_post_ad_open"
    const val AD_POST_AD_CLOSE = "ad_post_ad_close"
    const val AD_POST_WITH_NO_NETWORK = "ad_post_with_no_network"
    const val AD_POST_WITH_NETWORK = "ad_post_with_network"
    const val AD_POST_ADSHOW = "ad_post_adshow"

    //Story
    const val STORY_LINK_SHOW = "story_link_show"
    const val STORY_LINK_CLICK = "story_link_click"
    const val STORY_LINK_CANCEL = "story_link_cancel"
    const val STORY_LINK_LOGIN_SHOW = "story_link_login_show"
    const val STORY_LINK_LOGIN_CANCEL = "story_link_login_cancel"
    const val STORY_LINK_LOGIN_SUCCESS = "story_link_login_success"
    const val STORY_LINK_LOGIN_FAIL = "story_link_login_fail"
    const val STORY_LINK_LOGIN_RETEY = "story_link_login_retry_show"

    const val BILLING_PROMOTE_SHOW = "adfree_windows_promote_show"
    const val BILLING_PROMOTE_BUY_CLICK = "adfree_windows_promote_buy_click"
    const val BILLING_PROMOTE_CANCEL_CLICK = "adfree_windows_promote_cancel_click"

    const val DOWNLOAD_START = "download_start"
    const val STORY_HIGHTLIGHT_CLICK = "story_highlight_post_click"

    const val AUTO_DOWNLOAD_PERMISSON_DENY = "auto_download_permisson_deny"
    const val SETTING_LANGUAGE_CLICK = "setting_language_click"
    const val SETTING_LANGUAGE_SELECT = "setting_language_select_click"
    const val SETTING_LANGUAGE_CANCEL = "setting_language_select_cancle"

    const val LISTMODE_LONG_PRESS = "listmode_long_press"
    const val FEEDMODE_LONG_PRESS = "feedmode_long_press"
    const val DOWNLOAD_SORTBT_CLICK = "download_sortby_click"
    const val DOWNLOAD_SELECT_CLICK = "download_select_click"
    const val DOWNLOAD_SORTBY_DATE_NEW_CLICK = "download_sortby_date_new_click"
    const val DOWNLOAD_SORTBY_DATE_OLD_CLICK = "download_sortby_date_old_click"
    const val DOWNLOAD_SORTBY_NAME_A_TO_Z_CLICK = "download_sortby_name_A_to_Z_click"
    const val DOWNLOAD_SORTBY_NAME_Z_TO_A_CLICK = "download_sortby_name_Z_to_A_click"
    const val EDITMODE_SHOW = "editmode_show"
    const val EDITMODE_SELECT_ALL = "editmode_select_all"
    const val EDITMODE_SHARE = "editmode_share"
    const val EDITMODE_DETELE = "editmode_delete"

    //vip
    const val SPLASH_SHARETO_NOTSHOW = "splashscreen_shareto_notshow"//Share to

    const val REDOWNLOAD_START = "redownload_start"
    const val REDOWNLOAD_SUCCESSFULLY = "redownload_successfully"
    const val REDOWNLOAD_FAILED = "redownload_failed"

    const val EVENT_DOWNLOADED_SELECT_CLICK="select_mode_click"

    const val EVENT_REWARD_VIDEO="event_reward_video"
    const val EVENT_REWARD_VIDEO_CANCEL="event_reward_video_cancel"
    const val LOGIN_PRIVACY_CLICK = "login_privacy_click"
    const val DRAW_TW_AD = "left_tw_ad"

}
