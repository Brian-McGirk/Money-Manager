package MoneyManager.models;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Entity
public class Expense {

    @Id
    @GeneratedValue
    private int id;

    private String date;

    @NotNull
    @Size(min=3, max=15, message = "Expense must be between 3 and 15 characters")
    private String name;

    @NotNull
    @DecimalMin("0.00")
    private double cost;

    @ManyToOne
    private Category category;

    @ManyToMany(mappedBy = "expenses")
    private List<User> users;

    public Expense(){ addDate(); }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addDate(){

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);

        this.date = dateString;

    }

}
