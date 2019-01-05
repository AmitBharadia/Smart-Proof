package com.kit.verifier.zeroknowledge.util;

import com.kit.verifier.zeroknowledge.dto.ClosedRange;

import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class InputUtils {

    public static ClosedRange readRange(Scanner s) {

        BigInteger lo, hi;

        lo = readBigInteger(s, "Enter lower bound");
        do {
            hi = readBigInteger(s, "Enter upper bound (must be >= lower bound)");
        } while (hi.compareTo(lo) < 0);

        return ClosedRange.of(lo, hi);
    }

    public static String readString(Scanner s, String prompt) {
        System.out.println(prompt + ": ");
        return s.nextLine();
    }

    public static BigInteger readBigInteger(Scanner s, String prompt) {
        System.out.println(prompt + ": ");
        int n = s.nextInt();
        return new BigInteger("" + n);
    }

    /**
     * Read a serialized object from a file, de-serializes it and returns it.
     * @param fileName, the file to read.
     * @return object. the deserialized object.
     */
    public static Object readObject(final String fileName) {

        Object object = new Object();
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
        } catch (IOException ioe) {
            ioe.printStackTrace();;
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } finally {

            try {
                ois.close();
                fis.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return object;
    }

    /**
     * Serializes an object and saves it to a file.
     * @param fileName the name of the file to save the object.
     * @param object the object to save.
     */
    public static void saveObject(final String fileName, final Object object) {

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
}
