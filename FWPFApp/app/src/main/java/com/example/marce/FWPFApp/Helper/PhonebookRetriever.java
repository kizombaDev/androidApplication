package com.example.marce.FWPFApp.Helper;


import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/*
*
* this class reads the local phonebook
*
* Datei: PhonebookRetriever  Autor: Patrick
* Datum: 18.12.2015
* Historie:
* 18.12.15: class was created and implemented
*/
public class PhonebookRetriever {
    private Context context;

    public PhonebookRetriever(Context context) {
        this.context = context;
    }

    public String[] getAllPhonebookNumbers() {
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String[] numbers = new String[phones.getCount()];
        for (int i = 0; i < phones.getCount(); i++) {
            phones.moveToNext();
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            numbers[i] = phoneNumber;
        }
        phones.close();
        return numbers;
    }
}
