package LLD.parkinglot;

import lombok.AllArgsConstructor;

public class ParkingLot2 {
}

class Floor2{
    int ROWS = 3;
    int COLUMNS = 10;
    int floorNumber;
    ParkingSpot2[][] parkingSpots;
    Floor2(int floorNumber){
        this.floorNumber = floorNumber;
        this.parkingSpots = new ParkingSpot2[ROWS][COLUMNS];
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                if (i%3 == 0)
                    this.parkingSpots[i][j] = new ParkingSpot2(i,j,true,VehicleType.CAR);
                else if (i%3 == 1)
                    this.parkingSpots[i][j] = new ParkingSpot2(i,j,true,VehicleType.BIKE);
                else
                    this.parkingSpots[i][j] = new ParkingSpot2(i,j,true,VehicleType.TRUCK);
            }
        }
    }
}

@AllArgsConstructor
class ParkingSpot2{
    int row;
    int column;
    boolean isAvailable;
    VehicleType vehicleType;
}




