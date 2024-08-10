package org.example.caloriesburned;

public class CalorieActivity {
    private String name;
    private int caloriesPerHour;
    private int durationMinutes;
    private int totalCalories;

    public CalorieActivity(String name, int caloriesPerHour, int durationMinutes) {
        this.name = name;
        this.caloriesPerHour = caloriesPerHour;
        this.durationMinutes = durationMinutes;
        this.totalCalories = caloriesPerHour * durationMinutes / 60;
    }

    // Getters
    public String getName() { return name; }
    public int getCaloriesPerHour() { return caloriesPerHour; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getTotalCalories() { return totalCalories; }
}
