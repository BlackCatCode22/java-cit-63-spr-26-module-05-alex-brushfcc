//Zoo project practice
//AB CIT-63
//3/8/26

//import the needed packages
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class ZooKeeperApp {

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

    //New method breaking the parsing method from the main increasing ability to torubleshoot
    public static Animals parseAndCreateAnimal(String line, ArrayList<String> heyenaNames, int hyenaCount, ArrayList<String> lionNames, int lionCount, ArrayList<String> tigerNames, int tigerCount, ArrayList<String> bearNames, int bearCount) {

        //split into smaller pieces
        String[] parts = line.split(", ");
        String[] ageGenderSpecies = parts[0].split(" ");

        int age = Integer.parseInt(ageGenderSpecies[0]);
        String gender = ageGenderSpecies[3];
        String species = ageGenderSpecies[4].toLowerCase();
        String season = parts[1].split(" ")[2];
        String color = parts[2];
        int weight = Integer.parseInt(parts[3].split(" ")[0]);
        String origin = parts[4] + "," + parts[5];

        Animals newAnimal = null;
        String assignedID = "";
        String assignedName = "";

        //species break down
        if (species.equals("hyena")) {
            assignedName = "Hy" + String.format("%02d", hyenaCount + 1);
            assignedName = heyenaNames.remove(0);
            newAnimal = new Hyena(age, color, gender, weight, origin);
        }else if (species.equals("lion")) {
            assignedID = "Li" + String.format("%02d", lionCount + 1);
            assignedName = lionNames.remove(0);
            newAnimal = new Lion(age, color, gender, weight, origin);
        }else if (species.equals("tiger")) {
            assignedID = "Ti" + String.format("%02d", tigerCount + 1);
            assignedName = tigerNames.remove(0);
            newAnimal = new Tiger(age, color, gender, weight, origin);
        }else if (species.equals("bear")) {
            assignedID = "Be" + String.format("%02d", bearCount + 1);
            assignedName = bearNames.remove(0);
            newAnimal = new Bear(age, color, gender, weight, origin);
        }

        // finalize properties
        if(newAnimal != null) {
            newAnimal.setId(assignedID);
            newAnimal.setName(assignedName);
            newAnimal.setBirthDate(calculateBirthDate(age, season));
            newAnimal.setArrivalDate("2024-04-07");
        }
        return newAnimal;
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


        // Process arriving animals
        try (BufferedReader reader = new BufferedReader(new FileReader("arrivingAnimals.txt"))) {
            String line;

            //Read until the file is empty
            while ((line = reader.readLine()) != null) {

               String species = line.split(",")[0].split(" ")[4].toLowerCase();

               //Removed parsing and if-else logig from main
                Animals newAnimal = parseAndCreateAnimal(line, hyenaNamesList, hyenaCount, lionNamesList, lionCount, tigerNamesList, tigerCount, bearNamesList, bearCount);



                if (newAnimal != null) {
                    zooAnimals.add(newAnimal);
                    //update counters in main
                    if(species.equals("hyena")) hyenaCount++;
                    else if(species.equals("lion")) lionCount++;
                    else if(species.equals("tiger")) tigerCount++;
                    else if(species.equals("bear")) bearCount++;

                }
                //verify everything was read and extreacted
                System.out.println("successfully parsed: " + species);
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        System.out.println("All animals successfully processed and added to the zoo!");

        //hashmap to count how many of each species
        HashMap<String, Integer> speciesCount = new HashMap<>();

        for (Animals animal : zooAnimals) {
            speciesCount.put(animal.getSpecies(), speciesCount.getOrDefault(animal.getSpecies(), 0) + 1);

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