package com.example.balancemanager.utils;

import android.app.Activity;
import android.os.Environment;

import com.example.balancemanager.models.User;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileHandler {
    private static String filename = "User.dat";
    private static String filepath = "MyFileStorage";


    public static User readUser(Activity activity) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
           return null;
        }

        File myExternalFile = new File(activity.getExternalFilesDir(filepath), filename);
        try {
            FileInputStream fstream = new FileInputStream(myExternalFile);
            try {
                ObjectInputStream ostream = new ObjectInputStream(fstream);
                while (true) {
                    User user;
                    try {
                        user = (User) ostream.readObject();

                        return user;
                    } catch (EOFException e) {
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException ignored) {
                System.out.println(ignored);
            } finally {
                fstream.close();
            }
        } catch (IOException ignored) {
            System.out.println(ignored);
        }

        return new User();
    }

    public static void writeUser(Activity activity, User user) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            return;
        }
        try {
            File myExternalFile = new File(activity.getExternalFilesDir(filepath), filename);
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();
        } catch (IOException ignored) {
            System.out.println(ignored);
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
