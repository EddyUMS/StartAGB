package com.startagb.startagb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapterUsers extends BaseAdapter {
    // public String domain = MyGlobals.getInstance().getDomain();
    public String http = MyGlobals.getInstance().getHttp();
    Context context;
    String[] Userid, FirstName, LastName, IsActive, DistrictID,  UserPic,  RoleID;



    LayoutInflater inflater;

    public GridAdapterUsers(Context context, String[] Userid, String[] FirstName, String[] LastName, String[] IsActive, String[] DistrictID, String[] UserPic, String[] RoleID) {
        this.context = context;
        this.Userid = Userid ;
        this.FirstName = FirstName;
        this.LastName =LastName;
        this.IsActive = IsActive ;
        this.DistrictID = DistrictID;
        this.UserPic = UserPic;
        this.RoleID = RoleID;

    }

    @Override
    public int getCount() {
        return Userid.length;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.user_item,null);
        }

        TextView UserName =  convertView.findViewById(R.id.UserNameFR);
        TextView Role =  convertView.findViewById(R.id.RoleFR);
        ImageView userIMG  = convertView.findViewById(R.id.userImgFR);


        if(!FirstName[position].equals("not-set")){
            UserName.setText(FirstName[position] + " " + LastName[position]);
        }
        else{
            UserName.setText("USER" + Userid[position]);
        }



        String role = "";
        if(RoleID[position].equals("1")){
            role = "Farmer";
        }
        else if(RoleID[position].equals("2")){
            role = "Agent";
        }
        else if(RoleID[position].equals("3")){
            role = "Admin";
        }
        Role.setText(role);


        if(UserPic[position]!=null){
            byte[] byteData = Base64.decode(UserPic[position], Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
            userIMG.setImageBitmap(bitmap);
        }








        return convertView;

    }

    private String getImg(String comID, String item) {
        String[] field = new String[2];
        field[0] = "ID";
        field[1] = "item";
        String[] data = new String[2];
        data[0] = comID;
        data[1] = item;
        InsertData insertData = new InsertData(http+"://"+domain+"/AgriPriceBuddy/getGeneralImg.php", "POST", field, data);
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



    public String domain = MyGlobals.getInstance().getDomain();


}
