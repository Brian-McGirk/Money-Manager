package MoneyManager.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Entity
public class Income {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min=3, max=15, message = "Source must be between 3 and 15 characters")
    private String source;

    @NotNull
    @DecimalMin("0.00")
    private double weeklyAmount;

    @NotNull
    @DecimalMin("0.00")
    private double monthlyAmount;

    @ManyToMany(mappedBy = "incomes")
    private List<User> users;

    public Income() { }

    public int getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getWeeklyAmount() {
        return weeklyAmount;
    }

    public void setWeeklyAmount(double weeklyAmount) {
        this.weeklyAmount = weeklyAmount;
    }

    public double getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(double monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public void calcMonthlyAmount(double weeklyAmount){
        this.monthlyAmount = weeklyAmount * 4;
    }

    public double calcMonthlyTotal(Iterable<Income> incomes){
        double monthlyIncomeTotal = 0;

        for(Income income : incomes){
            monthlyIncomeTotal += income.getMonthlyAmount();
        }

        return monthlyIncomeTotal;
    }

}
