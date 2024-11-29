package practice.ch1;

import java.util.Vector;

public class Customer {
    private String _name;
    private Vector _rental = new Vector();

    public Customer(String name) {
        this._name = name;
    }

    public void addRental(Rental rental) {
        _rental.add(rental);
    }

    public String getName() {
        return _name;
    }
}
