/*
 * Copyright 7/31/2016 Anthony Restaino
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package video.report.mediaplayer.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import video.report.mediaplayer.R
import video.report.mediaplayer.exten.dimen
import video.report.mediaplayer.util.DeviceUtils


object DownloaderDialog {

    @JvmStatic
    fun showPositiveNegativeDialog(
            activity: Activity,
            @StringRes title: Int,
            @StringRes message: Int,
            messageArguments: Array<Any>? = null,
            positiveButton: DialogItem,
            negativeButton: DialogItem,
            onCancel: () -> Unit
    ) {
        val messageValue = if (messageArguments != null) {
            activity.getString(message, *messageArguments)
        } else {
            activity.getString(message)
        }
        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(title)
            setMessage(messageValue)
            setOnCancelListener { onCancel() }
            setPositiveButton(positiveButton.title) { _, _ -> positiveButton.onClick() }
            setNegativeButton(negativeButton.title) { _, _ -> negativeButton.onClick() }
        }.show()

        setDialogSize(activity, dialog)
    }

    @JvmStatic
    fun showPositiveNegativeDialog(
            activity: Activity,
            @StringRes message: Int,
            messageArguments: Array<Any>? = null,
            positiveButton: DialogItem,
            negativeButton: DialogItem,
            onCancel: () -> Unit
    ) {
        val messageValue = if (messageArguments != null) {
            activity.getString(message, *messageArguments)
        } else {
            activity.getString(message)
        }
        val dialog = AlertDialog.Builder(activity).apply {
            setMessage(messageValue)
            setOnCancelListener { onCancel() }
            setPositiveButton(positiveButton.title) { _, _ -> positiveButton.onClick() }
            setNegativeButton(negativeButton.title) { _, _ -> negativeButton.onClick() }
            setCancelable(false)

        }.show()

        setDialogSize(activity, dialog)
    }

    @JvmStatic
    fun setDialogSize(context: Context, dialog: Dialog) {
        var maxWidth = context.dimen(R.dimen.dialog_max_size)
        val padding = context.dimen(R.dimen.dialog_padding)
        val screenSize = DeviceUtils.getScreenWidth(context)
        if (maxWidth > screenSize - 2 * padding) {
            maxWidth = screenSize - 2 * padding
        }
        val window = dialog.window
        window?.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @JvmStatic
    fun showPopMenu(context: Context, anchor: View, @MenuRes menu: Int, listener: PopupMenu.OnMenuItemClickListener) {
        showPopMenu(context, anchor, menu, listener, null)
    }

    @SuppressLint("RestrictedApi")
    @JvmStatic
    fun showPopMenu(context: Context, anchor: View, @MenuRes menu: Int,
                    listener: PopupMenu.OnMenuItemClickListener,
                    dismissListener: PopupMenu.OnDismissListener?) {
        val popup = PopupMenu(context, anchor)
        popup.menuInflater.inflate(menu, popup.menu)
        val lang = DeviceUtils.getSystemLanguage()
        if ("ur" == lang || "ar" == lang || "fa" == lang){
        } else {
            if (popup.menu is MenuBuilder) {
                (popup.menu as MenuBuilder).setOptionalIconsVisible(true)
            }
        }
        popup.setOnMenuItemClickListener(listener)
        popup.setOnDismissListener(dismissListener)
        popup.show()
    }


    @JvmStatic
    fun showNoImageDialog(
            activity: Activity,
            @StringRes title: Int,
            view: View,
            positiveButton: DialogItem,
            negativeButton: DialogItem?,
            onCancel: () -> Unit
    ) {
        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(title)
            setView(view)
            setOnCancelListener { onCancel() }
            setPositiveButton(positiveButton.title) { _, _ -> positiveButton.onClick() }
            if (negativeButton != null)
                setNegativeButton(negativeButton.title) { _, _ -> negativeButton.onClick() }

        }.show()
        setDialogSize(activity, dialog)
    }
}
