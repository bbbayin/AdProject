package video.report.mediaplayer.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;


public class DialogHelper {
    public static final int POSITIVE_BUTTON = 0;
    public static final int NEGATIVE_BUTTON = 1;

    private static String getString(Context context, int res) {
        return (res != 0) ? context.getString(res) : null;
    }

    private static String[] getString(Context context, int[] res) {
        String[] str = new String[res.length];
        for (int i = 0; i < res.length; i++)
            str[i] = context.getString(res[i]);
        return str;
    }

    public static void showDialog(Context context, int title, int content,
                                  int positive, int negative, Listener listener) {
        showDialog(context, getString(context, title), getString(context, content),
                getString(context, positive), getString(context, negative), listener);
    }

    public static void showDialog(Context context, String title, String content,
                                  String positive, String negative, final Listener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null)
            builder.setTitle(title);
        if (content != null)
            builder.setMessage(content);

        if (positive != null) {
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onDialogClosed(POSITIVE_BUTTON);
                    }
                }
            });
        }
        if (negative != null) {
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null)
                        listener.onDialogClosed(NEGATIVE_BUTTON);
                }
            });
        }
        try {
            AlertDialog dialog = builder.show();
            DownloaderDialog.setDialogSize(context, dialog);
        }catch (Exception e){
            Log.e("DialogHelp","bad token");
        }
    }
//
//    public static void showSingleChoiceDialog(Context context, int title, int content, int[] choice,
//                                              int defaultChoice, int positive, int negative,
//                                              Listener listener) {
//        showSingleChoiceDialog(context, getString(context, title), getString(context, content),
//                getString(context, choice), defaultChoice, getString(context, positive),
//                getString(context, negative), listener);
//    }
//
//    public static void showSingleChoiceDialog(Context context, String title, String content, String[] choice,
//                                              int defaultChoice, String positive, String negative,
//                                              final Listener listener) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AppCompatDialogStyle);
//        if (title != null)
//            builder.setTitle(title);
//        if (content != null)
//            builder.setMessage(content);
//
//        LayoutInflater dialogInflater = LayoutInflater.from(builder.getContext());
//        View dialogView = dialogInflater.inflate(R.layout.dialog_list, null);
//
//        ArrayAdapter<String> choiceAdapter =
//                new ArrayAdapter<>(
//                        builder.getContext(), android.R.layout.simple_list_item_single_choice);
//        choiceAdapter.addAll(choice);
//
//        final ListView listView = dialogView.findViewById(R.id.list_view);
//        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listView.setAdapter(choiceAdapter);
//        listView.setItemChecked(defaultChoice, true);
//        choiceAdapter.notifyDataSetChanged();
//        builder.setView(listView);
//
//        if (positive != null) {
//            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (listener != null) {
//                        listener.onDialogClosed(POSITIVE_BUTTON, listView.getCheckedItemPosition());
//                    }
//                }
//            });
//        }
//        if (negative != null) {
//            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (listener != null)
//                        listener.onDialogClosed(NEGATIVE_BUTTON, -1);
//                }
//            });
//        }
//
//        try {
//            AlertDialog dialog = builder.show();
//            DownloaderDialog.setDialogSize(context, dialog);
//        }catch (Exception e){
//            Log.e("DialogHelp","bad token");
//        }
//    }

    public static void showMultipleChoiceDialog(Context context, int title, int content, int[] choice,
                                                boolean[] checkedState, int positive, int negative,
                                                Listener listener) {
        showMultipleChoiceDialog(context, getString(context, title), getString(context, content),
                getString(context, choice), checkedState, getString(context, positive),
                getString(context, negative), listener);
    }

    public static void showMultipleChoiceDialog(Context context, String title, String content, final String[] choice,
                                                boolean[] checkedState, String positive, String negative,
                                                final Listener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null)
            builder.setTitle(title);
        if (content != null)
            builder.setMessage(content);

