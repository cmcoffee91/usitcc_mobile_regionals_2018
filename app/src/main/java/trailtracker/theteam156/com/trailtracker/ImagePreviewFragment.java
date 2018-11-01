package trailtracker.theteam156.com.trailtracker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImagePreviewFragment   extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image_preview, null);


        Bundle info = getArguments();
        String imageUrl = info.getString("imageUrl");
        //byte[] image = (byte[]) info.getSerializable("byteArray");


        ImageView previewImage = (ImageView) v.findViewById(R.id.previewImage);
        previewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        // Glide.with(getActivity()).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(previewImage);
        //Glide.with(getActivity()).load(imageUrl).into(previewImage);
        Glide.with(this)
                .load( new File( imageUrl ) ) // Uri of the picture
                .into(previewImage);





        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       /* builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
               dismiss();
            }
        });
        */
        builder.setView(v);


        Dialog dialog = builder.create();

        return dialog;
    }

    @Override
    public void onStart()
    {
        if (getDialog() == null)
        {
            return;
        }
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // getDialog().getWindow().setWindowAnimations(
        //       R.style.DialogAnimation);

        super.onStart();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}