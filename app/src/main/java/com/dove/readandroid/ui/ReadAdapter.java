package com.dove.readandroid.ui;

import android.util.SparseArray;

import com.dove.readandroid.ui.model.BookSectionContent;
import com.dove.readandroid.ui.model.BookSectionItem;
import com.zchu.reader.StringAdapter;

import java.util.List;

/**
 * Created by Chu on 2017/8/15.
 */

public class ReadAdapter extends StringAdapter {

    private SparseArray<BookSectionContent> bookArray;
    private List<BookSectionItem> mBookSectionItems;

    public ReadAdapter() {
        bookArray = new SparseArray<>();
    }

    public ReadAdapter(List<BookSectionItem> bookSectionItems) {
        bookArray = new SparseArray<>();
        mBookSectionItems = bookSectionItems;

    }


    @Override
    protected String getPageSource(int section) {
        BookSectionContent sectionContent = bookArray.get(section);
        return sectionContent != null ? bookArray.get(section).content : null;
    }

    @Override
    public int getSectionCount() {
        return bookArray.size();
    }

    @Override
    public String getSectionName(int section) {
        BookSectionContent sectionContent = bookArray.get(section);
        return sectionContent != null ? bookArray.get(section).sectionName : null;
    }

    @Override
    public boolean hasNextSection(int currentSection) {
        return bookArray.get(currentSection + 1) != null;
    }

    @Override
    public boolean hasPreviousSection(int currentSection) {
        return bookArray.get(currentSection - 1) != null;
    }

    public void addData(int section, BookSectionContent content) {
        bookArray.put(section, content);
    }

    public boolean hasSection(int section) {
        return bookArray.get(section) != null;
    }

}
