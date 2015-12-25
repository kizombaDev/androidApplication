package com.example.marce.FWPFApp.Helper;


import android.content.Context;

/**
 * Created by Patrick on 18.12.2015.
 */
public class PhonebookRetriever {
    private Context context;

    public PhonebookRetriever(Context context) {
        this.context = context;
    }

    public String[] getAllPhonebookNumbers() {
//        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
//        String[] numbers = new String[phones.getCount()];
//        for(int i = 0; i < phones.getCount(); i++) {
//            phones.moveToNext();
//            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            numbers[i] = phoneNumber;
//        }
//        phones.close();
//        return  numbers;

        String[] numbers = new String[2];
        numbers[0] = "12345";
        numbers[1] = "23456";
        return numbers;
    }

//    public Contact[] getAllPhonebookContacts() {
//        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
//        Contact[] contacts = new Contact[phones.getCount()];
//        for(int i = 0; i < phones.getCount(); i++) {
//            phones.moveToNext();
//            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//            contacts[i] = new Contact(name, phoneNumber, null);
//        }
//        phones.close();
//        return  contacts;
//    }
}
