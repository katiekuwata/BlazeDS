/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

package dev.echoservice;

import java.util.Date;
import java.util.Calendar;

public class Book
{
    public String title;
    public String titleCopy;
    public String author;
    public Date published;
    public short pages;
    private String ISBN;

    public Book()
    {
    }

    public Book echoBook(Book b)
    {
        return b;
    }

    public Book newBook()
    {
        Book b = new Book();
        b.title = "Collapse - How Societies Choose to Fail or Survive";
        b.titleCopy = b.title;
        b.author = "Jared Diamond";
        Calendar cal = Calendar.getInstance();
        cal.set(2004, Calendar.DECEMBER, 29);
        b.published = cal.getTime();
        b.pages = 592;
        b.ISBN = "0670033375";

        return b;
    }

    public Book[] newBooks()
    {
        Book b0 = new Book();
        b0.title = "The Third Chimpanzee - The Evolution and Future of the Human Animal";
        b0.titleCopy = b0.title;
        b0.author = "Jared Diamond";
        Calendar cal = Calendar.getInstance();
        cal.set(1992, Calendar.JANUARY, 1);
        b0.published = cal.getTime();
        b0.pages = 407;
        b0.ISBN = "0060183071";

        Book b1 = new Book();
        b1.title = "Guns, Germs and Steel - The Fates of Human Societies";
        b1.titleCopy = b1.title;
        b1.author = "Jared Diamond";
        cal = Calendar.getInstance();
        cal.set(1997, Calendar.MARCH, 1);
        b1.published = cal.getTime();
        b1.pages = 480;
        b1.ISBN = "0393038912";

        Book b2 = newBook();

        return new Book[] {b1, b2, b0};
    }

    public void setISBN(String isbn)
    {
        if (isbn != null)
        {
            ISBN = isbn;
        }
    }

    public String getISBN()
    {
        return ISBN;
    }
}
