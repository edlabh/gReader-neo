# Inventário de preferências (gerado)

### pref_article_controls.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `auto_hide_item_controls` | CheckBox | true |  | Auto hide controls |
| `show_navigation_bar` | CheckBox | true |  | Show navigation bar |
| `show_item_bar` | CheckBox | true |  | Show item bar |
| `show_zoom_button` | CheckBox | true |  | Zoom buttons |
| `item_volume_key_action` | List | 1 | 0, 1, 2 | Volume key (article view) |
| `item_navigation_gesture` | List | 1 | 0, 1 | Navigation gestures |
| `item_double_tap_action` | List | 9 | 0, 1, 3, 4, 5, 14, 9, 13 | Double tap |
| `item_inapp_browser` | CheckBox | true |  | In-app Browser |
| `item_inapp_youtube` | CheckBox | true |  | In-app Youtube player |

### pref_backup.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### pref_cache.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `db_cache_location` | List | local | local, sdcard | Database storage |
| `cache_auto_clean_read` | List | 1 | 0, 1, 2, 3, 5, 7, 14, 28, -1 | Auto cleanup read items |
| `cache_auto_clean_unread` | List | -1 | 1, 2, 3, 5, 7, 14, 28, -1 | Auto cleanup unread items |
| `cache_keep_starred` | CheckBox | true |  | Keep starred items |
| `cache_keep_cached` | CheckBox | false |  | Keep cached items |
| `cache_auto_cleanup_images` | List | 1000 | 0, 100, 200, 500, 1000, 2000, -1 | Auto cleanup image cache |

### pref_look_and_feel.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `application_language` | List |  | , az, da, de, en, es, it, nl, ru, sk, sl, fr, gl, iw, hu, ko, lt, pl, pt_PT, pt_BR, tr, uk, ug, sv, cs, bg, ca, vi, ar, zh_CN, zh_TW, zh_HK, ja | Languages |
| `application_theme` | List | 0 | 0, 5, 3, 1, 2 | Themes |
| `application_theme_auto_dark` | CheckBox | true |  | Auto Dark Mode |
| `application_theme_dark` | List | 1 | 1, 2 | Dark Theme |
| `fullscreen` | CheckBox | false |  | Fullscreen |
| `use_phone_ui` | CheckBox | false |  | Use phone UI |
| `home_landscape_dual_pane` | CheckBox | true |  | Landscape dual view |
| `home_hide_article_list` | CheckBox | false |  | Hide article list pane |
| `item_list_volume_key_action` | List | 1 | 0, 1, 2 | Volume key (article list) |
| `item_list_font_size` | List | 16 | 12, 14, 16, 18, 20 | List font size |
| `item_nav_bar` | List | 1 | 0, 1, 2 | Navigation bar |
| `item_pinch_zoom` | CheckBox | false |  | Pinch zoom |
| `item_hardware_accelerated` | CheckBox | true |  | Hardware acceleration |

### pref_mark_read.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `fab_position` | List | 2 | 0, 1, 2 | Position |
| `fab_style` | List | 1 | 1, 2 | Style |
| `fab_opacity` | List | 100 | 0, 25, 50, 75, 100 | Opacity |
| `show_feeds_after_mark_all` | CheckBox | false |  | Show feeds after Mark all |
| `confirm_mark_as_read_all` | CheckBox | true |  | All items |
| `confirm_mark_as_read_sub` | CheckBox | false |  | Feed or Tag |

### pref_menu.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### pref_notification.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `sync_notifiable` | CheckBox | true |  | Notification |
| `sync_notify_sound` | CheckBox |  |  | Sound |
| `sync_notify_sound_ringtone` | Ringtone | content://settings/system/notification_sound |  | Notification sound |
| `sync_notify_vibrate` | CheckBox |  |  | Vibration |
| `sync_notify_led` | CheckBox |  |  | LED |
| `sync_notifiable_custom` | CheckBox | false |  | Custom notification (PREMIUM) |
| `sync_notify_custom_sound` | CheckBox |  |  | Sound |
| `sync_notify_custom_sound_ringtone` | Ringtone | content://settings/system/notification_sound |  | Notification sound |
| `sync_notify_custom_vibrate` | CheckBox |  |  | Vibration |
| `sync_notify_custom_led` | CheckBox |  |  | LED |

### pref_other.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### pref_quick_actions.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `item_list_left_action` | List | 3 | 0, 1, 2, 3, 4, 5, 14, 6, 7, 10, 11, 12 | Left action |
| `item_list_right_action` | List | 1 | 0, 1, 2, 3, 4, 5, 14, 6, 7, 10, 11, 12 | Right action |
| `item_list_swipe_action_ltr` | List | 0 | 0, 1, 2, 3, 4 | Swipe Left to Right |
| `item_list_swipe_action_rtl` | List | 0 | 0, 1, 2, 3, 4 | Swipe Right to Left |

### pref_reading.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `item_images_loading` | List | 1 | 0, 1, 2 | Load images |
| `item_load_link` | List | 0 | 0, 1 | Load article link |
| `item_autoload_link` | CheckBox | false |  | Autoload article link |
| `item_autoload_reading_mode` | CheckBox | false |  | Autoload reading mode |
| `item_user_agent` | List | 0 | 0, 1, 2, 3 | User Agent |

### pref_rich_list.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `rich_article_list` | CheckBox | true |  | Enable Rich Article List |
| `rich_show_snippet` | CheckBox | true |  | Show Snippet |
| `rich_show_thumb` | List | 2 | 0, 1, 2 | Show Preview Thumbnail |

### pref_text_to_speech.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `service_tts_auto_detect_language` | CheckBox | true |  | Auto detect language |
| `service_tts_read_title` | CheckBox | false |  | Read title |
| `service_tts_mark_read` | CheckBox | false |  | Mark read |
