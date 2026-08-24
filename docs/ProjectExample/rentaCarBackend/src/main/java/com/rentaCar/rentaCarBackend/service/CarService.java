package com.rentaCar.rentaCarBackend.service;

import com.rentaCar.rentaCarBackend.dto.CarDTO;
import com.rentaCar.rentaCarBackend.mapper.CarMapper;
import com.rentaCar.rentaCarBackend.model.Car;
import com.rentaCar.rentaCarBackend.model.CarModel;
import com.rentaCar.rentaCarBackend.JPARepo.CarModelRepository;
import com.rentaCar.rentaCarBackend.JPARepo.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarModelRepository carModelRepository;

    public CarService(CarRepository carRepository, CarMapper carMapper, CarModelRepository carModelRepository) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.carModelRepository = carModelRepository;
    }

    public List<CarDTO> getAll() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(carMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    public List<CarDTO> getWithStatus(int status) {
        List<Car> cars = carRepository.findByStatus(status);
        return cars.stream()
                .map(carMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    public List<CarDTO> searchCars(String searchParam) {
        // NAPOMENA: Ako si preimenovao metodu u CarRepository, proveri da li je ovde ispravno.
        // Pretpostavka da je metoda i dalje findByLicensePlateStartsWith
        List<Car> cars = carRepository.findByLicensePlateStartsWith(searchParam);
        return cars.stream()
                .map(carMapper::toDomainDTO)
                .collect(Collectors.toList());
    }

    public String addCar(CarDTO carDTO) {
        try {
            // 1. Učitaj CarModel iz baze
            // Koristimo Optional.orElseThrow za jasnije rukovanje greškama
            CarModel carModel = carModelRepository.findById(carDTO.getCarModel().getId())
                    .orElseThrow(() -> new RuntimeException("CarModel not found with id: " + carDTO.getCarModel().getId()));

            // 2. Pozovi maper sa OBA ARGUMENTA
            Car car = carMapper.toDomainEntity(carDTO, carModel);

            // 3. Sačuvaj
            carRepository.save(car);
            return "Successfully created a car!";

        } catch (Exception ex) {
            ex.printStackTrace(); // Dobra praksa da vidiš grešku u konzoli
            return "Cannot create this car! Error: " + ex.getMessage(); // Vraćamo poruku greške za bolji debug
        }
    }

    public String deleteCar(int carId) {
        try {
            if (carRepository.existsById(carId)) { // Provera da li automobil postoji pre brisanja
                carRepository.deleteById(carId);
                return "Successfully deleted the car!";
            } else {
                return "Car with id " + carId + " not found.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Cannot delete this car! Error: " + ex.getMessage();
        }
    }

    public CarDTO getCar(int carId) {
        Car carEntity = carRepository.findById(carId).orElse(null);
        // Ako entitet nije pronađen, maper će vratiti null, što je ok ako to očekuješ.
        // Alternativno, možeš baciti izuzetak ovde ako je automobil obavezan.
        return carMapper.toDomainDTO(carEntity);
    }

    public String updateCar(CarDTO carDTO) {
        try {
            // 1. Pronađi postojeći automobil da bismo bili sigurni da radimo update
            // Takođe, ovo je neophodno za proveru ID-ja i ostalih polja
            carRepository.findById(carDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Car not found with id: " + carDTO.getId()));

            // 2. Učitaj CarModel iz baze
            CarModel carModel = carModelRepository.findById(carDTO.getCarModel().getId())
                    .orElseThrow(() -> new RuntimeException("CarModel not found with id: " + carDTO.getCarModel().getId()));

            // 3. Pozovi maper sa OBA ARGUMENTA
            // Maper će kreirati novi Car objekat sa ID-jem iz DTO-a i povezanim CarModel-om
            Car car = carMapper.toDomainEntity(carDTO, carModel);

            // 4. Sačuvaj (save će uraditi UPDATE jer 'car' objekat ima ID)
            carRepository.save(car);
            return "Successfully updated the car!";

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Cannot update this car! Error: " + ex.getMessage();
        }
    }
}