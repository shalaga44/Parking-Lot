package parking

const val errorFlag = -1

class Spots {
    private var totalSpots: Int = errorFlag
    private var occupiedSpotsCounter = 0
    private var spotsList: MutableList<Spot> = mutableListOf()

    fun createParkingLotOfSize(size: Int) {
        occupiedSpotsCounter = 0
        spotsList = mutableListOf()
        totalSpots = size
        spotsList.add(Spot(0, Car(), false))

        (1..totalSpots).forEach {
            spotsList.add(createEmptySpot(it))
        }

    }

    fun hasFreeSpot(): Boolean {
        return totalSpots - occupiedSpotsCounter > 0
    }

    fun parkCar(car: Car): Spot {
        val id = getFreeSpotId()
        val newParkingSpot = Spot(id, car, false)
        spotsList[id] = newParkingSpot
        occupiedSpotsCounter++
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
        occupiedSpotsCounter--
        return spotID
    }

    private fun createEmptySpot(spotID: Int): Spot {
        return Spot(spotID, Car(), true)
    }

    fun isEmpty(): Boolean {
        return occupiedSpotsCounter == 0
    }

    fun getOccupiedSpots(): List<Spot> {
        return spotsList.drop(1).filter { !it.isFree }
    }

    fun isNotCreated(): Boolean {
        return totalSpots == errorFlag
    }


}

class Spot(val id: Int, val car: Car, val isFree: Boolean)

data class Car(val carNumber: String = "", val color: String = "")
sealed class Action
class ParkAction(val car: Car) : Action()
class LeaveAction(val spotID: Int) : Action()
object ExitAction : Action()
class WrongInputAction(val input: String) : Action()
class CreateSpotsAction(val spots: Int) : Action()
object StatusAction : Action()
class RegByColorAction(val color: String) : Action()
class SpotByColorAction(val color: String) : Action()
class SpotByRegAction(val carNumber: String) : Action()
class ParkingLot {
    var isOpen: Boolean = true
    private val parkFlag = "park"
    private val leaveFlag = "leave"
    private val exitFlag = "exit"
    private val createFlag = "create"
    private val statusFlag = "status"
    private val regByColorFlag = "reg_by_color"
    private val spotByColorFlag = "spot_by_color"
    private val spotByRegFlag = "spot_by_reg"
    private var mySports = Spots()

    fun readUserInput() {
        val line = readLine()
        if (line.isNullOrBlank())
            return
        else {
            val action = parseAction(line)
            doAction(action)
        }
    }

    private fun checkOccupiedSpotsThenLeave(spotID: Int) {
        if (isParkingCreated())
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
        if (isParkingCreated())
            if (mySports.hasFreeSpot())
                parkCar(car)
            else
                printParkError()

    }

    private fun isParkingCreated(): Boolean {
        if (mySports.isNotCreated()) {
            println("Sorry, a parking lot has not been created.")
            return false
        }
        return true
    }

    private fun printParkError() {
        println("Sorry, the parking lot is full.")
    }

    private fun parkCar(car: Car) {
        val spot = mySports.parkCar(car)
        printCarParked(spot)
    }

    private fun printCarParked(spot: Spot) {
        println("${spot.car.color} car parked in spot ${spot.id}.")
    }

    private fun parseAction(userInput: String): Action {
        val lineList = userInput.split(" ")

        return when (lineList.first()) {
            parkFlag -> ParkAction(Car(lineList[1], lineList[2]))
            leaveFlag -> LeaveAction(lineList[1].toInt())
            exitFlag -> ExitAction
            createFlag -> CreateSpotsAction(lineList[1].toInt())
            statusFlag -> StatusAction
            regByColorFlag -> RegByColorAction(lineList[1])
            spotByColorFlag -> SpotByColorAction(lineList[1])
            spotByRegFlag -> SpotByRegAction(lineList[1])
            else -> WrongInputAction(userInput)
        }
    }


