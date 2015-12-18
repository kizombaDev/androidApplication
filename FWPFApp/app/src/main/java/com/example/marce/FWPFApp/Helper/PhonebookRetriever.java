package com.example.marce.FWPFApp.Helper;


import android.provider.ContactsContract;
import android.database.Cursor;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.example.marce.FWPFApp.DataObjects.Contact;

/**
 * Created by Patrick on 18.12.2015.
 */
public class PhonebookRetriever {
    private Context context;

    public PhonebookRetriever(Context context) {
        this.context = context;
    }

    public Contact[] getAllPhonebookContacts() {
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        Contact[] contacts = new Contact[phones.getCount()];
        for(int i = 0; i < phones.getCount(); i++) {
            phones.moveToNext();
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contacts[i] = new Contact(name, phoneNumber, null);
        }
        phones.close();
        return  contacts;
    }
}
