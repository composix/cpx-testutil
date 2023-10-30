package nl.composix.bookstore;

import nl.composix.core.Node;
import nl.composix.core.Tree;

public class Book {
    private final String title;
    private final Tree<Integer,String> author;
    private final int year;
    private final double price;

    public Book(String title, String[] author, int year, double price) {
        this.title = title;
        this.author = new Node<>(author);
        this.year = year;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public Tree<Integer,String> getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }
}
