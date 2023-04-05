package edu.ucalgary.oop;

import java.io.*;
import java.util.*;

public class Schedule {
    private HashMap<Integer, ToDo> schedule = new HashMap<Integer, ToDo>();
    private ArrayList<FeedingSchedule> feedings = new ArrayList<FeedingSchedule>();
    private ArrayList<CleaningCage> cageCleanings = new ArrayList<CleaningCage>();
    private ArrayList<Treatment> treatments = new ArrayList<Treatment>();;
    private HashMap<String, Integer> animalCount = new HashMap<>();

    public Schedule() {
        animalCount.put("coyote", 0);
        animalCount.put("porcupine", 0);
        animalCount.put("fox", 0);
        animalCount.put("raccoon", 0);
        animalCount.put("beaver", 0);

        for (int i = 0; i < 24; i++) {
            schedule.put(i, new ToDo());
        }
    }

    public void addTasksToSchedule() {
        Collections.sort(treatments, Comparator.comparing(Treatment -> Treatment.getTASK().getMAXWINDOW()));

        for (Treatment treatment : treatments) {
            int treatmentStartHour = treatment.getSTART_HOUR();
            int treatmentMaxWindow = treatment.getTASK().getMAXWINDOW();

            int currentWindow = 0;
            while (currentWindow < treatmentMaxWindow) {
                int currentHour = treatmentStartHour + currentWindow;
                if (schedule.get(currentHour).getTimeRemaining() >= treatment.getTASK().getDURATION()) {
                    schedule.get(currentHour).addTask(treatment.getTASK().getDESCRIPTION() + " (" + treatment.getANIMAL().getNAME() + ")");
                    int newTimeRemaining = schedule.get(currentHour).getTimeRemaining()
                            - treatment.getTASK().getDURATION();
                    schedule.get(currentHour).updateTimeRemaining(newTimeRemaining);
                    break;
                } else {
                    currentWindow++;
                }
            }
        }
    }

    public void addFeedingToSchedule() {
        for (FeedingSchedule feeding : feedings) {
            int feedingStartHour = feeding.getStartHour();
            int feedingMaxWindow = feeding.getTimeWindow();
            int currentWindow = 0;
            boolean taskAssigned = false;
            while (currentWindow < feedingMaxWindow) {
                int currentHour = feedingStartHour + currentWindow;
                if (schedule.get(currentHour).getTimeRemaining() >= feeding.getDuration()) {
                    schedule.get(currentHour).addTask(feeding.getDescription() + " " + feeding.getName());
                    int newTimeRemaining = schedule.get(currentHour).getTimeRemaining()
                            - feeding.getDuration();
                    schedule.get(currentHour).updateTimeRemaining(newTimeRemaining);
                    taskAssigned = true;
                    break;
                } else {
                    currentWindow++;
                }
            }
            if (!taskAssigned) {
                callBackupVolunteer(feeding);
            }
        }
    }

    public String getAllNames() {
        StringBuilder sb = new StringBuilder();        
        for (Treatment treatment : treatments) {
            if (!sb.toString().contains(treatment.getANIMAL().getNAME()))
                sb.append(treatment.getANIMAL().getNAME()).append("\n");
        }
        return sb.toString();
    }

    public void callBackupVolunteer(FeedingSchedule feeding) {
        System.out.println("Not enough time for feeding: " + feeding.getName() + ". Calling backup volunteer.");
    }

    public void addCageCleaningToSchedule() {
        for (CleaningCage cageCleaning : cageCleanings) {
            int cageCleaningDuration = cageCleaning.getDuration();
            String cageCleaningDescription = cageCleaning.getDescription();

            for (int i = 0; i < 24; i++) {
                if (schedule.get(i).getTimeRemaining() >= cageCleaningDuration) {
                    schedule.get(i).addTask(cageCleaningDescription + " " + cageCleaning.getName());
                    int newTimeRemaining = schedule.get(i).getTimeRemaining() - cageCleaningDuration;
                    schedule.get(i).updateTimeRemaining(newTimeRemaining);
                    break;
                }
            }
        }
    }

    public void addAnimals(ArrayList<Animal> animals) {
        for (Animal animal : animals) {
            if (animal.getSPECIES().equals("porcupine")) {
                this.animalCount.put("porcupine", animalCount.get("porcupine") + 1);
            } else if (animal.getSPECIES().equals("coyote")) {
                this.animalCount.put("coyote", animalCount.get("coyote") + 1);
            } else if (animal.getSPECIES().equals("fox")) {
                this.animalCount.put("fox", animalCount.get("fox") + 1);
            } else if (animal.getSPECIES().equals("raccoon")) {
                this.animalCount.put("raccoon", animalCount.get("raccoon") + 1);
            } else if (animal.getSPECIES().equals("beaver")) {
                this.animalCount.put("beaver", animalCount.get("beaver") + 1);
            }
        }
    }

    public void addFeeding(FeedingSchedule feeding) {
        feedings.add(feeding);
    }

    public void addCageCleaning(CleaningCage cageCleaning) {
        cageCleanings.add(cageCleaning);
    }

    public void addTreatment(Treatment treatment) {
        treatments.add(treatment);
    }

    public ArrayList<Treatment> getTreatment() {
        return treatments;
    }

    public ArrayList<FeedingSchedule> getFeeding() {
        return feedings;
    }

    public ArrayList<CleaningCage> getCageCleaning() {
        return cageCleanings;
    }

    public HashMap<String, Integer> getAnimalCount() {
        return animalCount;
    }

    public HashMap<Integer, ToDo> getSchedule() {
        return schedule;
    }

    public String printSchedule() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            sb.append(i).append(":00\n").append(schedule.get(i)).append("\n");
        }
        return sb.toString();
    }

    public void printScheduleToFile() {
        FileWriter out = null;
        String outName = "schedule.txt";

        try {
            out = new FileWriter(outName);
            out.write(printSchedule());
        } catch (IOException e) {
            System.out.println("Error writing to file " + outName);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    System.out.println("Error closing file " + outName);
                }
            }
        }
        
    }

}