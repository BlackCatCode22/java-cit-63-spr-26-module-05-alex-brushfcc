//Zoo project practice
//AB CIT-63
//3/8/26

//import the needed packages

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class ZooKeeperApporiginal {

    //method for calculating birthdate
    public static String calculateBirthDate(int age, String birthSeason) {
        int currentYear = 2024;
        int birthYear = currentYear - age;
        String monthAndDay;

        switch (birthSeason.toLowerCase()) {
            case "spring":
                monthAndDay = "03-21";
                break;
            case "fall":
                monthAndDay = "09-21";
                break;
            case "winter":
                monthAndDay = "12-21";
                break;
            default:
                monthAndDay = "01-01";
                break;
        }

        return birthYear + "-" + monthAndDay;
    }

    public static void main(String[] args) {
        //This ArrayList is the secure holding area for your instantiated objects
        ArrayList<Animals> zooAnimals = new ArrayList<>();

        //Parsing the Animal names text document first
        //Setup the lists for names
        ArrayList<String> hyenaNamesList = new ArrayList<>();
        ArrayList<String> lionNamesList = new ArrayList<>();
        ArrayList<String> tigerNamesList = new ArrayList<>();
        ArrayList<String> bearNamesList = new ArrayList<>();

        //Read the Names File
        try (BufferedReader nameReader = new BufferedReader(new FileReader("animalNames.txt"))) {
            String nameLine;
            String currentSpeciesHeader = "";

            while ((nameLine = nameReader.readLine()) != null) {
                if (nameLine.trim().isEmpty()) continue;

                if (nameLine.contains("Hyena Names")) {
                    currentSpeciesHeader = "Hyena";
                } else if (nameLine.contains("Lion Names")) {
                    currentSpeciesHeader = "Lion";
                } else if (nameLine.contains("Bear Names")) {
                    currentSpeciesHeader = "Bear";
                } else if (nameLine.contains("Tiger Names")) {
                    currentSpeciesHeader = "Tiger";
                } else {
                    String[] namesArray = nameLine.split(", ");
                    for (String name : namesArray) {
                        if (currentSpeciesHeader.equals("Hyena")) hyenaNamesList.add(name);
                        else if (currentSpeciesHeader.equals("Lion")) lionNamesList.add(name);
                        else if (currentSpeciesHeader.equals("Bear")) bearNamesList.add(name);
                        else if (currentSpeciesHeader.equals("Tiger")) tigerNamesList.add(name);
                    }
                }
            }
            //verify that the names were read successfully
            System.out.println("Names Successflly loaded into lists!");
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        int hyenaCount = 0;
        int tigerCount = 0;
        int bearCount = 0;
        int lionCount = 0;


        // Try-with-resources automatically close the file when done
        try (BufferedReader reader = new BufferedReader(new FileReader("arrivingAnimals.txt"))) {
            String line;

            //Read until the file is empty
            while ((line = reader.readLine()) != null) {

                // Step 1: Split the line into chunks by comma
                String[] parts = line.split(", ");

                //Step 2. Extract Age, Gender, and Species for the first chunk (Index 0)
                String[] ageGenderSpecies = parts[0].split(" ");
                int age = Integer.parseInt(ageGenderSpecies[0]);
                String gender = ageGenderSpecies[3];
                String species = ageGenderSpecies[4];

                //Step 3: Extract Birth Season
                String season = parts[1].split(" ")[2];

                //integrate birthdate
                String calculatedBirthDate = calculateBirthDate(age, season);


                //Step 4. extract color
                String color = parts[2];

                //Step 5. Extract Weight
                String[] weightParts = parts[3].split(" ");
                int weight = Integer.parseInt(weightParts[0]);

                //Step 6. Extract origin
                String origin = parts[4] + "," + parts[5];

                //variables to hold assigned data
                String assignedID = "";
                String assignedName = "";
                Animals newAnimal = null;

                // Route the logic based on specific species
                if (species.equals("hyena")) {
                    hyenaCount++;
                    // String.format("%02d", number) ensures we get "01" instead of just "1"
                    assignedID = "Hy" + String.format("%02d", hyenaCount);
                    // .remove(0) takes the first name off the list and shifts the rest up
                    assignedName = hyenaNamesList.remove(0);
                    newAnimal = new Hyena(age, color, gender, weight, origin);

                } else if (species.equals("lion")) {
                    lionCount++;
                    assignedID = "Li" + String.format("%02d", lionCount);
                    assignedName = lionNamesList.remove(0);
                    newAnimal = new Lion(age, color, gender, weight, origin);

                } else if (species.equals("tiger")) {
                    tigerCount++;
                    assignedID = "Ti" + String.format("%02d", tigerCount);
                    assignedName = tigerNamesList.remove(0);
                    newAnimal = new Tiger(age, color, gender, weight, origin);

                } else if (species.equals("bear")) {
                    bearCount++;
                    assignedID = "Be" + String.format("%02d", bearCount);
                    assignedName = bearNamesList.remove(0);
                    newAnimal = new Bear(age, color, gender, weight, origin);
                }
                //Set the finalized properties and add to the master list
                if (newAnimal != null) {
                    newAnimal.setId(assignedID);
                    newAnimal.setName(assignedName);
                    newAnimal.setBirthDate(calculateBirthDate(age, season));
                    newAnimal.setArrivalDate("2024-04-07");
                    zooAnimals.add(newAnimal);
                }
                //verify everything was read and extreacted
                System.out.println("successfully parsed: " + species + " " + "from " + origin);
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        System.out.println("All animals successfully processed and added to the zoo!");

        //hashmap to count how many of each species
        HashMap<String, Integer> speciesCount = new HashMap<>();

        for (Animals animal : zooAnimals) {
            String species = animal.getSpecies();
            if (speciesCount.containsKey(species)) {
                int currentCount = speciesCount.get(species);
                speciesCount.put(species, currentCount + 1);
            } else {
                speciesCount.put(species, 1);
            }
        }
        System.out.println("\n**** Species Count *****");
        for (String key : speciesCount.keySet()) {
            System.out.println(key + ": " + speciesCount.get(key));
        }
        //write new report to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("newAnimals.txt"))) {
            //an array of the species to help organize
            String[] speciesList = {"hyena", "lion", "bear", "tiger"};

            for (String targetSpecies : speciesList) {
                String header = targetSpecies.substring(0, 1).toUpperCase() + targetSpecies.substring(1);

                //add habitat header
                writer.write(header + " Habitat\n\n");

                //Loop through all animals and only write the ones matching the target
                for (Animals animal : zooAnimals) {
                    if (animal.getSpecies().equalsIgnoreCase(targetSpecies)) {

                        // Construct the string for each animal
                        String line = String.format("%s; %s; %d years old; birth date %s; %s; %s;%d pounds; from %s; arrived %s\n",
                                animal.getId(),
                                animal.getName(),
                                animal.getAge(),
                                animal.getBirthDate(),
                                animal.getColor(),
                                animal.getGender(),
                                animal.getWeight(),
                                animal.getOrigin(),
                                animal.getArrivalDate()
                        );

                        //Write the compiled line to the text file
                        writer.write(line);
                    }
                }
                writer.write("\n");
            }

            //Write the final species count from the hashmap
            writer.write("**** Total Species Count****\n");
            for (String key : speciesCount.keySet()) {
                String capitalizedKey = key.substring(0, 1).toUpperCase() + key.substring(1);
                writer.write(capitalizedKey + ": " + speciesCount.get(key) + "\n");
            }
            //output verifying your report is ready or there was an error
            System.out.println("Report Successfully genertated! Check your project folder for 'newAnimals.txt'");
        }catch (IOException e) {
            System.out.println("Error writing the file: " + e.getMessage());

            }
        }
    }