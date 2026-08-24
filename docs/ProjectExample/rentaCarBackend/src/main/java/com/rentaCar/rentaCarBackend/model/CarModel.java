package com.rentaCar.rentaCarBackend.model;

import com.rentaCar.rentaCarBackend.model.enums.FuelType;
import com.rentaCar.rentaCarBackend.model.enums.TransmissionType;
import com.rentaCar.rentaCarBackend.model.enums.VehicleCategory;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class CarModel implements Serializable, DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String make; // нпр. "Skoda"

    @Column(nullable = false)
    private String name; // нпр. "Fabia"

    private String engineDescription; // нпр. "1.4 TDI"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleCategory category;

    @Column(nullable = false)
    private int seats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    private int doors;
    private int luggageCapacity;
    private boolean hasAc;

    @Column(length = 512)
    private String pictureUrl;

    @Column(length = 512)
    private String logoUrl;

    // --- Једноставан ценовник ---
    private double priceTier1to3;
    private double priceTier4to7;
    private double priceTier8to15;
    private double priceTier16to30;
    private double priceTier31to45;
    private double priceTier46plus;

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEngineDescription() { return engineDescription; }
    public void setEngineDescription(String engineDescription) { this.engineDescription = engineDescription; }
    public VehicleCategory getCategory() { return category; }
    public void setCategory(VehicleCategory category) { this.category = category; }
    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
    public TransmissionType getTransmission() { return transmission; }
    public void setTransmission(TransmissionType transmission) { this.transmission = transmission; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
    public int getDoors() { return doors; }
    public void setDoors(int doors) { this.doors = doors; }
    public int getLuggageCapacity() { return luggageCapacity; }
    public void setLuggageCapacity(int luggageCapacity) { this.luggageCapacity = luggageCapacity; }
    public boolean isHasAc() { return hasAc; }
    public void setHasAc(boolean hasAc) { this.hasAc = hasAc; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public double getPriceTier1to3() { return priceTier1to3; }
    public void setPriceTier1to3(double priceTier1to3) { this.priceTier1to3 = priceTier1to3; }
    public double getPriceTier4to7() { return priceTier4to7; }
    public void setPriceTier4to7(double priceTier4to7) { this.priceTier4to7 = priceTier4to7; }
    public double getPriceTier8to15() { return priceTier8to15; }
    public void setPriceTier8to15(double priceTier8to15) { this.priceTier8to15 = priceTier8to15; }
    public double getPriceTier16to30() { return priceTier16to30; }
    public void setPriceTier16to30(double priceTier16to30) { this.priceTier16to30 = priceTier16to30; }
    public double getPriceTier31to45() { return priceTier31to45; }
    public void setPriceTier31to45(double priceTier31to45) { this.priceTier31to45 = priceTier31to45; }
    public double getPriceTier46plus() { return priceTier46plus; }
    public void setPriceTier46plus(double priceTier46plus) { this.priceTier46plus = priceTier46plus; }
}