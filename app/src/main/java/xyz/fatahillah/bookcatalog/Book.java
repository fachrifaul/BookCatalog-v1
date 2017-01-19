package xyz.fatahillah.bookcatalog;

/**
 * Created by mac on 1/16/17.
 */

public class Book {
    private String mBookTitle;
    private String[] mBookAuthors;
    private String mBookDescription;
    private String mBookInfoLink;
    private String mBookCover;

    public Book(String bookTitle, String[] bookAuthors,
                String bookDescription, String bookInfoLink,
                String bookCover) {
        mBookTitle = bookTitle;
        mBookAuthors = bookAuthors;
        mBookDescription = bookDescription;
        mBookInfoLink = bookInfoLink;
        mBookCover = bookCover;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public String[] getAuthors() {
        return mBookAuthors;
    }

    public String generateStringOfAuthor() {
        String s = "";
        for(int i=0;i<mBookAuthors.length;i++) {
            if(i == mBookAuthors.length-1)
                s += mBookAuthors[i];
            else
                s += mBookAuthors[i] + ", ";
        }
        return s;
    }

    public String getBookDescription() {
        return mBookDescription;
    }

    public String getBookInfoLink() {
        return mBookInfoLink;
    }

    public String getBookCover() {
        return mBookCover;
    }
}
