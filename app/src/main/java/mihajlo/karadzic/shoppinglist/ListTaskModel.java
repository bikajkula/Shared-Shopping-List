package mihajlo.karadzic.shoppinglist;

public class ListTaskModel {

    String mTitle;
    boolean mChecked;
    String uID;

    public ListTaskModel(String mTitle, boolean mChecked, String uID) {
        this.mTitle = mTitle;
        this.mChecked = mChecked;
        this.uID = uID;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public boolean ismChecked() {
        return mChecked;
    }

    public void setmChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }

    public String getuID(){ return uID;}

    public void setuID(String uID) {
        this.uID = uID;
    }
}
