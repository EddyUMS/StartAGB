package com.startagb.startagb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.startagb.startagb.databinding.ActivityAgentDashboardBinding;
import com.startagb.startagb.databinding.ActivityUserUpdateInfoBinding;

public class UserUpdateInfo extends AppCompatActivity {
    private ActivityUserUpdateInfoBinding binding;
    private boolean hasUserPic = true;
    public String domain = MyGlobals.getInstance().getDomain();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityUserUpdateInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.userPicED.setVisibility(View.GONE);
        binding.loadPicED.setVisibility(View.VISIBLE);
        SetupUserPic(binding.userPicED);
        binding.userPicED.setVisibility(View.VISIBLE);
        binding.loadPicED.setVisibility(View.GONE);








    }



    private void SetupUserPic(ImageView usPic){
        hasUserPic=true;
        //Check if have pic
        String userPicBlob = getImg(MyGlobals.getInstance().getCurrentUSID(), "user");
        if(!userPicBlob.equals("null img")){
            byte[] byteData = Base64.decode(userPicBlob, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
            usPic.setImageBitmap(bitmap);
        }
        else{
            hasUserPic=false;
        }

    }


    private String getImg(String ID, String item) {
        String[] field = new String[2];
        field[0] = "ID";
        field[1] = "item";
        String[] data = new String[2];
        data[0] = ID;
        data[1] = item;
        InsertData insertData = new InsertData("http://"+domain+"/AgriPriceBuddy/getGeneralImg.php", "POST", field, data);
        if (insertData.startPut()) {
            if (insertData.onComplete()) {
                String img = insertData.getResult();
                if(img==null){
                    return "null img";
                }
                else{
                    return img;
                }

            }
        }
        else{
            return "null img";
        }
        return "null img";
    }
}