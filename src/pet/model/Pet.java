package pet.model;

import java.sql.Date;

/**
 * 宠物实体类
 */
public class Pet {
    private int id;
    private String name;
    private String species;
    private Date birthday;
    private String breed;
    private double weight;
    private String ownerName;
    private String ownerPhone;

    public Pet() {}

    public Pet(String name, String species, Date birthday, String breed,
               double weight, String ownerName, String ownerPhone) {
        this.name = name;
        this.species = species;
        this.birthday = birthday;
        this.breed = breed;
        this.weight = weight;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s)", id, name, species);
    }
}
