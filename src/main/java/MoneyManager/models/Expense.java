package MoneyManager.models;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;


@Entity
public class Expense {

    @Id
    @GeneratedValue
    private int id;

//    private Date date;

    @NotNull
    @Size(min=3, max=15)
    private String name;

    @NotNull
    @DecimalMin("0.00")
    private double cost;

    @ManyToOne
    private Category category;

    @ManyToMany(mappedBy = "expenses")
    private List<User> users;

    public Expense(){}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
}
