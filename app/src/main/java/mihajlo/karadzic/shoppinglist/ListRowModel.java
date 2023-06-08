package mihajlo.karadzic.shoppinglist;

public class ListRowModel {
    String mTitle, mShared;

    public ListRowModel(String mTitle,String mShared){
        this.mShared=mShared;
        this.mTitle=mTitle;
    }

    public String getmTitle(){return mTitle;}
    public String getmShared(){return mShared;}

    public void setmTitle(String mTitle){
        this.mTitle=mTitle;
    }

    public void setmShared(String mShared){
        this.mShared=mShared;
    }
}