//        LayoutInflater dialogInflater = LayoutInflater.from(builder.getContext());
//        View dialogView = dialogInflater.inflate(R.layout.dialog_list, null);
//
//        ArrayAdapter<String> choiceAdapter =
//                new ArrayAdapter<>(
//                        builder.getContext(), android.R.layout.simple_list_item_multiple_choice);
//        choiceAdapter.addAll(choice);
//
//        final ListView listView = dialogView.findViewById(R.id.list_view);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        listView.setAdapter(choiceAdapter);
//        choiceAdapter.notifyDataSetChanged();
//        if (checkedState != null) {
//            for (int i = 0; i < checkedState.length; i++) {
//                listView.setItemChecked(i, checkedState[i]);
//            }
//        }
//        builder.setView(dialogView);
        if (positive != null) {
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean[] checked = new boolean[choice.length];
                    for (int i = 0; i < checked.length; i++)
                        if (listener != null) {
                            listener.onDialogClosed(POSITIVE_BUTTON, checked);
                        }
                }
            });
        }
        if (negative != null) {
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null)
                        listener.onDialogClosed(NEGATIVE_BUTTON, -1);
                }
            });
        }
        try {
            AlertDialog dialog = builder.show();
            DownloaderDialog.setDialogSize(context, dialog);
        }catch (Exception e){
            Log.e("DialogHelp","bad token");
        }
    }

//    public static void showInviteDialog(final Context context, final String from) {
//        FireBaseEventUtils.getInstance().report("invite_show_" + from);
//        showDialog(context, R.string.invite_friend,
//                R.string.share_friend_content,
//                R.string.share_now,
//                R.string.rate_later,
//                new DialogHelper.Listener() {
//                    @Override
//                    public void onDialogClosed(int which) {
//                        if (which == DialogHelper.POSITIVE_BUTTON) {
//                            ShareUtils.shareWithFriends(context);
//                            FireBaseEventUtils.getInstance().report("invite_go_" + from);
//                        }
//                    }
//                });
//    }

//    public static void showRenameDialog(Context context, int title, int content, int hint,
//                                        String currentText, String sizeText, int positive,
//                                        int negative, Listener listener, NameChecker checker) {
//        showRenameDialog(context, getString(context, title), getString(context, content),
//                getString(context, hint), currentText, sizeText, getString(context, positive),
//                getString(context, negative), listener, checker);
//    }

//    public static void showRenameDialog(Context context, String title, String content, String hint,
//                                        String currentText, String sizeText, String positive,
//                                        String negative,  final Listener listener, NameChecker checker) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AppCompatDialogStyle);
//        if (title != null)
//            builder.setTitle(title);
//        if (content != null)
//            builder.setMessage(content);
//
//
//        LayoutInflater dialogInflater = LayoutInflater.from(builder.getContext());
//        View dialogView = dialogInflater.inflate(R.layout.dialog_edit_file_name, null);
//
//        final EditText editText = dialogView.findViewById(R.id.dialog_edit_text);
//        TextView sizeTextView = dialogView.findViewById(R.id.size_text);
//
//        editText.setHint(hint);
//        if (currentText != null) {
//            editText.setText(currentText);
//        }
//        if (sizeText != null) {
//            sizeTextView.setText(sizeText);
//        } else {
//            sizeTextView.setVisibility(View.INVISIBLE);
//        }
//
//        builder.setView(dialogView);
//
//        if (positive != null) {
//            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (listener != null) {
//                        listener.onDialogClosed(POSITIVE_BUTTON, editText.getText().toString());
//                    }
//                }
//            });
//        }
//        if (negative != null) {
//            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (listener != null)
//                        listener.onDialogClosed(NEGATIVE_BUTTON, -1);
//                }
//            });
//        }
//        try {
//            AlertDialog dialog = builder.show();
//            DownloaderDialog.setDialogSize(context, dialog);
//        }catch (Exception e){
//            Log.e("DialogHelp","bad token");
//        }
//    }



    public static class Listener {
        public void onDialogClosed(int which) {
        }

        public void onDialogClosed(int which, String text) {
        }

        public void onDialogClosed(int which, int choice) {
        }

        public void onDialogClosed(int which, boolean[] checked) {
        }
    }

    public static interface NameChecker {
        Pair<Boolean, Integer> isValid(String name);
    }

//    public static void showBottomDialog(final Context context, View view) {
//        try {
//            final Dialog dialog = new Dialog(context, R.style.DialogTheme);
//            dialog.setContentView(view);
//            Window window = dialog.getWindow();
//            window.setGravity(Gravity.BOTTOM);
//            window.setWindowAnimations(R.style.main_menu_animStyle);
//            //设置对话框大小
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.show();
//        }catch (Exception e){}
//    }
}
