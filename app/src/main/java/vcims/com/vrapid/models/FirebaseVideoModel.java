package vcims.com.vrapid.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebaseVideoModel implements Serializable{

    //For image and video
    public String status;
    public String url;
    public String name;

    /*Type = 1 for Image
    Type = 2 for Video
    Type = 3 for Ppt*/
    public int type;

    //For PPT
    public ArrayList<PPTModel> ppt;
    public String key;
}
