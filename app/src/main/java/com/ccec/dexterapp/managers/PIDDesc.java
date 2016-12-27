package com.ccec.dexterapp.managers;

/**
 * Created by manish on 26/12/16.
 */

public class PIDDesc {
    public static String getPIDDesc(String code) {
        switch (code) {
            case "00":
                return "PIDs supported [01 - 20]";
            case "01":
                return "Monitor status since DTCs cleared. (Includes malfunction indicator lamp (MIL) status and number of DTCs.)";
            case "02":
                return "Freeze DTC";
            case "03":
                return "Fuel system status";
            case "04":
                return "Calculated engine load";
            case "05":
                return "Engine coolant temperature";
            case "06":
                return "Short term fuel trim—Bank 1";
            case "07":
                return "Long term fuel trim—Bank 1";
            case "08":
                return "Short term fuel trim—Bank 2";
            case "09":
                return "Long term fuel trim—Bank 2";
            case "0A":
                return "Fuel pressure (gauge pressure)";
            case "0B":
                return "Intake manifold absolute pressure";
            case "0C":
                return "Engine RPM";
            case "0D":
                return "Vehicle speed";
            case "0E":
                return "Timing advance";
            case "0F":
                return "Intake air temperature";
            case "10":
                return "MAF air flow rate";
            case "11":
                return "Throttle position";
            case "12":
                return "Commanded secondary air status";
            case "13":
                return "Oxygen sensors present (in 2 banks)";
            case "14":
                return "Oxygen Sensor 1 A: Voltage B: Short texrm fuel trim";
            case "15":
                return "Oxygen Sensor 2 A: Voltage B: Short term fuel trim";
            case "16":
                return "Oxygen Sensor 3 A: Voltage B: Short term fuel trim";
            case "17":
                return "Oxygen Sensor 4 A: Voltage B: Short term fuel trim";
            case "18":
                return "Oxygen Sensor 5 A: Voltage B: Short term fuel trim";
            case "19":
                return "Oxygen Sensor 6 A: Voltage B: Short term fuel trim";
            case "1A":
                return "Oxygen Sensor 7 A: Voltage B: Short term fuel trim";
            case "1B":
                return "Oxygen Sensor 8 A: Voltage B: Short term fuel trim";
            case "1C":
                return "OBD standards this vehicle conforms to";
            case "1D":
                return "Oxygen sensors present (in 4 banks)";
            case "1E":
                return "Auxiliary input status";
            case "1F":
                return "Run time since engine start";
            case "20":
                return "PIDs supported [21 - 40]";
            case "21":
                return "Distance traveled with malfunction indicator lamp (MIL) on";
            case "22":
                return "Fuel Rail Pressure (relative to manifold vacuum)";
            case "23":
                return "Fuel Rail Gauge Pressure (diesel, or gasoline direct injection)";
            case "24":
                return "Oxygen Sensor 1 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "25":
                return "Oxygen Sensor 2 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "26":
                return "Oxygen Sensor 3 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "27":
                return "Oxygen Sensor 4 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "28":
                return "Oxygen Sensor 5 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "29":
                return "Oxygen Sensor 6 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "2A":
                return "Oxygen Sensor 7 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "2B":
                return "Oxygen Sensor 8 AB: Fuel–Air Equivalence Ratio CD: Voltage";
            case "2C":
                return "Commanded EGR";
            case "2D":
                return "EGR Error";
            case "2E":
                return "Commanded evaporative purge";
            case "2F":
                return "Fuel Tank Level Input";
            case "30":
                return "Warm-ups since codes cleared";
            case "31":
                return "Distance traveled since codes cleared";
            case "32":
                return "Evap. System Vapor Pressure";
            case "33":
                return "Absolute Barometric Pressure";
            case "34":
                return "Oxygen Sensor 1 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "35":
                return "Oxygen Sensor 2 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "36":
                return "Oxygen Sensor 3 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "37":
                return "Oxygen Sensor 4 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "38":
                return "Oxygen Sensor 5 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "39":
                return "Oxygen Sensor 6 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "3A":
                return "Oxygen Sensor 7 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "3B":
                return "Oxygen Sensor 8 AB: Fuel–Air Equivalence Ratio CD: Current";
            case "3C":
                return "Catalyst Temperature: Bank 1, Sensor 1";
            case "3D":
                return "Catalyst Temperature: Bank 2, Sensor 1";
            case "3E":
                return "Catalyst Temperature: Bank 1, Sensor 2";
            case "3F":
                return "Catalyst Temperature: Bank , Sensor 2";
            case "40":
                return "PIDs supported [41 - 60]";
            case "41":
                return "Monitor status this drive cycle";
            case "42":
                return "Control module voltage";
            case "43":
                return "Absolute load value";
            case "44":
                return "Fuel–Air commanded equivalence ratio";
            case "45":
                return "Relative throttle position";
            case "46":
                return "Ambient air temperature";
            case "47":
                return "Absolute throttle position B";
            case "48":
                return "Absolute throttle position C";
            case "49":
                return "Accelerator pedal position D";
            case "4A":
                return "Accelerator pedal position E";
            case "4B":
                return "Accelerator pedal position F";
            case "4C":
                return "Commanded throttle actuator";
            case "4D":
                return "Time run with MIL on";
            case "4E":
                return "Time since trouble codes cleared";
            case "4F":
                return "Maximum value for Fuel–Air equivalence ratio, oxygen sensor voltage, oxygen sensor current, and intake manifold absolute pressure";
            case "50":
                return "Maximum value for air flow rate from mass air flow sensor";
            case "51":
                return "Fuel Type";
            case "52":
                return "Ethanol fuel %";
            case "53":
                return "Absolute Evap system Vapor Pressure";
            case "54":
                return "Evap system vapor pressure";
            case "55":
                return "Short term secondary oxygen sensor trim, A: bank 1, B: bank 3";
            case "56":
                return "Long term secondary oxygen sensor trim, A: bank 1, B: bank 3";
            case "57":
                return "Short term secondary oxygen sensor trim, A: bank 2, B: bank 4";
            case "58":
                return "Long term secondary oxygen sensor trim, A: bank 2, B: bank 4";
            case "59":
                return "Fuel rail absolute pressure";
            case "5A":
                return "Relative accelerator pedal position";
            case "5B":
                return "Hybrid battery pack remaining life";
            case "5C":
                return "Engine oil temperature";
            case "5D":
                return "Fuel injection timing";
            case "5E":
                return "Engine fuel rate";
            case "5F":
                return "Emission requirements to which vehicle is designed";
            case "60":
                return "PIDs supported [61 - 80]";
            case "61":
                return "Driver's demand engine - percent torque";
            case "62":
                return "Actual engine - percent torque";
            case "63":
                return "Engine reference torque";
            case "64":
                return "Engine percent torque data";
            case "65":
                return "Auxiliary input / output supported";
            case "66":
                return "Mass air flow sensor";
            case "67":
                return "Engine coolant temperature";
            case "68":
                return "Intake air temperature sensor";
            case "69":
                return "Commanded EGR and EGR Error";
            case "6A":
                return "Commanded Diesel intake air flow control and relative intake air flow position";
            case "6B":
                return "Exhaust gas recirculation temperature";
            case "6C":
                return "Commanded throttle actuator control and relative throttle position";
            case "6D":
                return "Fuel pressure control system";
            case "6E":
                return "Injection pressure control system";
            case "6F":
                return "Turbocharger compressor inlet pressure";
            case "70":
                return "Boost pressure control";
            case "71":
                return "Variable Geometry turbo (VGT) control";
            case "72":
                return "Wastegate control";
            case "73":
                return "Exhaust pressure";
            case "74":
                return "Turbocharger RPM";
            case "75":
                return "Turbocharger temperature";
            case "76":
                return "Turbocharger temperature";
            case "77":
                return "Charge air cooler temperature (CACT)";
            case "78":
                return "Exhaust Gas temperature (EGT) Bank 1";
            case "79":
                return "Exhaust Gas temperature (EGT) Bank 2";
            case "7A":
                return "Diesel particulate filter (DPF)";
            case "7B":
                return "Diesel particulate filter (DPF)";
            case "7C":
                return "Diesel Particulate filter (DPF) temperature";
            case "7D":
                return "NOx NTE control area status";
            case "7E":
                return "PM NTE control area status";
            case "7F":
                return "Engine run time";
            case "80":
                return "PIDs supported [81 - A0]";
            case "81":
                return "Engine run time for Auxiliary Emissions Control Device(AECD)";
            case "82":
                return "Engine run time for Auxiliary Emissions Control Device(AECD)";
            case "83":
                return "NOx sensor";
            case "84":
                return "Manifold surface temperature";
            case "85":
                return "NOx reagent system";
            case "86":
                return "Particulate matter (PM) sensor";
            case "87":
                return "Intake manifold absolute pressure";

            default:
                return "got it";


        }
    }
}