    private fun doAction(action: Action) {
        return when (action) {
            is ParkAction -> checkFreeSpotsThenPark(action.car)
            is LeaveAction -> checkOccupiedSpotsThenLeave(action.spotID)
            is ExitAction -> isOpen = false
            is WrongInputAction -> println("Input Error: ${action.input}")
            is CreateSpotsAction -> createParkingLot(action.spots)
            is StatusAction -> getParkingLotStatusThenPrint()
            is RegByColorAction -> getRegNumberByColor(action.color)
            is SpotByColorAction -> getSpotByColor(action.color)
            is SpotByRegAction -> getSpotByReg(action.carNumber)
        }
    }

    private fun getSpotByReg(carNumber: String) {
        val occupiedSpots = mySports.getOccupiedSpots()
        if (isParkingCreated())
            if (occupiedSpots.isNotEmpty())
                printSpotByReg(occupiedSpots, carNumber)
            else
                printNoCarByRegFound(carNumber)
    }

    private fun printSpotByReg(occupiedSpots: List<Spot>, carNumber: String) {
        val filteredCars = occupiedSpots.filterByReg(carNumber)
        if (filteredCars.isNotEmpty())
            printCarsSpotInLine(filteredCars)
        else
            printNoCarByRegFound(carNumber)
    }

    private fun printNoCarByRegFound(carNumber: String) {
        println("No cars with registration number $carNumber were found.")
    }

    private fun getSpotByColor(color: String) {
        val occupiedSpots = mySports.getOccupiedSpots()
        if (isParkingCreated())
            if (occupiedSpots.isNotEmpty())
                printSpotByColor(occupiedSpots, color)
            else
                printNoCarByColorFound(color)
    }

    private fun printSpotByColor(occupiedSpots: List<Spot>, color: String) {
        val filteredCars = occupiedSpots.filterByColor(color)
        if (filteredCars.isNotEmpty())
            printCarsSpotInLine(filteredCars)
        else
            printNoCarByColorFound(color)
    }

    private fun printCarsSpotInLine(filteredCars: List<Spot>) {
        filteredCars.forEach {
            print(it.id)
            if (it != filteredCars.last())
                print(", ")
            else
                println()
        }
    }


    private fun getRegNumberByColor(color: String) {
        val occupiedSpots = mySports.getOccupiedSpots()
        if (isParkingCreated())
            if (occupiedSpots.isNotEmpty())
                printRegNumberByColor(occupiedSpots, color)
            else
                printNoCarByColorFound(color)


    }

    private fun printNoCarByColorFound(color: String) {
        println("No Cars with color $color were found.")
    }

    private fun printRegNumberByColor(occupiedSpots: List<Spot>, color: String) {
        val filteredCars = occupiedSpots.filterByColor(color)
        if (filteredCars.isNotEmpty())
            printCarsRegInLine(filteredCars)
        else
            printNoCarByColorFound(color)
    }

    private fun printCarsRegInLine(filteredCars: List<Spot>) {
        filteredCars.forEach {
            print(it.car.carNumber)
            if (it != filteredCars.last())
                print(", ")
            else
                println()

        }
    }

    private fun createParkingLot(spots: Int) {
        mySports.createParkingLotOfSize(spots)
        println("Created a parking lot with $spots spots.")

    }

    private fun getParkingLotStatusThenPrint() {
        if (isParkingCreated())
            if (mySports.isEmpty())
                println("Parking lot is empty.")
            else
                printParkingLotStatus()
    }

    private fun printParkingLotStatus() {
        mySports.getOccupiedSpots()
                .forEach { spot ->
                    println("${spot.id} ${spot.car.carNumber} ${spot.car.color}")
                }


    }
}

private fun List<Spot>.filterByColor(color: String): List<Spot> {
    return this.filter { it.car.color.toLowerCase() == color.toLowerCase() }

}private fun List<Spot>.filterByReg(carNumber: String): List<Spot> {
    return this.filter { it.car.carNumber.toLowerCase() == carNumber.toLowerCase() }
}

fun main() {
    val parkingLot = ParkingLot()
    while (parkingLot.isOpen)
        parkingLot.readUserInput()

}
