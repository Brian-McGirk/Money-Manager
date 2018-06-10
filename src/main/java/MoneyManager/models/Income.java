package MoneyManager.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Entity
public class Income {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private String date;

    @NotNull
    @Size(min=3, max=15, message = "Source must be between 3 and 15 characters")
    private String source;

    @NotNull
    @DecimalMin("0.00")
    private double dailyAmount;

    @NotNull
    @DecimalMin("0.00")
    private double weeklyAmount;

    @NotNull
    @DecimalMin("0.00")
    private double monthlyAmount;

    @ManyToMany(mappedBy = "incomes")
    private List<User> users;

    public Income() { addDate(); }

    public int getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getDailyAmount() {
        return dailyAmount;
    }

    public void setDailyAmount(double dailyAmount) {
        this.dailyAmount = dailyAmount;
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

    public int getNumberOfDailyIncome(Iterable<Income> incomes){

        int numberOfDailyIncomes = 0;

        for(Income income : incomes){
            if(income.getDailyAmount() != 0){
                numberOfDailyIncomes++;
            }
        }

        return numberOfDailyIncomes;
    }

    public double calcDailyAmount(Iterable<Income> incomes){
        double dailyExpenseTotal = 0;

        for(Income income : incomes){
            dailyExpenseTotal += income.getDailyAmount();
        }

        return dailyExpenseTotal;
    }

}
