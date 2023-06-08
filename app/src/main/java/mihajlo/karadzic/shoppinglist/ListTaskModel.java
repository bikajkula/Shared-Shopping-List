package mihajlo.karadzic.shoppinglist;

public class ListTaskModel {

    String mTitle;
    boolean mChecked;

    public ListTaskModel(String mTitle, boolean mChecked) {
        this.mTitle = mTitle;
        this.mChecked = mChecked;
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
}
