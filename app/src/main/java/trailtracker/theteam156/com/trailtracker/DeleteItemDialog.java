package trailtracker.theteam156.com.trailtracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DeleteItemDialog extends DialogFragment {


    public HandleDeleteDialog handlePicDialog;
    private Context mContext;


    public interface HandleDeleteDialog {
        void onClickIndex(boolean shouldDelete, int itemIndex);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Bundle args = getArguments();
        String title = args.getString("title");
        String message = args.getString("message");
        final int itemIndex = args.getInt("itemIndex");

        handlePicDialog = (HandleDeleteDialog) mContext;


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
                dismiss();
                handlePicDialog.onClickIndex(false, itemIndex);
                //sendResult(Activity.RESULT_OK, "camera");
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                handlePicDialog.onClickIndex(true, itemIndex);
                // sendResult(Activity.RESULT_OK, "gallery");
            }
        });


        Dialog dialog = builder.create();
        return dialog;
    }

    private void sendResult(int resultCode, String type) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent data = new Intent();
        data.putExtra("picType", type);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }



}
