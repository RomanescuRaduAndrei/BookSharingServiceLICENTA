package com.example.booksharingservicelicenta;


import android.os.Parcel;
import android.os.Parcelable;

public class BookDataClass implements Parcelable {
    private String imageURL, title,author, itemId , posterUserId, posterUsername, requesterId,requesterUsername,state;
    public BookDataClass(){
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }

    public String getItemId() {
        return itemId;
    }
    public String getPosterUserId(){return posterUserId;}

    public String getPosterUsername(){return posterUsername;}
    public String getRequesterId(){return requesterId;}
    public String getRequesterUsername(){return requesterUsername;}
    public String getState(){return state;}


    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public BookDataClass(String imageURL, String title , String author , String posterUserId, String posterUsername,String requesterId,String requesterUsername,String state) {
        this.imageURL = imageURL;
        this.title = title;
        this.author=author;
        this.posterUserId = posterUserId;
        this.posterUsername = posterUsername;
        this.requesterId = requesterId;
        this.requesterUsername= requesterUsername;
        this.state = state;



    }


    protected BookDataClass(Parcel in) {
        imageURL = in.readString();
        title = in.readString();
        author = in.readString();
        itemId =in.readString();
        posterUserId =in.readString();
        posterUsername =in.readString();
        state=in.readString();
    }

    public static final Parcelable.Creator<BookDataClass> CREATOR = new Parcelable.Creator<BookDataClass>() {
        @Override
        public BookDataClass createFromParcel(Parcel in) {
            return new BookDataClass(in);
        }

        @Override
        public BookDataClass[] newArray(int size) {
            return new BookDataClass[size];
        }
    };

    // Implement the describeContents() method
    @Override
    public int describeContents() {
        return 0;
    }

    // Implement the writeToParcel() method
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageURL);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(itemId);
        dest.writeString(posterUserId);
        dest.writeString(posterUsername);
        dest.writeString(state);
    }
}