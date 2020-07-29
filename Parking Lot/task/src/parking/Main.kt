package parking

val errorFlag = -1

class Spots(val totalSpots: Int) {
    var occupiedSpotsCounter = 0
    var spotsList: MutableList<Spot> = mutableListOf()

    init {
        spotsList.add(Spot(0, Car(), false))

        (1..totalSpots).forEach {

            spotsList.add(createEmptySpot(it))
        }
        spotsList[1] = Spot(1, Car(), false)

    }

    fun hasFreeSpot(): Boolean {
        return occupiedSpotsCounter < totalSpots
    }

    fun parkCar(car: Car): Spot {
        val id = getFreeSpotId()
        val newParkingSpot = Spot(id, car, false)
        spotsList[id] = newParkingSpot
        occupiedSpotsCounter--
        return newParkingSpot
    }

    private fun getFreeSpotId(): Int {
        spotsList.forEach {
            if (it.isFree)
                return it.id
        }
        return errorFlag
    }

    fun isOccupiedSpot(spotID: Int): Boolean {
        return !spotsList[spotID].isFree
    }

    fun freeSpot(spotID: Int): Int {
        spotsList[spotID] = createEmptySpot(spotID)
        occupiedSpotsCounter++
        return spotID
    }

    private fun createEmptySpot(spotID: Int): Spot {
        return Spot(spotID, Car(), true)
    }


}

class Spot(val id: Int, val car: Car, val isFree: Boolean)

data class Car(val carNumber: String = "", val color: String = "")
sealed class Action
class ParkAction(val car: Car) : Action()
class LeaveAction(val spotID: Int) : Action()
class ParkingLot {
    private val parkFlag = "park"
    private val leaveFlag = "leave"
    private var mySports = Spots(2)

    fun readUserInput() {
        print("> ")
        val line = readLine()!!.trim()
        if (line.isBlank())
            return
        val action = parseAction(line)
        doAction(action)
    }

    private fun doAction(action: Action) {
        when (action) {
            is ParkAction -> checkFreeSpotsThenPark(action.car)
            is LeaveAction -> checkOccupiedSpotsThenLeave(action.spotID)
        }
    }

    private fun checkOccupiedSpotsThenLeave(spotID: Int) {
        if (mySports.isOccupiedSpot(spotID))
            leaveCar(spotID)
        else
            printLeaveError(spotID)
    }

    private fun printLeaveError(spotID: Int) {
        println("There is no car in spot $spotID.")
    }

    private fun leaveCar(spotID: Int) {
        val freedSpot: Int = mySports.freeSpot(spotID)
        printSpotFreed(freedSpot)
    }

    private fun printSpotFreed(freedSpot: Int) {
        println("Spot $freedSpot is free.")
    }

    private fun checkFreeSpotsThenPark(car: Car) {
        if (mySports.hasFreeSpot())
            parkCar(car)
        else
            printParkError(car)

    }

    private fun printParkError(car: Car) {
        println("Can not park $car")
    }

    private fun parkCar(car: Car) {
        val spot = mySports.parkCar(car)
        printCarParked(spot)
    }

    private fun printCarParked(spot: Spot) {
        println("${spot.car.color} car parked in spot ${spot.id}.")
    }


    private fun parseAction(line: String): Action {
        val lineList = line.split(" ")

        return when (lineList.first()) {
            parkFlag -> ParkAction(Car(lineList[1], lineList[2]))
            leaveFlag -> LeaveAction(lineList[1].toInt())
            else -> ParkAction(Car("", ""))
        }
    }
}

fun main() {
    val parkingLot = ParkingLot()
    while (true) {
        parkingLot.readUserInput()
    }
}
