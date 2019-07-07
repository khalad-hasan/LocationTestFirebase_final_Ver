<?xml version="1.0" encoding="utf-8"?>
<!--
**
** Copyright 2013, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
-->
<view xmlns:android="http://schemas.android.com/apk/res/android"
    class="android.support.v7.widget.ActivityChooserView$InnerLayout"
    android:id="@+id/activity_chooser_view_content"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:layout_gravity="center"
    style="?attr/activityChooserViewStyle">

    <FrameLayout
        android:id="@+id/expand_activities_button"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        android:focusable="true"
        android:addStatesFromChildren="true"
        android:background="?attr/actionBarItemBackground"
        android:paddingTop="2dip"
        android:paddingBottom="2dip"
        android:paddingLeft="12dip"
        android:paddingRight="12dip">

        <ImageView android:id="@+id/image"
            android:layout_width="32dip"
            android:layout_height="32dip"
            android:layout_gravity="center"
            android:scaleType="fitCenter"
            android:adjustViewBounds="true" />

    </FrameLayout>

    <FrameLayout
        android:id="@+id/default_activity_button"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        android:focusable="true"
        android:addStatesFromChildren="true"
        android:background="?attr/actionBarItemBackground"
        android:paddingTop="2dip"
        android:paddingBottom="2dip"
        android:paddingLeft="12dip"
        android:paddingRight="12dip">

        <